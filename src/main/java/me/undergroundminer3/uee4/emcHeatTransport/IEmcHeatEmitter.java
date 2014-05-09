package me.undergroundminer3.uee4.emcHeatTransport;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Essentially only used for Wooden Power Pipe connection rules.
 *
 * This Tile Entity interface allows you to indicate that a block can emit emc
 * from a specific side.
 */
public interface IEmcHeatEmitter {

	public boolean canEmitEmcHeatFrom(ForgeDirection side);
}
