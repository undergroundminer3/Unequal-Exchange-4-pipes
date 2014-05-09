/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirTransport2;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPipeTransportAirHook {

	/**
	 * Override default behavior on receiving energy into the pipe.
	 *
	 * @return The amount of power used, or -1 for default behavior.
	 */
	double receiveEnergy(ForgeDirection from, double val);

	/**
	 * Override default requested power.
	 */
	double requestEnergy(ForgeDirection from, double amount);
}
