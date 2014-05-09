/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.energy.gui.widget;

import buildcraft.core.gui.tooltips.IToolTipProvider;
import buildcraft.core.gui.tooltips.ToolTip;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.DataInputStream;
import java.io.IOException;

import me.undergroundminer3.uee4.energy.gui.ContainerEE_BC;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.inventory.ICrafting;

public class WidgetEE_BC implements IToolTipProvider {

	public final int x;
	public final int y;
	public final int u;
	public final int v;
	public final int w;
	public final int h;
	public boolean hidden;
	protected ContainerEE_BC container;

	public WidgetEE_BC(final int x, final int y, final int u, final int v, final int w, final int h) {
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
	}

	public void addToContainer(final ContainerEE_BC container) {
		this.container = container;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final boolean isMouseOver(final int mouseX, final int mouseY) {
		return mouseX >= x - 1 && mouseX < x + w + 1 && mouseY >= y - 1 && mouseY < y + h + 1;
	}

	@SideOnly(Side.CLIENT)
	public boolean handleMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void handleMouseRelease(final int mouseX, final int mouseY, final int eventType) {
	}

	@SideOnly(Side.CLIENT)
	public void handleMouseMove(final int mouseX, final int mouseY, final int mouseButton, final long time) {
	}

	@SideOnly(Side.CLIENT)
	public void handleClientPacketData(final DataInputStream data) throws IOException {
	}

	@SideOnly(Side.CLIENT)
	public void draw(final GuiEE_BC gui, final int guiX, final int guiY, final int mouseX, final int mouseY) {
		gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, w, h);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ToolTip getToolTip() {
		return null;
	}

	@Override
	public boolean isToolTipVisible() {
		return true;
	}

	public void initWidget(final ICrafting player) {
	}

	public void updateWidget(final ICrafting player) {
	}
}
