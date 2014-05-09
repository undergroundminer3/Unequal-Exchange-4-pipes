package me.undergroundminer3.uee4.energy.gui.ledger;

import me.undergroundminer3.uee4.client.icon.GuiIcons;
import me.undergroundminer3.uee4.energy.gui.GuiEE_BC;
import me.undergroundminer3.uee4.util2.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import buildcraft.core.utils.StringUtils;

public class LedgerGenerator extends LedgerEE_BC {

	public final IGeneratorTabbable generator;
	public static final int headerColour = 0x1fd46c;
	public static final int subheaderColour = 0xaaafb8;
	public static final int textColour = 0x000000;

	public LedgerGenerator(final IGeneratorTabbable gen, final GuiEE_BC g) {
		super(g);
		this.generator = gen;
		maxHeight = 68;
		overlayColor = 0xdddddd;
	}

	@Override
	public void draw(final int x, final int y) {

		// Draw background
		drawBackground(x, y);

		final int xShift = isLeftSide() ? x - getWidth() + 2 : x;

		// Draw icon
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiEE_BC.ITEM_TEXTURE);

		drawIcon(GuiIcons.INSTANCE.getIcon(
				generator.isBurning() ? GuiIcons.GENERATOR_ON : GuiIcons.GENERATOR_OFF), xShift + 3, y + 4);

		if (!isFullyOpened())
			return;

		final FontRenderer fontRendererObj = gui.getFontRenderer();

		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.generatorLedgerTitle"), xShift + 22, y + 8, headerColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.generatorInternalGen") + ":", xShift + 22, y + 20, subheaderColour);
		fontRendererObj.drawString(String.format("%.2f E", generator.getInternalEnergyGeneration()), xShift + 22, y + 32, textColour);
		fontRendererObj.drawStringWithShadow(StringUtils.localize("gui.generatorBurnTime") + ":", xShift + 22, y + 44, subheaderColour);
		fontRendererObj.drawString(TimeUtil.getHumanReadableTimeFromTicks(generator.getBurningTicks()), xShift + 22, y + 56, textColour);

	}

	@Override
	public String getTooltip() {
		return generator.isBurning() ? (StringUtils.localize("gui.generatorBurningTooltip") + 
				TimeUtil.getHumanReadableTimeFromTicks(generator.getBurningTicks())) :
					StringUtils.localize("gui.generatorNotBurningTooltip");
	}
}