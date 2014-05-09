package me.undergroundminer3.uee4.emctransport;

import me.undergroundminer3.uee4.emcAirTransport.EmcAirHandler;
import me.undergroundminer3.uee4.emcAirTransport.IEmcAirReceptor;
import me.undergroundminer3.uee4.emcHeatTransport.EmcHeatHandler;
import me.undergroundminer3.uee4.emcHeatTransport.IEmcHeatReceptor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TileGenericPipe;

public final class EmcPipeUtil {

	private EmcPipeUtil() {};

	//MJPIPE
	
	public static final PowerHandler.PowerReceiver getPowerReceiver(final TileEntity tile, final ForgeDirection side) {
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

	public static final void doWork(final TileEntity tile, final PowerHandler workProvider) {
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
	
	public static final EmcAirHandler.EmcAirReceiver getEmcAirReceiver(final TileEntity tile, final ForgeDirection side) {
		if (tile instanceof IEmcAirReceptor) {
			return ((IEmcAirReceptor) tile).getEmcAirReceiver(side);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmcAirReceptor)
				return ((IEmcAirReceptor) pipeContainer.pipe).getEmcAirReceiver(null);
			else
				return null;
		} else {
			return null;
		}
	}

	public static final void doWork(final TileEntity tile, final EmcAirHandler workProvider) {
		if (tile instanceof IEmcAirReceptor) {
			((IEmcAirReceptor) tile).doWork(workProvider);
		} else if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipeContainer = (TileGenericPipe) tile;
			if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmcAirReceptor)
				((IEmcAirReceptor) pipeContainer.pipe).doWork(workProvider);
		} /*else {
			//do nothing
		}*/
	}
	
	//EMC2PIPE
	
		public static final EmcHeatHandler.EmcHeatReceiver getEmcHeatReceiver(final TileEntity tile, final ForgeDirection side) {
			if (tile instanceof IEmcHeatReceptor) {
				return ((IEmcHeatReceptor) tile).getEmcHeatReceiver(side);
			} else if (tile instanceof TileGenericPipe) {
				TileGenericPipe pipeContainer = (TileGenericPipe) tile;
				if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmcHeatReceptor)
					return ((IEmcHeatReceptor) pipeContainer.pipe).getEmcHeatReceiver(null);
				else
					return null;
			} else {
				return null;
			}
		}

		public static final void doWork(final TileEntity tile, final EmcHeatHandler workProvider) {
			if (tile instanceof IEmcHeatReceptor) {
				((IEmcHeatReceptor) tile).doWork(workProvider);
			} else if (tile instanceof TileGenericPipe) {
				TileGenericPipe pipeContainer = (TileGenericPipe) tile;
				if (BlockGenericPipe.isValid(pipeContainer.pipe) && pipeContainer.pipe instanceof IEmcHeatReceptor)
					((IEmcHeatReceptor) pipeContainer.pipe).doWork(workProvider);
			} /*else {
				//do nothing
			}*/
		}
}
