/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emc1energy;

import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.gates.IAction;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.core.IMachine;
import buildcraft.core.TileBuffer;
import buildcraft.core.TileBuildCraft;
import buildcraft.core.network.PacketPayload;
import buildcraft.core.network.PacketPayloadStream;
import buildcraft.core.network.PacketUpdate;
import buildcraft.core.utils.Utils;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

import me.undergroundminer3.uee4.emc1transport.Emc1Handler;
import me.undergroundminer3.uee4.emc1transport.Emc1Handler.Emc1Receiver;
import me.undergroundminer3.uee4.emc1transport.IEmc1Emitter;
import me.undergroundminer3.uee4.emc1transport.IEmc1Receptor;
import me.undergroundminer3.uee4.emctransport.EmcPipeUtil;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileAirConsumer extends TileBuildCraft implements IMachine, IPowerReceptor, IEmc1Receptor, IPowerEmitter {

	private PowerHandler powerHandler;
	private Emc1Handler emc1Handler;
	private TileBuffer[] tileBuffer = null;
	private SafeTimeTracker timer = new SafeTimeTracker();
	private int tick = Utils.RANDOM.nextInt();
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
		emc1Handler.configure(2, 30, 10, 200);
		emc1Handler.configureEmc1Perdition(10, 5);
	}
	
	private void initEmc1Provider() {
		powerHandler.configurePowerPerdition(1, 100);
	}
	
	public TileBuffer getTileBuffer(ForgeDirection side) {
		if (tileCache == null) {
			tileCache = TileBuffer.makeBuffer(worldObj, xCoord, yCoord, zCoord, false);
		}
		
		return tileCache[side.ordinal()];
	}
	private TileBuffer[] tileCache;
	
	public boolean isPoweredTile(TileEntity tile, ForgeDirection side) {
		return EmcPipeUtil.getPowerReceiver(tile, side.getOpposite()) != null;
	}
	
	private double getPowerToExtract() {
		TileEntity tile = getTileBuffer(ForgeDirection.UP).getTile();
		PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile, ForgeDirection.UP.getOpposite());
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
	
	public double extractEnergy(double min, double max, boolean doExtract) {
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
		TileEntity tile = getTileBuffer(ForgeDirection.UP).getTile();
		if (isPoweredTile(tile, ForgeDirection.UP)) {
			PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile, ForgeDirection.UP.getOpposite());

			double extracted = getPowerToExtract();
			if (extracted > 0) {
				double needed = receptor.receiveEnergy(PowerHandler.Type.ENGINE, extracted, ForgeDirection.UP.getOpposite());
				extractEnergy(receptor.getMinEnergyReceived(), needed, true); // Comment out for constant power
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

		tick++;

		if (emc1Handler.getEmc1Stored() > 10) {
			double power = emc1Handler.getEmc1Stored();
			power = emc1Handler.useEmc1(power, power, true);
			energy = power;
			sendPower();
		}

	}

	public void onNeighborBlockChange(Block block) {
		boolean p = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		if (powered != p) {
			powered = p;
			
			if(!worldObj.isRemote) {
				sendNetworkUpdate();
			}
		}
	}

	private TileEntity getTile(ForgeDirection side) {
		if (tileBuffer == null) {
			tileBuffer = TileBuffer.makeBuffer(worldObj, xCoord, yCoord, zCoord, false);
		}
		
		return tileBuffer[side.ordinal()].getTile();
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		powerHandler.readFromNBT(data);

		powered = data.getBoolean("powered");

		initPowerProvider();
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		powerHandler.writeToNBT(data);

		data.setBoolean("powered", powered);
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
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider) {
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
	public void handleUpdatePacket(PacketUpdate packet) throws IOException {
		PacketPayloadStream payload = (PacketPayloadStream) packet.payload;
		ByteBuf data = payload.stream;
		powered = data.readBoolean();

	}

	@Override
	public void handleDescriptionPacket(PacketUpdate packet) throws IOException {
		handleUpdatePacket(packet);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		destroy();
	}

	@Override
	public void validate() {
		tileBuffer = null;
		super.validate();
	}

	@Override
	public void destroy() {
		tileBuffer = null;
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
	public boolean allowAction(IAction action) {
		return false;
	}

//	@Override
//	public boolean canEmitEmc1From(ForgeDirection side) {
//		return true;
//	}

	@Override
	public Emc1Receiver getEmc1Receiver(ForgeDirection side) {
		return emc1Handler.getEmc1Receiver();
	}

	@Override
	public void doWork(Emc1Handler workProvider) {
	}

	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
//		// TODO Auto-generated method stub
//		return false;
		return true;
	}


}
