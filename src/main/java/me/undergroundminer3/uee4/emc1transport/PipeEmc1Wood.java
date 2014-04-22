/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emc1transport;

import buildcraft.api.core.IIconProvider;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.Pipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.emc1transport.Emc1Handler.Emc1Receiver;
import me.undergroundminer3.uee4.emctransport.EmcPipeIconProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class PipeEmc1Wood extends Pipe<PipeTransportEmc1> implements IEmc1Receptor, IPipeTransportEmc1Hook {

	private Emc1Handler emc1Handler;
	private boolean[] emc1Sources = new boolean[6];
	private boolean full;

	public PipeEmc1Wood(Item item) {
		super(new PipeTransportEmc1(), item);

		emc1Handler = new Emc1Handler(this, Emc1Handler.Type.PIPE);
		initEmc1Provider();
		transport.initFromPipe(getClass());
	}

	private void initEmc1Provider() {
		emc1Handler.configure(2, 500, 1, 1500);
		emc1Handler.configureEmc1Perdition(1, 10);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return EmcPipeIconProvider.INSTANCE;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return EmcPipeIconProvider.TYPE.PipeEmc1Wood_Standard.ordinal();
	}

	@Override
	public Emc1Receiver getEmc1Receiver(ForgeDirection side) {
		return emc1Handler.getEmc1Receiver();
	}

	@Override
	public void doWork(Emc1Handler workProvider) {
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (container.getWorldObj().isRemote)
			return;

		if (emc1Handler.getEmc1Stored() <= 0)
			return;

		int sources = 0;
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!container.isPipeConnected(o)) {
				emc1Sources[o.ordinal()] = false;
				continue;
			}
			if (emc1Handler.isEmc1Source(o)) {
				emc1Sources[o.ordinal()] = true;
			}
			if (emc1Sources[o.ordinal()]) {
				sources++;
			}
		}

		if (sources <= 0) {
			emc1Handler.useEmc1(5, 5, true);
			return;
		}

		double emc1ToRemove;

		if (emc1Handler.getEmc1Stored() > 40) {
			emc1ToRemove = emc1Handler.getEmc1Stored() / 40 + 4;
		} else if (emc1Handler.getEmc1Stored() > 10) {
			emc1ToRemove = emc1Handler.getEmc1Stored() / 10;
		} else {
			emc1ToRemove = 1;
		}
		emc1ToRemove /= (float) sources;

		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if (!emc1Sources[o.ordinal()])
				continue;

			float emc1Usable = (float) emc1Handler.useEmc1(0, emc1ToRemove, false);

			float emc1Sent = transport.receiveEmc1(o, emc1Usable);
			if (emc1Sent > 0) {
				emc1Handler.useEmc1(0, emc1Sent, true);
			}
		}
	}

	public boolean requestsEmc1() {
		if (full) {
			boolean request = emc1Handler.getEmc1Stored() < emc1Handler.getMaxEmc1Stored() / 2;
			if (request) {
				full = false;
			}
			return request;
		}
		full = emc1Handler.getEmc1Stored() >= emc1Handler.getMaxEmc1Stored() - 10;
		return !full;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		emc1Handler.writeToNBT(data);
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			data.setBoolean("emc1Sources[" + i + "]", emc1Sources[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		emc1Handler.readFromNBT(data);
		initEmc1Provider();
		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			emc1Sources[i] = data.getBoolean("emc1Sources[" + i + "]");
		}
	}

	@Override
	public float receiveEmc1(ForgeDirection from, float val) {
		return -1;
	}

	@Override
	public float requestEmc1(ForgeDirection from, float amount) {
		if (container.getTile(from) instanceof IPipeTile) {
			return amount;
		}
		return 0;
	}
}
