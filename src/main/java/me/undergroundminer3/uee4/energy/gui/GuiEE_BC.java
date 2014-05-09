/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.energy.gui;

import java.util.Collection;
import java.util.List;

import me.undergroundminer3.uee4.energy.gui.widget.WidgetEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import buildcraft.core.gui.tooltips.IToolTipProvider;
import buildcraft.core.gui.tooltips.ToolTip;
import buildcraft.core.gui.tooltips.ToolTipLine;

public abstract class GuiEE_BC extends GuiContainer {

	public static final ResourceLocation ITEM_TEXTURE = TextureMap.locationItemsTexture;
	
	public final LedgerMgr ledgerManagerRight = new LedgerMgr(this, false);
	public final LedgerMgr ledgerManagerLeft = new LedgerMgr(this, true);
	public final TileEntity tile;
	public final ResourceLocation texture;
	public final ContainerEE_BC container;

	public GuiEE_BC(final ContainerEE_BC container, final IInventory inventory, final ResourceLocation texture) {
		super(container);
		this.container = container;

		this.texture = texture;

		if (inventory instanceof TileEntity) {
			tile = (TileEntity) inventory;
		} else {
			tile = null;
		}

		initLedgers(inventory);
	}

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	protected void initLedgers(final IInventory inventory) {}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		final int left = this.guiLeft;
		final int top = this.guiTop;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) left, (float) top, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();

		final InventoryPlayer playerInv = this.mc.thePlayer.inventory;

		if (playerInv.getItemStack() == null) {
			drawToolTips(container.getWidgets(), mouseX, mouseY);
			drawToolTips(buttonList, mouseX, mouseY);
			drawToolTips(inventorySlots.inventorySlots, mouseX, mouseY);
		}

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@SuppressWarnings("rawtypes")
	private void drawToolTips(final Collection objects, final int mouseX, final int mouseY) {
		for (final Object obj : objects) {
			if (!(obj instanceof IToolTipProvider))
				continue;
			final IToolTipProvider provider = (IToolTipProvider) obj;
			if (!provider.isToolTipVisible())
				continue;
			final ToolTip tips = provider.getToolTip();
			if (tips == null)
				continue;
			final boolean mouseOver = provider.isMouseOver(mouseX - guiLeft, mouseY - guiTop);
			tips.onTick(mouseOver);
			if (mouseOver && tips.isReady()) {
				tips.refresh();
				drawToolTips(tips, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int mouseX, final int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(texture);
		final int x = (width - xSize) / 2;
		final int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		final int mX = mouseX - guiLeft;
		final int mY = mouseY - guiTop;

		for (final WidgetEE_BC widget : container.getWidgets()) {
			if (widget.hidden)
				continue;
			bindTexture(texture);
			widget.draw(this, x, y, mX, mY);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
		ledgerManagerLeft.drawLedgers(par1, par2);
		ledgerManagerRight.drawLedgers(par1, par2);
	}

	protected int getCenteredOffset(final String string) {
		return getCenteredOffset(string, xSize);
	}

	protected int getCenteredOffset(final String string, final int xWidth) {
		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
	}

	/**
	 * Returns if the passed mouse position is over the specified slot.
	 */
	private boolean isMouseOverSlot(final Slot slot, int mouseX, int mouseY) {
		final int left = this.guiLeft;
		final int top = this.guiTop;
		mouseX -= left;
		mouseY -= top;
		return mouseX >= slot.xDisplayPosition - 1 && mouseX < slot.xDisplayPosition + 16 + 1 && mouseY >= slot.yDisplayPosition - 1 && mouseY < slot.yDisplayPosition + 16 + 1;
	}

	// / MOUSE CLICKS
	@Override
	protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
		final int mX = mouseX - guiLeft;
		final int mY = mouseY - guiTop;

		for (final WidgetEE_BC widget : container.getWidgets()) {
			if (widget.hidden)
				continue;
			if (!widget.isMouseOver(mX, mY))
				continue;
			if (widget.handleMouseClick(mX, mY, mouseButton))
				return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);

		// / Handle ledger clicks
		ledgerManagerLeft.handleMouseClicked(mouseX, mouseY, mouseButton);
		ledgerManagerRight.handleMouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(final int mouseX, final int mouseY, final int mouseButton, final long time) {
		final int mX = mouseX - guiLeft;
		final int mY = mouseY - guiTop;
		for (final WidgetEE_BC widget : container.getWidgets()) {
			if (widget.hidden)
				continue;
			widget.handleMouseMove(mX, mY, mouseButton, time);
		}

		final Slot slot = getSlotAtPosition(mouseX, mouseY);
		if (mouseButton == 1 && slot instanceof IPhantomSlot)
			return;
		super.mouseClickMove(mouseX, mouseY, mouseButton, time);
	}

	@Override
	protected void mouseMovedOrUp(final int mouseX, final int mouseY, final int eventType) {
		super.mouseMovedOrUp(mouseX, mouseY, eventType);

		final int mX = mouseX - guiLeft;
		final int mY = mouseY - guiTop;
		for (final WidgetEE_BC widget : container.getWidgets()) {
			if (widget.hidden)
				continue;
			widget.handleMouseRelease(mX, mY, eventType);
		}
	}

	public Slot getSlotAtPosition(final int x, final int y) {
		for (int slotIndex = 0; slotIndex < this.inventorySlots.inventorySlots.size(); ++slotIndex) {
			final Slot slot = (Slot) this.inventorySlots.inventorySlots.get(slotIndex);
			if (isMouseOverSlot(slot, x, y))
				return slot;
		}
		return null;
	}

	public void bindTexture(final ResourceLocation texture) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	private void drawToolTips(final ToolTip toolTips, final int mouseX, final int mouseY) {
		if (toolTips.size() > 0) {
			final int left = this.guiLeft;
			final int top = this.guiTop;
			int length = 0;
			int x;
			int y;

			for (final ToolTipLine tip : toolTips) {
				y = this.fontRendererObj.getStringWidth(tip.text);

				if (y > length) {
					length = y;
				}
			}

			x = mouseX - left + 12;
			y = mouseY - top - 12;
			int var14 = 8;

			if (toolTips.size() > 1) {
				var14 += 2 + (toolTips.size() - 1) * 10;
			}

			this.zLevel = 300.0F;
			itemRender.zLevel = 300.0F;
			int var15 = -267386864;
			this.drawGradientRect(x - 3, y - 4, x + length + 3, y - 3, var15, var15);
			this.drawGradientRect(x - 3, y + var14 + 3, x + length + 3, y + var14 + 4, var15, var15);
			this.drawGradientRect(x - 3, y - 3, x + length + 3, y + var14 + 3, var15, var15);
			this.drawGradientRect(x - 4, y - 3, x - 3, y + var14 + 3, var15, var15);
			this.drawGradientRect(x + length + 3, y - 3, x + length + 4, y + var14 + 3, var15, var15);
			int var16 = 1347420415;
			int var17 = (var16 & 16711422) >> 1 | var16 & -16777216;
			this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + var14 + 3 - 1, var16, var17);
			this.drawGradientRect(x + length + 2, y - 3 + 1, x + length + 3, y + var14 + 3 - 1, var16, var17);
			this.drawGradientRect(x - 3, y - 3, x + length + 3, y - 3 + 1, var16, var16);
			this.drawGradientRect(x - 3, y + var14 + 2, x + length + 3, y + var14 + 3, var17, var17);

			for (final ToolTipLine tip : toolTips) {
				String line = tip.text;

				if (tip.color == -1) {
					line = "\u00a77" + line;
				} else {
					line = "\u00a7" + Integer.toHexString(tip.color) + line;
				}

				this.fontRendererObj.drawStringWithShadow(line, x, y, -1);

				y += 10 + tip.getSpacing();
			}

			this.zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
		}
	}

	@Override
	public void drawGradientRect(int par1, int par2, int par3, int par4,
			int par5, int par6) {
		super.drawGradientRect(par1, par2, par3, par4, par5, par6);
	}

	@Override
	public void drawHorizontalLine(int par1, int par2, int par3, int par4) {
		super.drawHorizontalLine(par1, par2, par3, par4);
	}

	@Override
	public void drawVerticalLine(int par1, int par2, int par3, int par4) {
		super.drawVerticalLine(par1, par2, par3, par4);
	}

	@Override
	public void drawCreativeTabHoveringText(String p_146279_1_,
			int p_146279_2_, int p_146279_3_) {
		super.drawCreativeTabHoveringText(p_146279_1_, p_146279_2_, p_146279_3_);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void drawHoveringText(List p_146283_1_, int p_146283_2_,
			int p_146283_3_, FontRenderer font) {
		super.drawHoveringText(p_146283_1_, p_146283_2_, p_146283_3_, font);
	}

	public int getXSize() {
		return this.xSize;
	}
	
	public int getYSize() {
		return this.ySize;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	
}
