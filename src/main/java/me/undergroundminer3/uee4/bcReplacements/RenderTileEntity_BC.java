/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderTileEntity_BC extends TileEntitySpecialRenderer {

	public RenderTileEntity_BC() {
		super();
	}

	@Override
	public void renderTileEntityAt(final TileEntity tileentity,
			final double x, final double y, final double z, final float f) {

		if (tileentity != null) {
			final Block b = tileentity.getBlockType();
			if (TileRenderDelegateRegistry.blockByEntityRenders.containsKey(b)) {
				TileRenderDelegateRegistry.blockByEntityRenders.get(b).worldRender(tileentity, x, y, z, f);
			}
		}
	}

	@Override
	public void bindTexture(final ResourceLocation p_147499_1_) {
		super.bindTexture(p_147499_1_);
	}

}
