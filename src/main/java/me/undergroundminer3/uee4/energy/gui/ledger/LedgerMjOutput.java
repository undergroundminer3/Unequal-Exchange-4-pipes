package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.client.icon.GuiIcons;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import buildcraft.core.utils.StringUtils;

public class LedgerMjOutput extends LedgerEE_BC {

	public final IMJEnergyEmitterTabbable machine;
	public static final int headerColour = 0xe1c92f;
	public static final int subheaderColour = 0xaaafb8;
	public static final int textColour = 0x000000;

	public LedgerMjOutput(final IMJEnergyEmitterTabbable engine, final GuiEE_BC g) {
		super(g);
		this.machine = engine;
		maxHeight = 94;
		overlayColor = 0xd46c1f;
	}

	@Override
	public void draw(final int x, final int y) {
		
		final int xShift = isLeftSide() ? x - getWidth() + 2 : x;

		// Draw background
		drawBackground(x, y);

		// Draw icon
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiEE_BC.ITEM_TEXTURE);
		drawIcon(GuiIcons.INSTANCE.getIcon(GuiIcons.MJ_POWOUT), xShift + 3, y + 4);

		if (!isFullyOpened())
			return;

		final FontRenderer fontRendererObj = gui.getFontRenderer();

		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.energyMJTitle"), xShift + 22, y + 8, headerColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.currentOutputMJ") + ":", xShift + 22, y + 20, subheaderColour);
		fontRendererObj.drawString(String.format("%.1f MJ/t", machine.getMJEnergyOutput()), xShift + 22, y + 32, textColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.storedMJ") + ":", xShift + 22, y + 44, subheaderColour);
		fontRendererObj.drawString(String.format("%.1f MJ", machine.getMJEnergyStored()), xShift + 22, y + 56, textColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.maxOutputMJ") + ":", xShift + 22, y + 68, subheaderColour);
		fontRendererObj.drawString(String.format("%.1f MJ", machine.getMJEnergyOutputMax()), xShift + 22, y + 80, textColour);
		//			fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.heat") + ":", x + 22, y + 68, subheaderColour);
		//			fontRendererObj.drawString(String.format("%.2f \u00B0C", machine.getHeat()), x + 22, y + 80, textColour);

	}

	@Override
	public String getTooltip() {
		return String.format("%.1f MJ/t", machine.getMJEnergyOutput());
	}
}