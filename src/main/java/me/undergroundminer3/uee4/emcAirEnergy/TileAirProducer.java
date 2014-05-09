/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirEnergy;

import buildcraft.api.energy.EnergyAPI;
import buildcraft.api.energy.EnergyBattery;
import buildcraft.api.gates.IAction;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.core.IMachine;
import buildcraft.core.TileBuffer;
import buildcraft.core.network.PacketPayload;
import buildcraft.core.network.PacketUpdate;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

import me.undergroundminer3.uee4.abstacts.TileEntityEE_BC;
import me.undergroundminer3.uee4.config.ExplodeUtil;
import me.undergroundminer3.uee4.emcAirTransport.EmcAirHandler;
import me.undergroundminer3.uee4.emcAirTransport.IEmcAirEmitter;
import me.undergroundminer3.uee4.emcAirTransport.IEmcAirReceptor;
import me.undergroundminer3.uee4.emcAirTransport.EmcAirHandler.EmcAirReceiver;
import me.undergroundminer3.uee4.emcAirTransport2.AirHandler;
import me.undergroundminer3.uee4.emcAirTransport2.IAirReceptor;
import me.undergroundminer3.uee4.emctransport.EmcPipeUtil;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileAirProducer extends TileEntityEE_BC implements IMachine, IEmcAirReceptor, IEmcAirEmitter {

	private EmcAirHandler emcAirHandler;
	//	private TileBuffer[] tileBuffer = null;
	//	private SafeTimeTracker timer = new SafeTimeTracker();
	//	private int tick = Utils.RANDOM.nextInt();
	private boolean powered = false;
	
	@EnergyBattery(maxCapacity = 1000, maxReceivedPerCycle = 150,
			minimumConsumption = 1, energyChannel = EnergyAPI.batteryChannelMJ)
	private double mjStored = 0;

	public TileAirProducer() {
		emcAirHandler = new EmcAirHandler(this, EmcAirHandler.Type.ENGINE);
		initEmcAirProvider();
	}

	private void initEmcAirProvider() {
		emcAirHandler.configureEmcAirPerdition(1, 100);
	}

	public TileBuffer getTileBuffer(final ForgeDirection side) {
		if (tileCache == null) {
			tileCache = TileBuffer.makeBuffer(worldObj, xCoord, yCoord, zCoord, false);
		}

		return tileCache[side.ordinal()];
	}
	private TileBuffer[] tileCache;

	public boolean isPoweredTile(final TileEntity tile, final ForgeDirection side) {
		if (tile == null) {
			return false;
		} else if (tile instanceof IAirReceptor) {
			return ((IAirReceptor) tile).getAirReceiver(side.getOpposite()) != null;
		} else {
			return EnergyAPI.getBattery(tile, Names2.Energy.CHANNEL_EMCAIR) != null;
		}
	}

	private double getEmcAirToExtract(final ForgeDirection direction) {
		final TileEntity tile = getTileBuffer(direction).getTile();

		if (tile instanceof IAirReceptor) {
			AirHandler.AirReceiver receptor = ((IAirReceptor) tile)
					.getAirReceiver(direction.getOpposite());

			return extractEnergy(receptor.getMinEnergyReceived(),
					receptor.getMaxEnergyReceived(), false);
		} else {
			return extractEnergy(0, EnergyAPI.getBattery(tile, Names2.Energy.CHANNEL_EMCAIR)
					.getEnergyRequested(), false);
		}
	}

	public double maxEnergyReceived() {
		return 2000;
	}

	public double maxEnergyExtracted() {
		return 500;
	}

	public double energy;

	public double extractEnergy(final double min, final double max, final boolean doExtract) {
		if (energy < min) {
			return 0;
		}

		double actualMax;

		if (max > maxEnergyExtracted()) {
			actualMax = maxEnergyExtracted();
		} else {
			actualMax = max;
		}

		if (actualMax < min) {
			return 0;
		}

		double extracted;

		if (energy >= actualMax) {
			extracted = actualMax;

			if (doExtract) {
				energy -= actualMax;
			}
		} else {
			extracted = energy;

			if (doExtract) {
				energy = 0;
			}
		}

		return extracted;
	}

	private void sendPower() {

		byte fansBlocked = 0;

		if (this.worldObj.getBlock(xCoord, yCoord + 1, zCoord).isOpaqueCube()) fansBlocked++;
		if (this.worldObj.getBlock(xCoord, yCoord - 1, zCoord).isOpaqueCube()) fansBlocked++;

		//if it cant suck air properly, waste some power
		if (fansBlocked == 1) {
			energy *= 0.25;
		}

		//blow up like an air compressor
		if (fansBlocked >= 2) {
			ExplodeUtil.blowUp(this.worldObj, xCoord, yCoord, zCoord, 7.0F);
		} else {

			sendPower(ForgeDirection.UP);
			sendPower(ForgeDirection.DOWN);

			sendPower(ForgeDirection.NORTH);
			sendPower(ForgeDirection.SOUTH);
			sendPower(ForgeDirection.EAST);
			sendPower(ForgeDirection.WEST);

		}


	}

	private void sendPower(final ForgeDirection direction) {
		TileEntity tile = getTileBuffer(direction).getTile();
		if (isPoweredTile(tile, direction)) {
			final EmcAirReceiver receptor = EmcPipeUtil.getEmcAirReceiver(tile, direction.getOpposite());

			double extracted = getEmcAirToExtract(direction);
			if (extracted > 0) {
				double needed = receptor.receiveEmcAir(EmcAirHandler.Type.ENGINE, extracted, direction.getOpposite());
				extractEnergy(receptor.getMinEmcAirReceived(), needed, true); // Comment out for constant power
				//currentOutput = extractEnergy(0, needed, true); // Uncomment for constant power
			}
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			return;
		}

		if (powered) {
			return;
		}

		//		tick++;

		energy += mjStored;

		if (energy > 10) {
			sendPower();
		}

		if (mjStored > 0.25D) mjStored -= 0.25D; //waste some

	}

	public void onNeighborBlockChange(final Block block) {
		boolean p = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (powered != p) {
			powered = p;

			if(!worldObj.isRemote) {
				sendNetworkUpdate();
			}
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		powered = data.getBoolean("powered");
		energy = data.getDouble("cachedEnergy");

	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);

		data.setBoolean("powered", powered);
		data.setDouble("cachedEnergy", energy);
	}

	@Override
	public boolean isActive() {
		//TODO
		return false;
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(new PacketPayload.StreamWriter() {
			@Override
			public void writeData(ByteBuf buf) {
				buf.writeBoolean(powered);
			}
		});

		return payload;
	}

	@Override
	public void handleUpdatePacket(final PacketUpdate packet) throws IOException {
		PacketPayload payload = packet.payload;
		ByteBuf data = payload.stream;
		powered = data.readBoolean();

	}

	@Override
	public void handleDescriptionPacket(final PacketUpdate packet) throws IOException {
		handleUpdatePacket(packet);
	}

	//	@Override
	//	public void invalidate() {
	//		super.invalidate();
	//		destroy();
	//	}
	//
	//	@Override
	//	public void validate() {
	//		super.validate();
	//	}
	//
	//	@Override
	//	public void destroy() {
	//	}

	@Override
	public boolean manageFluids() {
		return false;
	}

	@Override
	public boolean manageSolids() {
		return false;
	}

	@Override
	public boolean allowAction(final IAction action) {
		return false;
	}

	@Override
	public EmcAirReceiver getEmcAirReceiver(final ForgeDirection side) {
		return emcAirHandler.getEmcAirReceiver();
	}

	@Override
	public void doWork(final EmcAirHandler workProvider) {
	}

	@Override
	public boolean canEmitEmcAirFrom(final ForgeDirection side) {
		return true;
	}


}
