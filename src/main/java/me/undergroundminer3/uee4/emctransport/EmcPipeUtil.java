package me.undergroundminer3.uee4.emctransport;

import me.undergroundminer3.uee4.emc1transport.Emc1Handler;
import me.undergroundminer3.uee4.emc1transport.IEmc1Receptor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TileGenericPipe;

public final class EmcPipeUtil {

	private EmcPipeUtil() {};

	//MJPIPE
	
	public static PowerHandler.PowerReceiver getPowerReceiver(final TileEntity tile, final ForgeDirection side) {
		if (tile instanceof IPowerReceptor) {
			return ((IPowerReceptor) tile).getPowerReceiver(side);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IPowerReceptor)
				return ((IPowerReceptor) pipeContainer.pipe).getPowerReceiver(null);
			else
				return null;
		} else {
			return null;
		}
	}

	public static void doWork(final TileEntity tile, final PowerHandler workProvider) {
		if (tile instanceof IPowerReceptor) {
			((IPowerReceptor) tile).doWork(workProvider);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IPowerReceptor)
				((IPowerReceptor) pipeContainer.pipe).doWork(workProvider);
		} /*else {
			//do nothing
		}*/
	}
	
	//EMC1PIPE
	
	public static Emc1Handler.Emc1Receiver getEmc1Receiver(final TileEntity tile, final ForgeDirection side) {
		if (tile instanceof IEmc1Receptor) {
			return ((IEmc1Receptor) tile).getEmc1Receiver(side);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmc1Receptor)
				return ((IEmc1Receptor) pipeContainer.pipe).getEmc1Receiver(null);
			else
				return null;
		} else {
			return null;
		}
	}

	public static void doWork(final TileEntity tile, final Emc1Handler workProvider) {
		if (tile instanceof IEmc1Receptor) {
			((IEmc1Receptor) tile).doWork(workProvider);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmc1Receptor)
				((IEmc1Receptor) pipeContainer.pipe).doWork(workProvider);
		} /*else {
			//do nothing
		}*/
	}
}
