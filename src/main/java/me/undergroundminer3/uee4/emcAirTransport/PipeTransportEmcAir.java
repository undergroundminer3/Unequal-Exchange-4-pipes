/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirTransport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.undergroundminer3.uee4.emcAirTransport.EmcAirHandler.EmcAirReceiver;
import me.undergroundminer3.uee4.emctransport.EmcPipeTypes;
import me.undergroundminer3.uee4.network2.ChannelHandler;
import me.undergroundminer3.uee4.network2.IEmcPacketAble;
import me.undergroundminer3.uee4.network2.PacketEmcPipeUpdate;
import me.undergroundminer3.uee4.pipesModded.IModdedPipeRenderable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.energy.EnergyAPI;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.core.DefaultProps;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public class PipeTransportEmcAir extends PipeTransport implements IEmcPacketAble, IModdedPipeRenderable {

	public static final Map<Class<? extends Pipe<?>>, Integer> emcAirCapacities = new HashMap<Class<? extends Pipe<?>>, Integer>();

	public static final short MAX_DISPLAY = 100;
	public static final int DISPLAY_SMOOTHING = 10;
	public static final int OVERLOAD_TICKS = 60;


	static {
		emcAirCapacities.put(PipeEmcAirCobblestone.class, 8);
		emcAirCapacities.put(PipeEmcAirStone.class, 16);
		emcAirCapacities.put(PipeEmcAirWood.class, 32);
		emcAirCapacities.put(PipeEmcAirQuartz.class, 64);
		emcAirCapacities.put(PipeEmcAirGold.class, 256);
		emcAirCapacities.put(PipeEmcAirDiamond.class, 64);
	}
	private boolean needsCreInit = true;
	protected TileEntity[] tiles = new TileEntity[6];
	public float[] displayPow = new float[6];
	protected float[] prevDisplayPow = new float[6];
	protected int[] powQuery = new int[6];
	public int[] nextPowQuery = new int[6];
	protected long currentDate;
	protected float[] internalPow = new float[6];
	public float[] internalNextPow = new float[6];
	public int maxPow = 8;
	protected double highestPow;

	public short[] clientDisplayPow = new short[6];
	public int overload = 0;

	public float[] movementStage = new float[] {0, 0, 0};

	SafeTimeTracker tracker = new SafeTimeTracker();

	public PipeTransportEmcAir() {
		for (int i = 0; i < 6; ++i) {
			powQuery[i] = 0;
		}

		for (int i = 0; i < 3; ++i) {
			movementStage[i] = (float) Math.random();
		}
	}

	@Override
	public PipeType getPipeType() {
		return EmcPipeTypes.EmcAir;
	}

	public void initFromPipe(Class<? extends Pipe<?>> pipeClass) {
		maxPow = emcAirCapacities.get(pipeClass);
	}

	@Override
	public boolean canPipeConnect(final TileEntity tile, final ForgeDirection side) {
		if (tile instanceof TileGenericPipe) {
			Pipe<?> pipe2 = ((TileGenericPipe) tile).pipe;
			if (BlockGenericPipe.isValid(pipe2) && !(pipe2.transport instanceof PipeTransportEmcAir)) {
				return false;
			}
			return true;
		}

		if (tile instanceof IEmcAirReceptor) {
			IEmcAirReceptor receptor = (IEmcAirReceptor) tile;
			EmcAirReceiver receiver = receptor.getEmcAirReceiver(side.getOpposite());
			if (receiver != null && receiver.getType().canReceiveFromPipes()) {
				return true;
			}
		}

		if (container.pipe instanceof PipeEmcAirWood && tile instanceof IEmcAirEmitter) {
			IEmcAirEmitter emitter = (IEmcAirEmitter) tile;
			if (emitter.canEmitEmcAirFrom(side.getOpposite())) {
				return true;
			}
		}

		if (EnergyAPI.getBattery(tile, EnergyAPI.batteryChannelMJ) != null) {
			return true;
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		updateTiles();
	}

	protected void updateTiles() {
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = container.getTile(side);
			if (container.isPipeConnected(side)) {
				tiles[side.ordinal()] = tile;
			} else {
				tiles[side.ordinal()] = null;
				internalPow[side.ordinal()] = 0;
				internalNextPow[side.ordinal()] = 0;
				displayPow[side.ordinal()] = 0;
			}
		}
	}

	protected void initCre() {
		if (needsCreInit) {
			needsCreInit = false;
			updateTiles();
		}
	}

	@Override
	public void updateEntity() {
		if (container.getWorldObj().isRemote) {
			// updating movement stage. We're only carrying the movement on half
			// the things. This is purely for animation purpose.

			for (int i = 0; i < 6; i += 2) {
				movementStage [i / 2] = (movementStage [i / 2] + 0.01F) % 1.0F;
			}
			return;
		}

		step();

		initCre();

		// Send the power to nearby pipes who requested it

		System.arraycopy(displayPow, 0, prevDisplayPow, 0, 6);
		Arrays.fill(displayPow, 0.0F);

		for (int i = 0; i < 6; ++i) {
			if (internalPow[i] > 0) {
				float totalPowQuery = 0;

				for (int j = 0; j < 6; ++j) {
					if (j != i && powQuery[j] > 0)
						if (tiles[j] instanceof TileGenericPipe || tiles[j] instanceof IEmcAirReceptor) {
							totalPowQuery += powQuery[j];
						}
				}

				for (int j = 0; j < 6; ++j) {
					if (j != i && powQuery[j] > 0) {
						float watts = 0.0F;

						EmcAirHandler.EmcAirReceiver prov = getReceiverOnSide(ForgeDirection.VALID_DIRECTIONS[j]);
						if (prov != null && prov.emcAirRequest() > 0) {
							watts = (internalPow[i] / totalPowQuery) * powQuery[j];
							watts = (float) prov.receiveEmcAir(EmcAirHandler.Type.PIPE, watts, ForgeDirection.VALID_DIRECTIONS[j].getOpposite());
							internalPow[i] -= watts;
						} else if (tiles[j] instanceof TileGenericPipe) {
							watts = (internalPow[i] / totalPowQuery) * powQuery[j];
							TileGenericPipe nearbyTile = (TileGenericPipe) tiles[j];

							PipeTransportEmcAir nearbyTransport = (PipeTransportEmcAir) nearbyTile.pipe.transport;

							watts = nearbyTransport.receiveEmcAir(ForgeDirection.VALID_DIRECTIONS[j].getOpposite(), watts);
							internalPow[i] -= watts;
						}

						displayPow[j] += watts;
						displayPow[i] += watts;
					}
				}
			}
		}

		highestPow = 0;
		for (int i = 0; i < 6; i++) {
			displayPow[i] = (prevDisplayPow[i] * (DISPLAY_SMOOTHING - 1.0F) + displayPow[i]) / DISPLAY_SMOOTHING;
			if (displayPow[i] > highestPow) {
				highestPow = displayPow[i];
			}
		}

		overload += highestPow > maxPow * 0.95 ? 1 : -1;
		if (overload < 0) {
			overload = 0;
		}
		if (overload > OVERLOAD_TICKS) {
			overload = OVERLOAD_TICKS;
		}

		// Compute the tiles requesting energy that are not emc pipes

		for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			final EmcAirReceiver prov = getReceiverOnSide(dir);
			if (prov != null) {
				float request = (float) prov.emcAirRequest();

				if (request > 0) {
					requestEmcAir(dir, request);
				}
			}
		}

		// Sum the amount of energy requested on each side

		int[] transferQuery = new int[6];

		for (int i = 0; i < 6; ++i) {
			transferQuery[i] = 0;

			for (int j = 0; j < 6; ++j) {
				if (j != i) {
					transferQuery[i] += powQuery[j];
				}
			}

			transferQuery[i] = Math.min(transferQuery[i], maxPow);
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

						PipeTransportEmcAir nearbyTransport = (PipeTransportEmcAir) nearbyTile.pipe.transport;
						nearbyTransport.requestEmcAir(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), transferQuery[i]);
					}
				}
			}
		}

		if (tracker.markTimeIfDelay(container.getWorldObj(), 2 * BuildCraftCore.updateFactor)) {
			PacketEmcPipeUpdate packet = new PacketEmcPipeUpdate(container.xCoord, container.yCoord, container.zCoord);

			double displayFactor = MAX_DISPLAY / 1024.0;
			for (int i = 0; i < clientDisplayPow.length; i++) {
				clientDisplayPow[i] = (short) (displayPow[i] * displayFactor + .9999);
			}

			packet.displayPower = clientDisplayPow;
			packet.overload = isOverloaded();
			ChannelHandler.sendToPlayers(packet, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord, DefaultProps.PIPE_CONTENTS_RENDER_DIST);
		}

	}

	protected EmcAirReceiver getReceiverOnSide(final ForgeDirection side) {
		final TileEntity tile = tiles[side.ordinal()];
		if (!(tile instanceof IEmcAirReceptor)) {
			return null;
		}
		final IEmcAirReceptor receptor = (IEmcAirReceptor) tile;
		final EmcAirReceiver receiver = receptor.getEmcAirReceiver(side.getOpposite());
		if (receiver == null)
			return null;
		if (!receiver.getType().canReceiveFromPipes())
			return null;
		return receiver;
	}

	public boolean isOverloaded() {
		return overload >= OVERLOAD_TICKS;
	}

	protected void step() {
		if (currentDate != container.getWorldObj().getTotalWorldTime()) {
			currentDate = container.getWorldObj().getTotalWorldTime();

			powQuery = nextPowQuery;
			nextPowQuery = new int[6];

			float[] next = internalPow;
			internalPow = internalNextPow;
			internalNextPow = next;
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
	public float receiveEmcAir(final ForgeDirection from, /*var*/ float val) {
		step();
		if (this.container.pipe instanceof IPipeTransportEmcAirHook) {
			float ret = ((IPipeTransportEmcAirHook) this.container.pipe).receiveEmcAir(from, val);
			if (ret >= 0) {
				return ret;
			}
		}
		int side = from.ordinal();
		if (internalNextPow[side] > maxPow) {
			return 0;
		}

		internalNextPow[side] += val;

		if (internalNextPow[side] > maxPow) {
			val -= internalNextPow[side] - maxPow;
			internalNextPow[side] = maxPow;
			if (val < 0) {
				val = 0;
			}
		}
		return val;
	}

	public void requestEmcAir(final ForgeDirection from, final float amount) {
		step();
		if (this.container.pipe instanceof IPipeTransportEmcAirHook) {
			nextPowQuery[from.ordinal()] += ((IPipeTransportEmcAirHook) this.container.pipe).requestEmcAir(from, amount);
		} else {
			nextPowQuery[from.ordinal()] += amount;
		}
	}

	@Override
	public void initialize() {
		currentDate = container.getWorldObj().getTotalWorldTime();
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			powQuery[i] = nbttagcompound.getInteger("emcAirQuery[" + i + "]");
			nextPowQuery[i] = nbttagcompound.getInteger("nextEmcAirQuery[" + i + "]");
			internalPow[i] = (float) nbttagcompound.getDouble("internalEmcAir[" + i + "]");
			internalNextPow[i] = (float) nbttagcompound.getDouble("internalNextEmcAir[" + i + "]");
		}

	}

	@Override
	public void writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		for (int i = 0; i < 6; ++i) {
			nbttagcompound.setInteger("emcAirQuery[" + i + "]", powQuery[i]);
			nbttagcompound.setInteger("nextEmcAirQuery[" + i + "]", nextPowQuery[i]);
			nbttagcompound.setDouble("internalEmcAir[" + i + "]", internalPow[i]);
			nbttagcompound.setDouble("internalNextEmcAir[" + i + "]", internalNextPow[i]);
		}
	}

	public boolean isTriggerActive(final ITrigger trigger) {
		return false;
	}

	/**
	 * Client-side handler for receiving emc updates from the server;
	 *
	 * @param packetPower
	 */
	@Override
	public void handlePowerPacket(final PacketEmcPipeUpdate packetPower) {
		clientDisplayPow[0] = packetPower.displayPower[0];
		clientDisplayPow[1] = packetPower.displayPower[1];
		clientDisplayPow[2] = packetPower.displayPower[2];
		clientDisplayPow[3] = packetPower.displayPower[3];
		clientDisplayPow[4] = packetPower.displayPower[4];
		clientDisplayPow[5] = packetPower.displayPower[5];

		overload = packetPower.overload ? OVERLOAD_TICKS : 0;
	}

	/**
	 * This can be use to provide a rough estimate of how much emc is flowing
	 * through a pipe. Measured in <strike>MJ</strike> EMC/t.
	 *
	 * @return <strike>MJ</strike> EMC/t
	 */
	public double getCurrentEmcAirTransferRate() {
		return highestPow;
	}

	/**
	 * This can be use to provide a rough estimate of how much emc is
	 * contained in a pipe. Measured in <strike>MJ</strike> EMC.
	 * 
	 * Max should be around (throughput * internalPower.length * 2), ie 112 MJ for a Cobblestone Pipe. #TODO
	 *
	 * @return <strike>MJ</strike> EMC
	 */
	public double getCurrentEmcAirAmount() {
		double amount = 0.0;
		for (double d : internalPow) {
			amount += d;
		}
		for (double d : internalNextPow) {
			amount += d;
		}
		return amount;
	}



	@Override
	public short[] getClientDisplayPower() {
		return clientDisplayPow;
	}



	@Override
	public int getOverload() {
		return overload;
	}
	
	public float getPistonStage(final int i) {
		if (movementStage [i] < 0.5F) {
			return movementStage [i] * 2;
		} else {
			return 1 - (movementStage [i] - 0.5F) * 2;
		}
	}

	public double clearInstantEmcAir() {
		double amount = 0.0D;

		for (int i = 0; i < internalPow.length; ++i) {
			amount += internalPow[i];
			internalPow[i] = 0;
		}

		return amount;
	}
}
