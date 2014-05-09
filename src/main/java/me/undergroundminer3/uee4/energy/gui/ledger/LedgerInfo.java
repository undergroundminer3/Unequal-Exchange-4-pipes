package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.client.icon.GuiIcons;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import buildcraft.core.utils.StringUtils;

public class LedgerInfo extends LedgerEE_BC {

	public static final int headerColour = 0xe1c92f;
	public static final int textColour = 0x000000;
	public final String machineName;
	public final String[] informationData;
	public final boolean local;

	public LedgerInfo(final GuiEE_BC g, final String name, final String[] data, final boolean localize) {
		super(g);
		//		this.maxHeight = 82;
		this.maxHeight = 24 + (data.length * 12);
		this.overlayColor = 0x2fa0e1;
		this.machineName = name;
		this.informationData = data;
		this.local = localize;
	}

	@Override
	public void draw(final int x, final int y) {

		//		final boolean leftSide = isLeftSide();
		final int xShift = isLeftSide() ? x - getWidth() + 2 : x;

		// Draw background
		drawBackground(x, y);

		// Draw icon
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiEE_BC.ITEM_TEXTURE);

		drawIcon(GuiIcons.INSTANCE.getIcon(GuiIcons.INFO_SIGN), xShift + 3, y + 4);

		if (!isFullyOpened())
			return;

		final FontRenderer fontRendererObj = gui.getFontRenderer();

		fontRendererObj.drawStringWithShadow((local ? StringUtils.localize(machineName) : machineName) + " "
				+ StringUtils.localize("gui.informationLedgerTitle"), xShift + 22, y + 8, headerColour);

		int yTextInc = 24;

//		GL11.glPushMatrix();
//		GL11.glTranslatef(-20.0F, 0.0F, 0.0F);
//		GL11.glScalef(0.90F, 0.90F, 1.0F);
		for (final String text : informationData) {

			fontRendererObj.drawString((local ? StringUtils.localize(text) : text),
					xShift + 4, y + yTextInc, textColour);
			yTextInc += 12;

		}
//		GL11.glPopMatrix();

		//		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineHeatCurrent") + ":", x + 22, y + 20, subheaderColour);
		//		fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getHeat()), x + 22, y + 32, textColour);
		//		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineHeatIdeal") + ":", x + 22, y + 44, subheaderColour);
		//		fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getIdealHeat()), x + 22, y + 56, textColour);
		//		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.maxOutputMJ") + ":", x + 22, y + 68, subheaderColour);
		//		fontRendererObj.drawString(String.format("%.1f MJ", machine.getMJEnergyOutputMax()), x + 22, y + 80, textColour);
		//			fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.heat") + ":", x + 22, y + 68, subheaderColour);
		//			fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getHeat()), x + 22, y + 80, textColour);

	}

	@Override
	public String getTooltip() {
		return String.format(StringUtils.localize("gui.informationLedgerTitle"));
	}
}