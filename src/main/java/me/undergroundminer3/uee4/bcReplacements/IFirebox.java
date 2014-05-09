package me.undergroundminer3.uee4.bcReplacements;

import buildcraft.api.energy.EnergyAPI.BatteryObject;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public abstract interface IFirebox {

	public abstract void update(final boolean powered, final TileEnginePlus engine);

	public abstract double getMaxHeat();

	public abstract double getHeat();

	public abstract double output();
	
	public abstract boolean isFireboxInstalled();
	
	public abstract boolean canInstallWithGear(final Item gear);
	
	public abstract boolean installComponents(final Item[] components);
	
	public abstract void read(final NBTTagCompound nbt);
	
	public abstract void save(final NBTTagCompound nbt);
	
	public abstract BatteryObject getBattery(final String channel);
	
//	public abstract b
}
