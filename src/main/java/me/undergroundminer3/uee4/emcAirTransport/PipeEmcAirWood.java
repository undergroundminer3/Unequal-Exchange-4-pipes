/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirTransport;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.Pipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.emcAirTransport.EmcAirHandler.EmcAirReceiver;
import me.undergroundminer3.uee4.emctransport.EmcPipeIconProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class PipeEmcAirWood extends Pipe<PipeTransportEmcAir> implements IEmcAirReceptor, IPipeTransportEmcAirHook {

	private EmcAirHandler emcAirHandler;
	private boolean[] emcAirSources = new boolean[6];
	private boolean full;

	public PipeEmcAirWood(final Item item) {
		super(new PipeTransportEmcAir(), item);

		emcAirHandler = new EmcAirHandler(this, EmcAirHandler.Type.PIPE);
		initEmcAirProvider();
		transport.initFromPipe(getClass());
	}

	private void initEmcAirProvider() {
		emcAirHandler.configure(2, 500, 1, 1500);
		emcAirHandler.configureEmcAirPerdition(1, 10);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return EmcPipeIconProvider.INSTANCE;
	}

	@Override
	public int getIconIndex(final ForgeDirection direction) {
		return EmcPipeIconProvider.TYPE.PipeAirWood_Standard.ordinal();
	}

	@Override
	public EmcAirReceiver getEmcAirReceiver(final ForgeDirection side) {
		return emcAirHandler.getEmcAirReceiver();
	}

	@Override
	public void doWork(final EmcAirHandler workProvider) {
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (container.getWorldObj().isRemote)
			return;

		if (emcAirHandler.getEmcAirStored() <= 0)
			return;

		int sources = 0;
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!container.isPipeConnected(o)) {
				emcAirSources[o.ordinal()] = false;
				continue;
			}
			if (emcAirHandler.isEmcAirSource(o)) {
				emcAirSources[o.ordinal()] = true;
			}
			if (emcAirSources[o.ordinal()]) {
				sources++;
			}
		}

		if (sources <= 0) {
			emcAirHandler.useEmcAir(5, 5, true);
			return;
		}

		double emcAirToRemove;

		if (emcAirHandler.getEmcAirStored() > 40) {
			emcAirToRemove = emcAirHandler.getEmcAirStored() / 40 + 4;
		} else if (emcAirHandler.getEmcAirStored() > 10) {
			emcAirToRemove = emcAirHandler.getEmcAirStored() / 10;
		} else {
			emcAirToRemove = 1;
		}
		emcAirToRemove /= (float) sources;

		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!emcAirSources[o.ordinal()])
				continue;

			float emcAirUsable = (float) emcAirHandler.useEmcAir(0, emcAirToRemove, false);

			float emcAirSent = transport.receiveEmcAir(o, emcAirUsable);
			if (emcAirSent > 0) {
				emcAirHandler.useEmcAir(0, emcAirSent, true);
			}
		}
	}

	public boolean requestsEmcAir() {
		if (full) {
			boolean request = emcAirHandler.getEmcAirStored() < emcAirHandler.getMaxEmcAirStored() / 2;
			if (request) {
				full = false;
			}
			return request;
		}
		full = emcAirHandler.getEmcAirStored() >= emcAirHandler.getMaxEmcAirStored() - 10;
		return !full;
	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		emcAirHandler.writeToNBT(data);
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			data.setBoolean("emcAirSources[" + i + "]", emcAirSources[i]);
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		emcAirHandler.readFromNBT(data);
		initEmcAirProvider();
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			emcAirSources[i] = data.getBoolean("emcAirSources[" + i + "]");
		}
	}

	@Override
	public float receiveEmcAir(ForgeDirection from, float val) {
		return -1;
	}

	@Override
	public float requestEmcAir(ForgeDirection from, float amount) {
		if (container.getTile(from) instanceof IPipeTile) {
			return amount;
		}
		return 0;
	}
}
