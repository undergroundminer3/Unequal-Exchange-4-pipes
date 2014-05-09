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
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerExplosion;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerGenerator;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerHeat;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerInfo;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerMachine;
import me.undergroundminer3.uee4.energy.gui.ledger.LedgerMjOutput;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public abstract class GuiEnginePlus extends GuiEE_BC {

	public GuiEnginePlus(final ContainerEnginePlus container, final IInventory inventory, final ResourceLocation texture) {
		super(container, inventory, texture);
	}

	@Override
	protected void initLedgers(final IInventory inventory) {
		super.initLedgers(inventory);
		
		final TileEnginePlus engine = (TileEnginePlus) tile;
		
		ledgerManagerLeft.add(new LedgerMjOutput(engine, this));
		ledgerManagerLeft.add(new LedgerHeat(engine, this));
		ledgerManagerLeft.add(new LedgerExplosion(engine, this));
		ledgerManagerLeft.add(new LedgerGenerator(engine, this));
		ledgerManagerLeft.add(new LedgerMachine(engine, this));

		ledgerManagerRight.add(new LedgerInfo(this, "gui.enginePlus.ledgerPrefix", new String[] {
				"gui.enginePlus.infoLedger.text1",
				"gui.enginePlus.infoLedger.text2",
				"gui.enginePlus.infoLedger.text3",
				"gui.enginePlus.infoLedger.text4",
				"gui.enginePlus.infoLedger.text5",
				"gui.enginePlus.infoLedger.text6",
				"gui.enginePlus.infoLedger.text7",
				"gui.enginePlus.infoLedger.text8",
				"gui.enginePlus.infoLedger.text9",
				"gui.enginePlus.infoLedger.text10"
		}, true));
	}
}
