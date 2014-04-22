package me.undergroundminer3.uee4.emc1transport;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Essentially only used for Wooden Power Pipe connection rules.
 *
 * This Tile Entity interface allows you to indicate that a block can emit emc
 * from a specific side.
 */
public interface IEmc1Emitter {

	public boolean canEmitEmc1From(ForgeDirection side);
}
