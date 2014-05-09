package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.bcReplacements.SessionVars2;
import me.undergroundminer3.uee4.config.Config;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import buildcraft.core.DefaultProps;
import buildcraft.core.render.RenderUtils;
import buildcraft.core.utils.SessionVars;

/**
 * Side ledger for guis
 */
public abstract class LedgerEE_BC {

	public static final ResourceLocation LEDGER_TEXTURE_DEFAULT_RIGHT = new ResourceLocation("buildcraft", DefaultProps.TEXTURE_PATH_GUI + "/ledger.png");
	public static final ResourceLocation LEDGER_TEXTURE_DEFAULT_LEFT = new ResourceLocation("uee4", DefaultProps.TEXTURE_PATH_GUI + "/ledger2.png");

	private boolean open;
	protected int overlayColor = 0xffffff;
	public int currentShiftX = 0;
	public int currentShiftY = 0;
	protected int limitWidth = 128;
	protected int maxWidth = 124;
	protected int minWidth = 24;
	protected int currentWidth = minWidth;
	protected int maxHeight = 24;
	protected int minHeight = 24;
	protected int currentHeight = minHeight;

	protected final GuiEE_BC gui;

	private boolean isLeftSide;

	public void update() {
		// Width
		if (open && currentWidth < maxWidth) {
			currentWidth += Config.tabExpandSpeed;
		} else if (!open && currentWidth > minWidth) {
			currentWidth -= Config.tabExpandSpeed;
		}

		// Height
		if (open && currentHeight < maxHeight) {
			currentHeight += Config.tabExpandSpeed;
		} else if (!open && currentHeight > minHeight) {
			currentHeight -= Config.tabExpandSpeed;
		}
	}

	public int getHeight() {
		return currentHeight;
	}

	public int getWidth() {
		return currentWidth;
	}

	public abstract void draw(final int x, final int y);

	public abstract String getTooltip();

	public boolean handleMouseClicked(final int x, final int y, final int mouseButton) {
		return false;
	}

	public boolean intersectsWith(final int mouseX, final int mouseY, final int shiftX, final int shiftY) {

		if (isLeftSide) { //THANK YOU, COFHLIB (LGPL3) https://github.com/KingLemming/CoFHLib
			if (mouseX <= shiftX && mouseX >= shiftX - getWidth() && mouseY >= shiftY && mouseY <= shiftY + getHeight()) {
				return true;
			}
		} else {
			if (mouseX >= shiftX && mouseX <= shiftX + getWidth() && mouseY >= shiftY && mouseY <= shiftY + getHeight()) {
				return true;
			}
		} //END COFHLIB

		return false;
	}

	public void setFullyOpen() {
		open = true;
		currentWidth = maxWidth;
		currentHeight = maxHeight;
	}

	public LedgerEE_BC(final GuiEE_BC g) {
		super();
		this.gui = g;
	}

	public void toggleOpen() {
		if (open) {
			open = false;
			if (this.isLeftSide()) {
				SessionVars2.setOpenedLedger2(null);
			} else {
				SessionVars.setOpenedLedger(null);
			}
		} else {
			open = true;
			if (this.isLeftSide()) {
				SessionVars2.setOpenedLedger2(this.getClass());
			} else {
				SessionVars.setOpenedLedger(this.getClass());
			}
		}
	}

	public boolean isVisible() {
		return true;
	}

	public boolean isOpen() {
		return this.open;
	}

	protected boolean isFullyOpened() {
		return getWidth() >= maxWidth;
	}

	protected void drawBackground(final int x, final int y) {

		RenderUtils.setGLColorFromInt(overlayColor);

		final int width = getWidth();
		final int height = getHeight();

		Minecraft.getMinecraft().renderEngine.bindTexture(isLeftSide() ? LEDGER_TEXTURE_DEFAULT_LEFT : LEDGER_TEXTURE_DEFAULT_RIGHT);

		if (isLeftSide()) {
			//THANK YOU, COFHLIB (LGPL3) https://github.com/KingLemming/CoFHLib
			gui.drawTexturedModalRect(x - width, y + 4, 0, 256 - height + 4, 4, height - 4);
			gui.drawTexturedModalRect(x - width + 4, y, 256 - width + 4, 0, width - 4, 4);
			gui.drawTexturedModalRect(x - width, y, 0, 0, 4, 4);
			gui.drawTexturedModalRect(x - width + 4, y + 4, 256 - width + 4, 256 - height + 4, width - 4, height - 4);
			//END COFHLIB
		} else {
			gui.drawTexturedModalRect(x, y, 0, 256 - height, 4, height); //left column
			gui.drawTexturedModalRect(x + 4, y, 256 - width + 4, 0, width - 4, 4);//top row
			// Add in top left corner again
			gui.drawTexturedModalRect(x, y, 0, 0, 4, 4); //left corner

			gui.drawTexturedModalRect(x + 4, y + 4, 256 - width + 4, 256 - height + 4, width - 4, height - 4); //rest of it
		}

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
	}

	protected void drawIcon(final IIcon icon, final int x, final int y) {
//		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
//		GL11.glEnable(GL11.GL_BLEND);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
		gui.drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
//		GL11.glPopAttrib();
	}

	public boolean isLeftSide() {
		return isLeftSide;
	}

	public void setLeftSide(final boolean leftSide) {
		this.isLeftSide = leftSide;
	}
}