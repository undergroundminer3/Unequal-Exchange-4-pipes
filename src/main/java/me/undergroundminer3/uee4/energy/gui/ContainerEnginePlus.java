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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerEnginePlus extends ContainerEE_BC {

	protected TileEnginePlus engine;

	public ContainerEnginePlus(final InventoryPlayer inventoryplayer, final TileEnginePlus tileEngine) {
		super(tileEngine.getSizeInventory());

		engine = tileEngine;

		upgradeSlot = new Slot(tileEngine, 0, upgradeSlotXPos, upgradeSlotYPos);
		confirmSlot = new Slot(tileEngine, 1, confirmSlotXPos, confirmSlotYPos);
		
		addSlotToContainer(upgradeSlot);
		addSlotToContainer(confirmSlot);

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
			}

		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18, 142));
		}
	}
	
	public final Slot upgradeSlot;
	public final Slot confirmSlot;
	
	public static final int upgradeSlotXPos = 70;
	public static final int upgradeSlotYPos = 41;
	public static final int confirmSlotXPos = 90;
	public static final int confirmSlotYPos = 41;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++) {
			engine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
		}
	}

	@Override
	public void updateProgressBar(final int i, final int j) {
		engine.getGUINetworkData(i, j);
	}

	public boolean isUsableByPlayer(final EntityPlayer entityplayer) {
		return engine.isUseableByPlayer(entityplayer);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		return engine.isUseableByPlayer(entityplayer);
	}
}
