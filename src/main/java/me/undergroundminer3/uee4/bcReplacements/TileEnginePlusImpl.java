package me.undergroundminer3.uee4.bcReplacements;

import buildcraft.energy.TileEngine;
import buildcraft.energy.TileEngine.EnergyStage;
import me.undergroundminer3.uee4.energy.gui.ContainerEnginePlus;
import me.undergroundminer3.uee4.init.ModInstance;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnginePlusImpl extends TileEnginePlus {

	public TileEnginePlusImpl() {
		super(2);
	}

	@Override
	public float explosionRange() {
		return 2;
	}

	@Override
	public double getMJEnergyInputMin() {
		return 0;
	}

	@Override
	protected EnergyStage computeEnergyStage() {
		final double energyLevel = getEnergyLevel();
		if (energyLevel < 0.25f) {
			return EnergyStage.BLUE;
		} else if (energyLevel < 0.5f) {
			return EnergyStage.GREEN;
		} else if (energyLevel < 0.75f) {
			return EnergyStage.YELLOW;
		} else {
			return EnergyStage.RED;
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, ForgeDirection side) {
		if (!worldObj.isRemote) {
			player.openGui(ModInstance.getMod(), Names2.GuiIDS.GUI_ENGINEPLUS, worldObj, xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	public float getPistonSpeed() {
		if (!worldObj.isRemote) {
			return Math.max(0.08f * getHeatLevel(), 0.01f);
		}

		switch (getEnergyStage()) {
		case GREEN:
			return 0.02F;
		case YELLOW:
			return 0.04F;
		case RED:
			return 0.08F;
		default:
			return 0.01F;
		}
	}

	@Override
	public void engineUpdate() {
		super.engineUpdate();

		if (isRedstonePowered) {
			//			if (worldObj.getTotalWorldTime() % 16 == 0) {
			addInternEnergy(currentGeneration);
			//          }
		}
	}

	//TODO config
	//	@Override
	//	public ConnectOverride overridePipeConnection(final PipeType type, final ForgeDirection with) {
	//		return ConnectOverride.DISCONNECT;
	//	}

	@Override
	public boolean isBurning() {
		return burnTime > 0;
	}

	@Override
	public int getScaledBurnTime(final int i) {
		return 0;
	}

	@Override
	public double getMJEnergyOutput() {
		return currentOutputDisplay;
	}

	@Override
	public double getMJEnergyInputMax() {
		return 200;
	}

	@Override
	public double getMJEnergyOutputMax() {
		return 100;
	}

	@Override
	public double getMJEnergyStoredMax() {
		return 1000;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void getGUINetworkData(final int id, final int value) {
		super.getGUINetworkData(id, value);
		switch (id) {
		case 15:
			burnTime = value;
			break;
		case 16:
			totalBurnTime = value;
			break;
		}
	}

	@Override
	public void sendGUINetworkData(final ContainerEnginePlus containerEngine, final ICrafting iCrafting) {
		super.sendGUINetworkData(containerEngine, iCrafting);
		iCrafting.sendProgressBarUpdate(containerEngine, 15, burnTime);
		iCrafting.sendProgressBarUpdate(containerEngine, 16, totalBurnTime);
	}

	public int burnTime = 0;
	public int totalBurnTime = 0;

	@Override
	public ResourceLocation getBaseTexture() {
		return TileEngine.BASE_TEXTURES[0];
	}

	@Override
	public ResourceLocation getChamberTexture() {
		return TileEngine.CHAMBER_TEXTURES[0];
	}

	@Override
	public double getInternalEnergyGeneration() {
		return currentGeneration;
	}

}
