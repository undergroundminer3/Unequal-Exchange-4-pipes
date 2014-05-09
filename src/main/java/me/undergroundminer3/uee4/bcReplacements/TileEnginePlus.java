/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

import java.util.LinkedList;

import me.undergroundminer3.uee4.abstacts.TileEntityEE_BC;
import me.undergroundminer3.uee4.config.ExplodeUtil;
import me.undergroundminer3.uee4.emctransport.EmcPipeUtil;
import me.undergroundminer3.uee4.energy.gui.ContainerEnginePlus;
import me.undergroundminer3.uee4.energy.gui.ledger.IGeneratorTabbable;
import me.undergroundminer3.uee4.energy.gui.ledger.IMJEnergyEmitterTabbable;
import me.undergroundminer3.uee4.energy.gui.ledger.IMJEnergyReceptorTabbable;
import me.undergroundminer3.uee4.energy.gui.ledger.IOverheatable;
import me.undergroundminer3.uee4.energy.gui.ledger.IStoresCachedEnergy;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.BuildCraftEnergy;
import buildcraft.api.core.NetworkData;
import buildcraft.api.energy.EnergyAPI;
import buildcraft.api.energy.EnergyAPI.BatteryObject;
import buildcraft.api.energy.EnergyAPI.IBatteryProvider;
import buildcraft.api.energy.EnergyBattery;
import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.core.TileBuffer;
import buildcraft.core.inventory.SimpleInventory;
import buildcraft.energy.TileEngine;
import buildcraft.energy.TileEngine.EnergyStage;

