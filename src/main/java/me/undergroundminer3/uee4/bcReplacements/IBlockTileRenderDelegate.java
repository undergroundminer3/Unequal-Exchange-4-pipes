package me.undergroundminer3.uee4.bcReplacements;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public abstract interface IBlockTileRenderDelegate {

	public abstract void inventoryRender(final double x, final double y, final double z,
			final float f, final float f1, final Block b, final int meta);

	public abstract void worldRender(final TileEntity tile,
			final double x, final double y, final double z, final double f);
}
