/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emc1transport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.undergroundminer3.uee4.emc1transport.Emc1Handler.Emc1Receiver;
import me.undergroundminer3.uee4.emctransport.EmcPipeTypes;
import me.undergroundminer3.uee4.network2.ChannelHandler;
import me.undergroundminer3.uee4.network2.IEmcPacketAble;
import me.undergroundminer3.uee4.network2.PacketEmcPipeUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.core.DefaultProps;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.network.PacketPowerUpdate;

public class PipeTransportEmc1 extends PipeTransport implements IEmcPacketAble {

	private static final short MAX_DISPLAY = 100;
	private static final int DISPLAY_SMOOTHING = 10;
	private static final int OVERLOAD_TICKS = 60;
	public static final Map<Class<? extends Pipe>, Integer> emc1Capacities = new HashMap<Class<? extends Pipe>, Integer>();

	static {
		emc1Capacities.put(PipeEmc1Cobblestone.class, 8);
		emc1Capacities.put(PipeEmc1Stone.class, 16);
		emc1Capacities.put(PipeEmc1Wood.class, 32);
		emc1Capacities.put(PipeEmc1Quartz.class, 64);
		emc1Capacities.put(PipeEmc1Gold.class, 256);
		emc1Capacities.put(PipeEmc1Diamond.class, 64);
	}
	private boolean needsInit = true;
	private TileEntity[] tiles = new TileEntity[6];
	public float[] displayEmc1 = new float[6];
	private float[] prevDisplayEmc1 = new float[6];
	public short[] clientDisplayEmc1 = new short[6];
	public int overload;
	private int[] emc1Query = new int[6];
	public int[] nextEmc1Query = new int[6];
	private long currentDate;
	private float[] internalEmc1 = new float[6];
	public float[] internalNextEmc1 = new float[6];
	public int maxEmc1 = 8;
	private double highestEmc1;
	SafeTimeTracker tracker = new SafeTimeTracker();

	public PipeTransportEmc1() {
		for (int i = 0; i < 6; ++i) {
			emc1Query[i] = 0;
		}
		
	}
	
	

	@Override
	public PipeType getPipeType() {
		return EmcPipeTypes.EMC1;
	}

	public void initFromPipe(Class<? extends Pipe> pipeClass) {
		maxEmc1 = emc1Capacities.get(pipeClass);
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		if (tile instanceof TileGenericPipe) {
			Pipe pipe2 = ((TileGenericPipe) tile).pipe;
			if (BlockGenericPipe.isValid(pipe2) && !(pipe2.transport instanceof PipeTransportEmc1))
				return false;
			return true;
		}

		if (tile instanceof IEmc1Receptor) {
			IEmc1Receptor receptor = (IEmc1Receptor) tile;
			Emc1Receiver receiver = receptor.getEmc1Receiver(side.getOpposite());
			if (receiver != null && receiver.getType().canReceiveFromPipes())
				return true;
		}

		if (container.pipe instanceof PipeEmc1Wood && tile instanceof IEmc1Emitter) {
			IEmc1Emitter emitter = (IEmc1Emitter) tile;
			if (emitter.canEmitEmc1From(side.getOpposite()))
				return true;
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		updateTiles();
	}

	private void updateTiles() {
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = container.getTile(side);
			if (container.isPipeConnected(side)) {
				tiles[side.ordinal()] = tile;
			} else {
				tiles[side.ordinal()] = null;
				internalEmc1[side.ordinal()] = 0;
				internalNextEmc1[side.ordinal()] = 0;
				displayEmc1[side.ordinal()] = 0;
			}
		}
	}

	private void init() {
		if (needsInit) {
			needsInit = false;
			updateTiles();
		}
	}

