/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.energy.gui;

import me.undergroundminer3.uee4.bcReplacements.TileEnginePlus;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import buildcraft.core.DefaultProps;
import buildcraft.core.utils.StringUtils;

public class GuiEnginePlusImpl extends GuiEnginePlus {

	private static final ResourceLocation TEXTURE = new ResourceLocation("uee4", DefaultProps.TEXTURE_PATH_GUI + "/enginePlusInit.png");

	public GuiEnginePlusImpl(final InventoryPlayer inventoryplayer, final TileEnginePlus tileEngine) {
		super(new ContainerEnginePlus(inventoryplayer, tileEngine), tileEngine, TEXTURE);
		
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		String title = StringUtils.localize("tile.enginePlusUEE4");
		fontRendererObj.drawString(title, getCenteredOffset(title), 6, 0x404040);
		fontRendererObj.drawString(StringUtils.localize("gui.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}
	
	protected int page;

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int x, final int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(TEXTURE);
		final int j = (width - xSize) / 2;
		final int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

		final TileEnginePlus engine = (TileEnginePlus) tile;
		if (engine.getScaledBurnTime(12) > 0) {
			final int l = engine.getScaledBurnTime(12);

			drawTexturedModalRect(j + 80, (k + 24 + 12) - l, 176, 12 - l, 14, l + 2);
		}
	}
}
