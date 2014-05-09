package me.undergroundminer3.uee4.energy.gui;

import java.util.ArrayList;

import me.undergroundminer3.uee4.bcReplacements.SessionVars2;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerEE_BC;
import buildcraft.core.utils.SessionVars;

public class LedgerMgr {

	private GuiEE_BC gui;
	private boolean isLeftSide;
	protected ArrayList<LedgerEE_BC> ledgers = new ArrayList<LedgerEE_BC>();

	public LedgerMgr(final GuiEE_BC gui, final boolean leftSide) {
		this.gui = gui;
		this.isLeftSide = leftSide;
	}

	public void add(final LedgerEE_BC ledger) {
		ledger.setLeftSide(isLeftSide);
		this.ledgers.add(ledger);
		if (isLeftSide) {
			if (SessionVars2.getOpenedLedger2() != null && ledger.getClass().equals(SessionVars2.getOpenedLedger2())) {
				ledger.setFullyOpen();
			}
		} else {
			if (SessionVars.getOpenedLedger() != null && ledger.getClass().equals(SessionVars.getOpenedLedger())) {
				ledger.setFullyOpen();
			}
		}
	}

	/**
	 * Inserts a ledger into the next-to-last position.
	 *
	 * @param ledger
	 */
	public void insert(final LedgerEE_BC ledger) {
		ledger.setLeftSide(isLeftSide);
		this.ledgers.add(ledgers.size() - 1, ledger);
	}

	public LedgerEE_BC getAtPosition(final int mX, final int mY) {

		final int xShift = isLeftSide ? (gui.getWidth() / 2) - (gui.getXSize() / 2) : ((gui.getWidth() - gui.getXSize()) / 2) + gui.getXSize();
		int yShift = ((gui.getHeight() - gui.getYSize()) / 2) + 8;

		for (int i = 0; i < ledgers.size(); i++) {
			final LedgerEE_BC ledger = ledgers.get(i);
			if (!ledger.isVisible()) {
				continue;
			}

			ledger.currentShiftX = xShift;
			ledger.currentShiftY = yShift;
			if (ledger.intersectsWith(mX, mY, xShift, yShift)) {
				return ledger;
			}

			yShift += ledger.getHeight();
		}

		return null;
	}

	public void drawLedgers(final int mouseX, final int mouseY) {

		int yPos = 8;
		for (final LedgerEE_BC ledger : ledgers) {

			ledger.update();
			if (!ledger.isVisible()) {
				continue;
			}

			ledger.draw(isLeftSide ? 0 : gui.getXSize(), yPos);
			yPos += ledger.getHeight();
		}

		final LedgerEE_BC ledger = getAtPosition(mouseX, mouseY);
		if (ledger != null) {
			final int startX = mouseX - ((gui.getWidth() - gui.getXSize()) / 2) + 12;
			final int startY = mouseY - ((gui.getHeight() - gui.getYSize()) / 2) - 12;

			final String tooltip = ledger.getTooltip();
			final int textWidth = gui.getFontRenderer().getStringWidth(tooltip);
			gui.drawGradientRect(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000, 0xc0000000);
			gui.getFontRenderer().drawStringWithShadow(tooltip, startX, startY, -1);
		}
	}

	public void handleMouseClicked(final int x, final int y, final int mouseButton) {

		if (mouseButton == 0) {

			final LedgerEE_BC ledger = this.getAtPosition(x, y);

			// Default action only if the mouse click was not handled by the
			// ledger itself.
			if (ledger != null && !ledger.handleMouseClicked(x, y, mouseButton)) {

				for (final LedgerEE_BC other : ledgers) {
					if (other != ledger && other.isOpen()) {
						other.toggleOpen();
					}
				}
				ledger.toggleOpen();
			}
		}

	}
}