	@Override
	public void updateEntity() {
		if (container.getWorldObj().isRemote) {
			return;
		}

		step();

		init();

		// Send the emc1 to nearby pipes who requested it

		System.arraycopy(displayEmc1, 0, prevDisplayEmc1, 0, 6);
		Arrays.fill(displayEmc1, 0.0F);

		for (int i = 0; i < 6; ++i) {
			if (internalEmc1[i] > 0) {
				float totalEmc1Query = 0;

				for (int j = 0; j < 6; ++j) {
					if (j != i && emc1Query[j] > 0)
						if (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IEmc1Receptor) {
							totalEmc1Query += emc1Query[j];
						}
				}

				for (int j = 0; j < 6; ++j) {
					if (j != i && emc1Query[j] > 0) {
						float watts = 0.0F;

						Emc1Handler.Emc1Receiver prov = getReceiverOnSide(ForgeDirection.VALID_DIRECTIONS[j]);
						if (prov != null && prov.emc1Request() > 0) {
							watts = (internalEmc1[i] / totalEmc1Query) * emc1Query[j];
							watts = (float) prov.receiveEmc1(Emc1Handler.Type.PIPE, watts, ForgeDirection.VALID_DIRECTIONS[j].getOpposite());
							internalEmc1[i] -= watts;
						} else if (tiles[j] instanceof TileGenericPipe) {
							watts = (internalEmc1[i] / totalEmc1Query) * emc1Query[j];
							TileGenericPipe nearbyTile = (TileGenericPipe) tiles[j];

							PipeTransportEmc1 nearbyTransport = (PipeTransportEmc1) nearbyTile.pipe.transport;

							watts = nearbyTransport.receiveEmc1(ForgeDirection.VALID_DIRECTIONS[j].getOpposite(), watts);
							internalEmc1[i] -= watts;
						}

						displayEmc1[j] += watts;
						displayEmc1[i] += watts;
					}
				}
			}
		}

		highestEmc1 = 0;
		for (int i = 0; i < 6; i++) {
			displayEmc1[i] = (prevDisplayEmc1[i] * (DISPLAY_SMOOTHING - 1.0F) + displayEmc1[i]) / DISPLAY_SMOOTHING;
			if (displayEmc1[i] > highestEmc1) {
				highestEmc1 = displayEmc1[i];
			}
		}

		overload += highestEmc1 > maxEmc1 * 0.95 ? 1 : -1;
		if (overload < 0) {
			overload = 0;
		}
		if (overload > OVERLOAD_TICKS) {
			overload = OVERLOAD_TICKS;
		}

		// Compute the tiles requesting energy that are not emc pipes

		for (int i = 0; i < 6; ++i) {
			Emc1Receiver prov = getReceiverOnSide(ForgeDirection.VALID_DIRECTIONS[i]);
			if (prov != null) {
				float request = (float) prov.emc1Request();

				if (request > 0) {
					requestEmc1(ForgeDirection.VALID_DIRECTIONS[i], request);
				}
			}
		}

		// Sum the amount of energy requested on each side

		int[] transferQuery = new int[6];

		for (int i = 0; i < 6; ++i) {
			transferQuery[i] = 0;

			for (int j = 0; j < 6; ++j) {
				if (j != i) {
					transferQuery[i] += emc1Query[j];
				}
			}

			transferQuery[i] = Math.min(transferQuery[i], maxEmc1);
		}

		// Transfer the requested energy to nearby pipes

		for (int i = 0; i < 6; ++i) {
			if (transferQuery[i] != 0) {
				if (tiles[i] != null) {
					TileEntity entity = tiles[i];

					if (entity instanceof TileGenericPipe) {
						TileGenericPipe nearbyTile = (TileGenericPipe) entity;

						if (nearbyTile.pipe == null) {
							continue;
						}

						PipeTransportEmc1 nearbyTransport = (PipeTransportEmc1) nearbyTile.pipe.transport;
						nearbyTransport.requestEmc1(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), transferQuery[i]);
					}
				}
			}
		}

