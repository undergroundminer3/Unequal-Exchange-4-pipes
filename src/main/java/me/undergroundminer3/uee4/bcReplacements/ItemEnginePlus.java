/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

import me.undergroundminer3.uee4.abstacts.ItemBlock_BC;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemEnginePlus extends ItemBlock_BC {

	public ItemEnginePlus(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemstack) {
		//		if (itemstack.getItemDamage() == 0)
		//			return "tile.engineWood";
		//		else if (itemstack.getItemDamage() == 1)
		//			return "tile.engineStone";
		//		else
		//			return "tile.engineIron";

//		return EngineRegistry.getEngineName(itemstack.getItemDamage());
		return "tile.uee4.enginePlus.WIP";
	}
}
