/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emc1energy;

import buildcraft.api.gates.IAction;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.core.IMachine;
import buildcraft.core.TileBuffer;
import buildcraft.core.network.PacketPayload;
import buildcraft.core.network.PacketPayloadStream;
import buildcraft.core.network.PacketUpdate;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

import me.undergroundminer3.uee4.abstacts.TileEntityEE_BC;
import me.undergroundminer3.uee4.config.ExplodeUtil;
import me.undergroundminer3.uee4.emc1transport.Emc1Handler;
import me.undergroundminer3.uee4.emc1transport.Emc1Handler.Emc1Receiver;
import me.undergroundminer3.uee4.emc1transport.IEmc1Receptor;
import me.undergroundminer3.uee4.emctransport.EmcPipeUtil;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileAirConsumer extends TileEntityEE_BC implements IMachine, IPowerReceptor, IEmc1Receptor, IPowerEmitter {

	private PowerHandler powerHandler;
	private Emc1Handler emc1Handler;
	//	private TileBuffer[] tileBuffer = null;
	//	private SafeTimeTracker timer = new SafeTimeTracker();
	//	private int tick = Utils.RANDOM.nextInt();
	private boolean powered = false;

	public TileAirConsumer() {
		//		powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
		powerHandler = new PowerHandler(this, PowerHandler.Type.ENGINE);
		//		emc1Handler = new Emc1Handler(this, Emc1Handler.Type.ENGINE);
		emc1Handler = new Emc1Handler(this, Emc1Handler.Type.MACHINE);

		initPowerProvider();
		initEmc1Provider();
	}

	private void initPowerProvider() {
		emc1Handler.configure(1, 150, 300, 1500);
		emc1Handler.configureEmc1Perdition(10, 5);
	}

	private void initEmc1Provider() {
		powerHandler.configurePowerPerdition(1, 100);
	}

	public TileBuffer getTileBuffer(final ForgeDirection side) {
		if (tileCache == null) {
			tileCache = TileBuffer.makeBuffer(worldObj, xCoord, yCoord, zCoord, false);
		}

		return tileCache[side.ordinal()];
	}
	private TileBuffer[] tileCache;

	public boolean isPoweredTile(final TileEntity tile, final ForgeDirection side) {
		return EmcPipeUtil.getPowerReceiver(tile, side.getOpposite()) != null;
	}

	private double getPowerToExtract(final ForgeDirection direction) {
		TileEntity tile = getTileBuffer(direction).getTile();
		PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile, direction);
		return extractEnergy(receptor.getMinEnergyReceived(), receptor.getMaxEnergyReceived(), false); // Comment out for constant power
		//		return extractEnergy(0, getActualOutput(), false); // Uncomment for constant power
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

		byte gratesBlocked = 0;

		if (this.worldObj.getBlock(xCoord, yCoord + 1, zCoord).isOpaqueCube()) gratesBlocked++;
		if (this.worldObj.getBlock(xCoord, yCoord - 1, zCoord).isOpaqueCube()) gratesBlocked++;

		//if it cant expel air properly, waste some power
		if (gratesBlocked == 1) {
			energy *= 0.25;
		}

		//blow up like a baloon
		if (gratesBlocked >= 2) {
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
			PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile, direction.getOpposite());

			double extracted = getPowerToExtract(direction);
			if (extracted > 0) {
				//				double needed = 
				receptor.receiveEnergy(PowerHandler.Type.ENGINE, extracted, direction.getOpposite());
				//				extractEnergy(receptor.getMinEnergyReceived(), needed, true); // Comment out for constant power
				//				currentOutput = extractEnergy(0, needed, true); // Uncomment for constant power
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


		double power = emc1Handler.getEmc1Stored();
		power = emc1Handler.useEmc1(power, power, true);
		energy += power;
		
		if (energy > 10) {
			sendPower();
		}

		emc1Handler.useEmc1(0.0D, 0.25D, true); //waste some

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

	//	private TileEntity getTile(ForgeDirection side) {
	//		if (tileBuffer == null) {
	//			tileBuffer = TileBuffer.makeBuffer(worldObj, xCoord, yCoord, zCoord, false);
	//		}
	//		
	//		return tileBuffer[side.ordinal()].getTile();
	//	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		powerHandler.readFromNBT(data);

		powered = data.getBoolean("powered");
		energy = data.getDouble("cachedEnergy");

		initPowerProvider();
	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);

		powerHandler.writeToNBT(data);

		data.setBoolean("powered", powered);
		data.setDouble("cachedEnergy", energy);
	}

	@Override
	public boolean isActive() {
		//TODO
		return false;
		//		if (next != null) {
		//			return isPumpableFluid(next.x, next.y, next.z);
		//		} else {
		//			return false;
		//		}
	}

	@Override
	public PowerReceiver getPowerReceiver(final ForgeDirection side) {
		return powerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(final PowerHandler workProvider) {
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayloadStream payload = new PacketPayloadStream(new PacketPayloadStream.StreamWriter() {
			@Override
			public void writeData(ByteBuf buf) {
				buf.writeBoolean(powered);
			}
		});

		return payload;
	}

	@Override
	public void handleUpdatePacket(final PacketUpdate packet) throws IOException {
		PacketPayloadStream payload = (PacketPayloadStream) packet.payload;
		ByteBuf data = payload.stream;
		powered = data.readBoolean();

	}

	@Override
	public void handleDescriptionPacket(final PacketUpdate packet) throws IOException {
		handleUpdatePacket(packet);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		destroy();
	}

	@Override
	public void validate() {
		//		tileBuffer = null;
		super.validate();
	}

	@Override
	public void destroy() {
		//		tileBuffer = null;
	}

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

	//	@Override
	//	public boolean canEmitEmc1From(ForgeDirection side) {
	//		return true;
	//	}

	@Override
	public Emc1Receiver getEmc1Receiver(final ForgeDirection side) {
		return emc1Handler.getEmc1Receiver();
	}

	@Override
	public void doWork(final Emc1Handler workProvider) {
	}

	@Override
	public boolean canEmitPowerFrom(final ForgeDirection side) {
		//		// TODO Auto-generated method stub
		//		return false;
		return true;
	}


}