		if (tracker.markTimeIfDelay(container.getWorldObj(), 2 * BuildCraftCore.updateFactor)) {
			PacketEmcPipeUpdate packet = new PacketEmcPipeUpdate(container.xCoord, container.yCoord, container.zCoord);

			double displayFactor = MAX_DISPLAY / 1024.0;
			for (int i = 0; i < clientDisplayEmc1.length; i++) {
				clientDisplayEmc1[i] = (short) (displayEmc1[i] * displayFactor + .9999);
			}

			packet.displayPower = clientDisplayEmc1;
			packet.overload = isOverloaded();
			ChannelHandler.sendToPlayers(packet, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord, DefaultProps.PIPE_CONTENTS_RENDER_DIST);
		}

	}

	private Emc1Receiver getReceiverOnSide(ForgeDirection side) {
		TileEntity tile = tiles[side.ordinal()];
		if (!(tile instanceof IEmc1Receptor))
			return null;
		IEmc1Receptor receptor = (IEmc1Receptor) tile;
		Emc1Receiver receiver = receptor.getEmc1Receiver(side.getOpposite());
		if (receiver == null)
			return null;
		if (!receiver.getType().canReceiveFromPipes())
			return null;
		return receiver;
	}

	public boolean isOverloaded() {
		return overload >= OVERLOAD_TICKS;
	}

	private void step() {
		if (currentDate != container.getWorldObj().getTotalWorldTime()) {
			currentDate = container.getWorldObj().getTotalWorldTime();

			emc1Query = nextEmc1Query;
			nextEmc1Query = new int[6];

			float[] next = internalEmc1;
			internalEmc1 = internalNextEmc1;
			internalNextEmc1 = next;
//			for (int i = 0; i < powerQuery.length; i++) {
//				int sum = 0;
//				for (int j = 0; j < powerQuery.length; j++) {
//					if (i != j) {
//						sum += powerQuery[j];
//					}
//				}
//				if (sum == 0 && internalNextPower[i] > 0) {
//					internalNextPower[i] -= 1;
//				}
//			}
		}
	}

	/**
	 * Do NOT ever call this from outside <strike>Buildcraft</strike> UEE4. It is NOT part of the API.
	 * All power input MUST go through designated input pipes, such as Wooden
	 * Power Pipes or a subclass thereof.
	 */
	public float receiveEmc1(ForgeDirection from, float val) {
		step();
		if (this.container.pipe instanceof IPipeTransportEmc1Hook) {
			float ret = ((IPipeTransportEmc1Hook) this.container.pipe).receiveEmc1(from, val);
			if (ret >= 0)
				return ret;
		}
		int side = from.ordinal();
		if (internalNextEmc1[side] > maxEmc1)
			return 0;

		internalNextEmc1[side] += val;

		if (internalNextEmc1[side] > maxEmc1) {
			val -= internalNextEmc1[side] - maxEmc1;
			internalNextEmc1[side] = maxEmc1;
			if (val < 0)
				val = 0;
		}
		return val;
	}

	public void requestEmc1(final ForgeDirection from, final float amount) {
		step();
		if (this.container.pipe instanceof IPipeTransportEmc1Hook) {
			nextEmc1Query[from.ordinal()] += ((IPipeTransportEmc1Hook) this.container.pipe).requestEmc1(from, amount);
		} else {
			nextEmc1Query[from.ordinal()] += amount;
		}
	}

	@Override
	public void initialize() {
		currentDate = container.getWorldObj().getTotalWorldTime();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			emc1Query[i] = nbttagcompound.getInteger("emc1Query[" + i + "]");
			nextEmc1Query[i] = nbttagcompound.getInteger("nextEmc1Query[" + i + "]");
			internalEmc1[i] = (float) nbttagcompound.getDouble("internalEmc1[" + i + "]");
			internalNextEmc1[i] = (float) nbttagcompound.getDouble("internalNextEmc1[" + i + "]");
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			nbttagcompound.setInteger("emc1Query[" + i + "]", emc1Query[i]);
			nbttagcompound.setInteger("nextEmc1Query[" + i + "]", nextEmc1Query[i]);
			nbttagcompound.setDouble("internalEmc1[" + i + "]", internalEmc1[i]);
			nbttagcompound.setDouble("internalNextEmc1[" + i + "]", internalNextEmc1[i]);
		}
	}

	public boolean isTriggerActive(ITrigger trigger) {
		return false;
	}

	/**
	 * Client-side handler for receiving emc1 updates from the server;
	 *
	 * @param packetPower
	 */
	@Override
	public void handlePowerPacket(PacketEmcPipeUpdate packetPower) {
		clientDisplayEmc1 = packetPower.displayPower;
		overload = packetPower.overload ? OVERLOAD_TICKS : 0;
	}

	/**
	 * This can be use to provide a rough estimate of how much emc1 is flowing
	 * through a pipe. Measured in <strike>MJ</strike> EMC/t.
	 *
	 * @return <strike>MJ</strike> EMC/t
	 */
	public double getCurrentEmc1TransferRate() {
		return highestEmc1;
	}

	/**
	 * This can be use to provide a rough estimate of how much emc is
	 * contained in a pipe. Measured in <strike>MJ</strike> EMC.
	 * 
	 * Max should be around (throughput * internalPower.length * 2), ie 112 MJ for a Cobblestone Pipe. #TODO
	 *
	 * @return <strike>MJ</strike> EMC
	 */
	public double getCurrentEmc1Amount() {
		double amount = 0.0;
		for (double d : internalEmc1) {
			amount += d;
		}
		for (double d : internalNextEmc1) {
			amount += d;
		}
		return amount;
	}
}