public abstract class TileEnginePlus extends TileEntityEE_BC implements
IPowerReceptor, IPowerEmitter, IOverrideDefaultTriggers,
IPipeConnection, IInventory, IMJEnergyEmitterTabbable,
IMJEnergyReceptorTabbable, IOverheatable, IGeneratorTabbable,
IStoresCachedEnergy, IBatteryProvider {

	@Override
	public BatteryObject getBattery(final String channel) {
		if (channel.equals(EnergyAPI.batteryChannelMJ)) {
			return batteryMj;
		}
		if (channel.equals(Names2.Energy.CHANNEL_EMCAIR)) {
			return batteryEmcAir;
		}
		//no battery for emcheat
		//we output heat???
		return null;
	}

	private EnergyAPI.BatteryObject batteryMj;
	private EnergyAPI.BatteryObject batteryEmcAir;

	private static class AnoymousBatteryAir {
		@EnergyBattery(energyChannel = Names2.Energy.CHANNEL_EMCAIR)
		double airStored;
	}

	private static class AnoymousBatteryEnergy {
		@EnergyBattery(energyChannel = EnergyAPI.batteryChannelMJ)
		double mjStored;
	}
	
	public TileEnginePlus(final int invSize) {
		this.mjPowerHandler = new PowerHandler(this, Type.ENGINE);
		this.mjPowerHandler.configurePowerPerdition(1, 100);
		this.inv = new SimpleInventory(invSize, "EnginePlus", 64);

		this.batteryMj = EnergyAPI.getBattery(new AnoymousBatteryEnergy(), EnergyAPI.batteryChannelMJ);
		this.batteryMj.reconfigure(0.0D, 0.0D, 0.0D, EnergyAPI.batteryChannelMJ);

		this.batteryEmcAir = EnergyAPI.getBattery(new AnoymousBatteryAir(), Names2.Energy.CHANNEL_EMCAIR);
		this.batteryEmcAir.reconfigure(0.0D, 0.0D, 0.0D, Names2.Energy.CHANNEL_EMCAIR);
	}

	@Override
	public double getEfficiencyPrecentage() {
		return 1.0D;
	}

	@Override
	public int getBurningTicks() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getIdealHeat() {
		return 120;
	}

	@Override
	public double getMaxHeat() {
		return Double.MAX_VALUE;
	}

	@Override
	public double getMJEnergyInputMin() {
		return 0;
	}

	@Override
	public int getTicksUntilNonOperational() {
		return Integer.MAX_VALUE;
	}

	@Override
	public double getMJEnergyInput() {
		return this.mjPowerHandler.getPowerReceiver().getAveragePowerReceived();
	}

	@Override
	public double getMJEnergyInputMax() {
		return 200.0D;
	}

	// public enum EnergyStage {
	// BLUE, GREEN, YELLOW, RED, OVERHEAT;
	// public static final EnergyStage[] VALUES = values();
	// }
	// i don't want another conflict

	public static final float MIN_HEAT = 20;
	public static final float IDEAL_HEAT = 100;
	public static final float MAX_HEAT = 250;
	protected int progressPart = 0;
	protected boolean lastPower = false;
	protected PowerHandler mjPowerHandler;
	public boolean isRedstonePowered = false;
	private boolean checkOrienation = false;
	private TileBuffer[] tileCache;
	public float progress;
	public double energy;
	public float heat = MIN_HEAT;
	//
	public @NetworkData
	EnergyStage energyStage = EnergyStage.BLUE;
	public @NetworkData
	ForgeDirection orientation = ForgeDirection.UP;
	public @NetworkData
	boolean isPumping = false; // Used for SMP synch

	protected double currentGeneration = 4.0D;
	protected double currentOutputDisplay = 0.0D;

	@Override
	public void initialize() {
		if (!this.worldObj.isRemote) {
			this.mjPowerHandler.configure(this.getMJEnergyInputMin(),
					this.getMJEnergyInputMax(), 1, this.getMJEnergyStoredMax());
			this.checkRedstonePower();
		}
	}

	protected void doRender_internalcall(final double x, final double y,
			final double z, final double f) {
		// do not use tile entity cordinates!

		// TODO more attributes
		RenderEnginePlus.renderEngine(
				new EnginePlusRenderData(this.getTrunkTexture(this
						.getEnergyStage()), this.getChamberTexture(), this
						.getBaseTexture(), this.progress), (float) x,
						(float) y, (float) z, this.orientation);
	}

	protected IFirebox firebox;

	public IFirebox getFirebox() {
		return firebox;
	}

	public abstract ResourceLocation getBaseTexture();

	public abstract ResourceLocation getChamberTexture();

	public ResourceLocation getTrunkTexture(final EnergyStage stage) {
		switch (stage) {
		case BLUE:
			return TileEngine.TRUNK_BLUE_TEXTURE;
		case GREEN:
			return TileEngine.TRUNK_GREEN_TEXTURE;
		case YELLOW:
			return TileEngine.TRUNK_YELLOW_TEXTURE;
		case RED:
			return TileEngine.TRUNK_RED_TEXTURE;
		default:
			return TileEngine.TRUNK_RED_TEXTURE;
		}
	}

	public boolean onBlockActivated(final EntityPlayer player,
			final ForgeDirection side) {
		return false;
	}

	public double getEnergyLevel() {
		return this.energy / this.getMJEnergyStoredMax();
	}

	protected EnergyStage computeEnergyStage() {
		final float energyLevel = this.getHeatLevel();
		if (energyLevel < 0.25f) {
			return EnergyStage.BLUE;
		} else if (energyLevel < 0.5f) {
			return EnergyStage.GREEN;
		} else if (energyLevel < 0.75f) {
			return EnergyStage.YELLOW;
		} else if (energyLevel < 1f) {
			return EnergyStage.RED;
		} else {
			return EnergyStage.OVERHEAT;
		}
	}

	public final EnergyStage getEnergyStage() {
		if (!this.worldObj.isRemote) {
			if (this.energyStage == EnergyStage.OVERHEAT) {
				return this.energyStage;
			}

			final EnergyStage newStage = this.computeEnergyStage();

			if (this.energyStage != newStage) {
				this.energyStage = newStage;
				this.sendNetworkUpdate();
			}
		}

		return this.energyStage;
	}

	public void updateHeatLevel() {
		this.heat = (float) ((MAX_HEAT - MIN_HEAT) * this.getEnergyLevel())
				+ MIN_HEAT;
	}

	public float getHeatLevel() {
		return (this.heat - MIN_HEAT) / (MAX_HEAT - MIN_HEAT);
	}

	public float getIdealHeatLevel() {
		return this.heat / IDEAL_HEAT;
	}

	@Override
	public double getHeat() {
		return this.heat;
	}

	public float getPistonSpeed() {
		if (!this.worldObj.isRemote) {
			return Math.max(0.16f * this.getHeatLevel(), 0.01f);
		}

		switch (this.getEnergyStage()) {
		case BLUE:
			return 0.02F;
		case GREEN:
			return 0.04F;
		case YELLOW:
			return 0.08F;
		case RED:
			return 0.16F;
		default:
			return 0;
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.worldObj.isRemote) {
			if (this.progressPart != 0) {
				this.progress += this.getPistonSpeed();

				if (this.progress > 1) {
					this.progressPart = 0;
					this.progress = 0;
				}
			} else if (this.isPumping) {
				this.progressPart = 1;
			}

			return;
		}

		if (this.checkOrienation) {
			this.checkOrienation = false;

			if (!this.isOrientationValid()) {
				this.switchOrientation(true);
			}
		}

		if (!this.isRedstonePowered) {
			if (this.energy > 1) {
				this.energy--;
			}
			if (this.mjEnergy > 1) {
				this.mjEnergy--;
			}
		}

		this.inputPenaltyThisTick = this.getInputPenalty();

		this.updateHeatLevel();
		this.getEnergyStage();
		this.engineUpdate();

		this.updatePowerOutputDisplay();

		final TileEntity tile = this.getTileBuffer(this.orientation).getTile();

		this.pumpEToMj();

		if (this.progressPart != 0) {
			this.progress += this.getPistonSpeed();

			if ((this.progress > 0.5) && (this.progressPart == 1)) {
				this.progressPart = 2;
				this.sendPower(); // Comment out for constant power
			} else if (this.progress >= 1) {
				this.progress = 0;
				this.progressPart = 0;

			}
		} else if (this.isRedstonePowered && this.isActive()) {
			if (isPoweredTile(tile, this.orientation)) {
				if (this.getPowerToExtract() > 0) {
					this.progressPart = 1;
					this.setPumping(true);
				} else {
					this.setPumping(false);
				}
			} else {
				this.setPumping(false);
			}
		} else {
			this.setPumping(false);
		}

		// Uncomment for constant power
		// if (isRedstonePowered && isActive()) {
		// sendPower();
		// } else currentOutput = 0;

		this.burn();

	}

	private void updatePowerOutputDisplay() {
		this.currentOutputDisplay = this.mjPowerHandler.getPowerReceiver().getAveragePowerReceived();
	}

	private double getPowerToExtract() {
		final TileEntity tile = this.getTileBuffer(this.orientation).getTile();

		final PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile,
				this.orientation.getOpposite());

		if (receptor != null) {
			return this.extractMJEnergy(receptor.getMinEnergyReceived(),
					receptor.getMaxEnergyReceived(), false);
		} else {
			return this.extractMJEnergy(0, EnergyAPI.getBattery(tile, EnergyAPI.batteryChannelMJ)
					.getEnergyRequested(), false);
		}
	}

	private void sendPower() {
		final TileEntity tile = this.getTileBuffer(this.orientation).getTile();

		final double extracted = this.getPowerToExtract();

		final PowerReceiver receptor = EmcPipeUtil.getPowerReceiver(tile,
				this.orientation.getOpposite());

		if (receptor != null) {
			if (extracted > 0) {
				final double needed = receptor.receiveEnergy(
						PowerHandler.Type.ENGINE, extracted,
						this.orientation.getOpposite());

				this.extractMJEnergy(receptor.getMinEnergyReceived(), needed,
						true);
			}
		} else {
			final BatteryObject battery = EnergyAPI.getBattery(tile, EnergyAPI.batteryChannelMJ);

			battery.addEnergy(this.extractMJEnergy(0,
					battery.maxReceivedPerCycle(), true));
		}
	}

	// Uncomment out for constant power
	// public float getActualOutput() {
	// float heatLevel = getIdealHeatLevel();
	// return getCurrentOutput() * heatLevel;
	// }
	protected void burn() {
	}

	protected void engineUpdate() {
		if (!this.isRedstonePowered) {
			if (this.energy >= 1) {
				this.energy -= 1;
			} else if (this.energy < 1) {
				this.energy = 0;
			}

			if (this.mjEnergy >= 1) {
				this.mjEnergy -= 1;
			} else if (this.mjEnergy < 1) {
				this.mjEnergy = 0;
			}
		}
	}

	public boolean isActive() {
		return true;
	}

	protected final void setPumping(final boolean isActive) {
		if (this.isPumping == isActive) {
			return;
		}

		this.isPumping = isActive;
		this.sendNetworkUpdate();
	}

	public boolean isOrientationValid() {
		final TileEntity tile = this.getTileBuffer(this.orientation).getTile();

		return isPoweredTile(tile, this.orientation);
	}

	public boolean switchOrientation(final boolean preferPipe) {
		if (preferPipe && this.switchOrientationDo(true)) {
			return true;
		} else {
			return this.switchOrientationDo(false);
		}
	}

	private boolean switchOrientationDo(final boolean pipesOnly) {
		for (int i = this.orientation.ordinal() + 1; i <= (this.orientation
				.ordinal() + 6); ++i) {
			final ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];

			final TileEntity tile = this.getTileBuffer(o).getTile();

			if ((!pipesOnly || (tile instanceof IPipeTile))
					&& isPoweredTile(tile, o)) {
				this.orientation = o;
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord,
						this.zCoord);
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord,
						this.yCoord, this.zCoord, this.worldObj.getBlock(
								this.xCoord, this.yCoord, this.zCoord));

				return true;
			}
		}

		return false;
	}

	public TileBuffer getTileBuffer(final ForgeDirection side) {
		if (this.tileCache == null) {
			this.tileCache = TileBuffer.makeBuffer(this.worldObj, this.xCoord,
					this.yCoord, this.zCoord, false);
		}

		return this.tileCache[side.ordinal()];
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.tileCache = null;
		this.checkOrienation = true;
	}

	@Override
	public void validate() {
		super.validate();
		this.tileCache = null;
		this.checkOrienation = true;
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		this.inv.readFromNBT(data);

		this.orientation = ForgeDirection.getOrientation(data
				.getInteger("orientation"));
		this.progress = data.getFloat("progress");
		this.energy = data.getDouble("energy");
		this.mjEnergy = data.getDouble("mjEnergy");
		this.heat = data.getFloat("heat");
	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		super.writeToNBT(data);

		this.inv.writeToNBT(data);

		data.setInteger("orientation", this.orientation.ordinal());
		data.setFloat("progress", this.progress);
		data.setDouble("energy", this.energy);
		data.setDouble("mjEnergy", this.mjEnergy);
		data.setFloat("heat", this.heat);

	}

	public void getGUINetworkData(final int id, final int value) {
		switch (id) {
		case 0:
			int iEnergy = (int) Math.round(this.energy * 10);
			iEnergy = (iEnergy & 0xffff0000) | (value & 0xffff);
			this.energy = iEnergy / 10;
			break;
		case 1:
			iEnergy = (int) Math.round(this.energy * 10);
			iEnergy = (iEnergy & 0xffff) | ((value & 0xffff) << 16);
			this.energy = iEnergy / 10;
			break;
		case 2:
			this.currentOutputDisplay = value / 10F;
			break;
		case 3:
			this.heat = value / 100F;
			break;
		case 4:
			int mEnergy = (int) Math.round(this.mjEnergy * 10);
			mEnergy = (mEnergy & 0xffff0000) | (value & 0xffff);
			this.mjEnergy = mEnergy / 10;
			break;
		case 5:
			mEnergy = (int) Math.round(this.mjEnergy * 10);
			mEnergy = (mEnergy & 0xffff) | ((value & 0xffff) << 16);
			this.mjEnergy = mEnergy / 10;
			break;
		}
	}

	public void sendGUINetworkData(final ContainerEnginePlus containerEngine,
			final ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(containerEngine, 0,
				(int) Math.round(this.energy * 10) & 0xffff);
		iCrafting.sendProgressBarUpdate(containerEngine, 1,
				(int) (Math.round(this.energy * 10) & 0xffff0000) >> 16);
		iCrafting.sendProgressBarUpdate(containerEngine, 2,
				(int) Math.round(this.currentOutputDisplay * 10));
		iCrafting.sendProgressBarUpdate(containerEngine, 3,
				Math.round(this.heat * 100));
		iCrafting.sendProgressBarUpdate(containerEngine, 4,
				(int) Math.round(this.mjEnergy * 10) & 0xffff);
		iCrafting.sendProgressBarUpdate(containerEngine, 5,
				(int) (Math.round(this.mjEnergy * 10) & 0xffff0000) >> 16);
	}

	/* STATE INFORMATION */
	@Override
	public abstract boolean isBurning();

	public abstract int getScaledBurnTime(final int scale);

	@Override
	public PowerReceiver getPowerReceiver(final ForgeDirection side) {
		return this.mjPowerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(final PowerHandler workProvider) {
		if (this.worldObj.isRemote) {
			return;
		}

		this.addMjEnergyToIntern(this.mjPowerHandler.useEnergy(1,
				this.getMJEnergyInputMax(), true) * 0.95F);
	}

	@Override
	public double getInternEnergy() {
		return this.energy;
	}

	@Override
	public double getInternEnergyMax() {
		return Double.MAX_VALUE;
	}

	public void addInternEnergy(final double addition) {
		this.energy += addition;

		if (this.getEnergyStage() == EnergyStage.OVERHEAT) {
			// worldObj.createExplosion(null, xCoord, yCoord, zCoord,
			// explosionRange(), true);
			// worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			ExplodeUtil.blowUp(this.worldObj, this.xCoord, this.yCoord,
					this.zCoord, this.explosionRange());
		}

		if (this.energy > this.getInternEnergyMax()) {
			this.energy = this.getInternEnergyMax();
		}
	}

	public static final double MJ_TO_E = 0.96D;
	public static final double E_TO_MJ = 0.98D;

	public double convertMjToE(final double mj) {
		return mj * MJ_TO_E;
	}

	public double convertEToMj(final double e) {
		return e * E_TO_MJ;
	}

	public double getInputPenalty() {
		return 0.01D;
	}

	protected double inputPenaltyThisTick = getInputPenalty();

	public double modifyByInputPenalty(final double e) {
		double result = e;
		result -= this.inputPenaltyThisTick;
		this.inputPenaltyThisTick -= e;
		if (this.inputPenaltyThisTick < 0) {
			this.inputPenaltyThisTick = 0;
		}
		//		if (result < 0) {
		//			result = 0;
		//		}
		//		return result;
		return result < 0 ? 0 : result;
	}

	public double addMjEnergyToIntern(final double addition) {
		final double result = modifyByInputPenalty(convertMjToE(addition));
		energy += result;
		return result;
	}

	public void pumpEToMj() {
		final double max = this.getMJEnergyStoredMax() - this.getMJEnergyStored();
		final double energyConverted = convertEToMj(energy);
		final double actualConverted = energyConverted < max ? energyConverted : max;
		final double actual = actualConverted * (1 / convertEToMj(1.0D));
		energy -= actual;
		mjEnergy += actualConverted;
	}

	public double extractMJEnergy(final double min, final double max,
			final boolean doExtract) {
		if (this.mjEnergy < min) {
			return 0;
		}

		double actualMax;

		if (max > this.getMJEnergyOutputMax()) {
			actualMax = this.getMJEnergyOutputMax();
		} else {
			actualMax = max;
		}

		if (actualMax < min) {
			return 0;
		}

		double extracted;

		if (this.mjEnergy >= actualMax) {
			extracted = actualMax;

			if (doExtract) {
				this.mjEnergy -= actualMax;
			}
		} else {
			extracted = this.mjEnergy;

			if (doExtract) {
				this.mjEnergy = 0;
			}
		}

		return extracted;
	}

	public static boolean isPoweredTile(final TileEntity tile,
			final ForgeDirection side) {
		return (EmcPipeUtil.getPowerReceiver(tile, side) != null)
				|| (EnergyAPI.getBattery(tile, EnergyAPI.batteryChannelMJ) != null); // TODO global add
	}

	public abstract float explosionRange();

	@Override
	public double getMJEnergyStored() {
		return this.mjEnergy;
	}

	public double mjEnergy;

	@Override
	public LinkedList<ITrigger> getTriggers() {
		final LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

		triggers.add(BuildCraftEnergy.triggerBlueEngineHeat);
		triggers.add(BuildCraftEnergy.triggerGreenEngineHeat);
		triggers.add(BuildCraftEnergy.triggerYellowEngineHeat);
		triggers.add(BuildCraftEnergy.triggerRedEngineHeat);

		return triggers;
	}

	@Override
	public ConnectOverride overridePipeConnection(final PipeType type,
			final ForgeDirection with) {
		if (type == PipeType.POWER) {
			return ConnectOverride.DEFAULT;
		} else if (with == this.orientation) {
			return ConnectOverride.DISCONNECT;
		} else {
			return ConnectOverride.DEFAULT;
		}
	}

	@Override
	public boolean canEmitPowerFrom(final ForgeDirection side) {
		return side == this.orientation;
	}

	public void checkRedstonePower() {
		this.isRedstonePowered = this.worldObj.isBlockIndirectlyGettingPowered(
				this.xCoord, this.yCoord, this.zCoord);
	}

	private final SimpleInventory inv;

	/* IINVENTORY IMPLEMENTATION */
	@Override
	public int getSizeInventory() {
		return this.inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return this.inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amount) {
		return this.inv.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return this.inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(final int slot,
			final ItemStack itemstack) {
		this.inv.setInventorySlotContents(slot, itemstack);
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return true;
	}

	@Override
	public String getInventoryName() {
		return "EnginePlus";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord,
				this.zCoord) == this;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
}
