/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.client.renderer.blockentity.Renderers;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderBlockEntityInv_BC implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(final Block block,
			final int metadata, final int modelID, final RenderBlocks renderer) {

		if (block.getRenderType() == Renderers.dynamicBlockEntityRendererID) {

			if (TileRenderDelegateRegistry.blockByEntityRenders.containsKey(block)) {
				TileRenderDelegateRegistry.blockByEntityRenders.get(block).inventoryRender(-0.5, -0.5, -0.5, 0, 0,
						block, metadata);
			}

		}
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world,
			final int x, final int y, final int z,
			final Block block, final int modelId, final RenderBlocks renderer) {

		if (block.getRenderType() == Renderers.dynamicBlockEntityRendererID) {
			// renderblocks.renderStandardBlock(block, i, j, k);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(final int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return Renderers.dynamicBlockEntityRendererID;
	}

}
