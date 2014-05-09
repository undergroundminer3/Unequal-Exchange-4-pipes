package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.client.icon.GuiIcons;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import buildcraft.core.utils.StringUtils;

public class LedgerMachine extends LedgerEE_BC {

	public final IStoresCachedEnergy generator;
	public static final int headerColour = 0xe1c92f;
	public static final int subheaderColour = 0xaaafb8;
	public static final int textColour = 0x000000;

	public LedgerMachine(final IStoresCachedEnergy gen, final GuiEE_BC g) {
		super(g);
		this.generator = gen;
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

		drawIcon(GuiIcons.INSTANCE.getIcon(GuiIcons.GOLD_GEAR), xShift + 3, y + 4);

		if (!isFullyOpened())
			return;

		final FontRenderer fontRendererObj = gui.getFontRenderer();

		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineEnergyStoreLedger"), xShift + 22, y + 8, headerColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineEnergyCache") + ":", xShift + 22, y + 20, subheaderColour);
		fontRendererObj.drawString(String.format("%.2f E", generator.getInternEnergy()), xShift + 22, y + 32, textColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.machineEnergyCacheMax") + ":", xShift + 22, y + 44, subheaderColour);
		fontRendererObj.drawString(String.format("%.2f E", generator.getInternEnergyMax()), xShift + 22, y + 56, textColour);

	}

	@Override
	public String getTooltip() {
		return String.format("%.2f E", generator.getInternEnergy());
	}
}