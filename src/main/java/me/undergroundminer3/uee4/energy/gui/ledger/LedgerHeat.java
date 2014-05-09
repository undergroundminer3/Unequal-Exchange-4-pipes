package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.client.icon.GuiIcons;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import buildcraft.core.utils.StringUtils;

public class LedgerHeat extends LedgerEE_BC {

	public final IHasTemperature machine;
	public static final int headerColour = 0xe1c92f;
	public static final int subheaderColour = 0xaaafb8;
	public static final int textColour = 0x000000;

	public LedgerHeat(final IHasTemperature heatedBlock, final GuiEE_BC g) {
		super(g);
		this.machine = heatedBlock;
		maxHeight = 68;
		overlayColor = 0xe44955;
	}

	@Override
	public void draw(final int x, final int y) {

		// Draw background
		drawBackground(x, y);

		final int xShift = isLeftSide() ? x - getWidth() + 2 : x;

		// Draw icon
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiEE_BC.ITEM_TEXTURE);

		drawIcon(GuiIcons.INSTANCE.getIcon(GuiIcons.LAVA_BUCKET), xShift + 3, y + 4);

		if (!isFullyOpened())
			return;

		final FontRenderer fontRendererObj = gui.getFontRenderer();

		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineHeatTitle"), xShift + 22, y + 8, headerColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineHeatCurrent") + ":", xShift + 22, y + 20, subheaderColour);
		fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getHeat()), xShift + 22, y + 32, textColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineHeatIdeal") + ":", xShift + 22, y + 44, subheaderColour);
		fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getIdealHeat()), xShift + 22, y + 56, textColour);
		//		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.maxOutputMJ") + ":", x + 22, y + 68, subheaderColour);
		//		fontRendererObj.drawString(String.format("%.1f MJ", machine.getMJEnergyOutputMax()), x + 22, y + 80, textColour);
		//			fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.heat") + ":", x + 22, y + 68, subheaderColour);
		//			fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getHeat()), x + 22, y + 80, textColour);

	}

	@Override
	public String getTooltip() {
		return String.format("%.2f \u00B0C", machine.getHeat());
	}
}