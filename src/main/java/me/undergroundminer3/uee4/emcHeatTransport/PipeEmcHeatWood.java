/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcHeatTransport;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.Pipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.emcHeatTransport.EmcHeatHandler.EmcHeatReceiver;
import me.undergroundminer3.uee4.emctransport.EmcPipeIconProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class PipeEmcHeatWood extends Pipe<PipeTransportEmcHeat> implements IEmcHeatReceptor, IPipeTransportEmcHeatHook {

	private EmcHeatHandler emcHeatHandler;
	private boolean[] emcHeatSources = new boolean[6];
	private boolean full;

	public PipeEmcHeatWood(final Item item) {
		super(new PipeTransportEmcHeat(), item);

		emcHeatHandler = new EmcHeatHandler(this, EmcHeatHandler.Type.PIPE);
		initEmcHeatProvider();
		transport.initFromPipe(getClass());
	}

	private void initEmcHeatProvider() {
		emcHeatHandler.configure(2, 500, 1, 1500);
		emcHeatHandler.configureEmcHeatPerdition(1, 10);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return EmcPipeIconProvider.INSTANCE;
	}

	@Override
	public int getIconIndex(final ForgeDirection direction) {
		return EmcPipeIconProvider.TYPE.PipeEmcHeatWood_Standard.ordinal();
	}

	@Override
	public EmcHeatReceiver getEmcHeatReceiver(final ForgeDirection side) {
		return emcHeatHandler.getEmcHeatReceiver();
	}

	@Override
	public void doWork(final EmcHeatHandler workProvider) {
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (container.getWorldObj().isRemote)
			return;

		if (emcHeatHandler.getEmcHeatStored() <= 0)
			return;

		int sources = 0;
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!container.isPipeConnected(o)) {
				emcHeatSources[o.ordinal()] = false;
				continue;
			}
			if (emcHeatHandler.isEmcHeatSource(o)) {
				emcHeatSources[o.ordinal()] = true;
			}
			if (emcHeatSources[o.ordinal()]) {
				sources++;
			}
		}

		if (sources <= 0) {
			emcHeatHandler.useEmcHeat(5, 5, true);
			return;
		}

		double emcHeatToRemove;

		if (emcHeatHandler.getEmcHeatStored() > 40) {
			emcHeatToRemove = emcHeatHandler.getEmcHeatStored() / 40 + 4;
		} else if (emcHeatHandler.getEmcHeatStored() > 10) {
			emcHeatToRemove = emcHeatHandler.getEmcHeatStored() / 10;
		} else {
			emcHeatToRemove = 1;
		}
		emcHeatToRemove /= (float) sources;

		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!emcHeatSources[o.ordinal()])
				continue;

			float emcHeatUsable = (float) emcHeatHandler.useEmcHeat(0, emcHeatToRemove, false);

			float emcHeatSent = transport.receiveEmcHeat(o, emcHeatUsable);
			if (emcHeatSent > 0) {
				emcHeatHandler.useEmcHeat(0, emcHeatSent, true);
			}
		}
	}

	public boolean requestsEmcHeat() {
		if (full) {
			boolean request = emcHeatHandler.getEmcHeatStored() < emcHeatHandler.getMaxEmcHeatStored() / 2;
			if (request) {
				full = false;
			}
			return request;
		}
		full = emcHeatHandler.getEmcHeatStored() >= emcHeatHandler.getMaxEmcHeatStored() - 10;
		return !full;
	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);
		emcHeatHandler.writeToNBT(data);
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			data.setBoolean("emcHeatSources[" + i + "]", emcHeatSources[i]);
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);
		emcHeatHandler.readFromNBT(data);
		initEmcHeatProvider();
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			emcHeatSources[i] = data.getBoolean("emcHeatSources[" + i + "]");
		}
	}

	@Override
	public float receiveEmcHeat(ForgeDirection from, float val) {
		return -1;
	}

	@Override
	public float requestEmcHeat(ForgeDirection from, float amount) {
		if (container.getTile(from) instanceof IPipeTile) {
			return amount;
		}
		return 0;
	}
}
