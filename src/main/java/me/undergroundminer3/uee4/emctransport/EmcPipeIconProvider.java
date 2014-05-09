/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emctransport;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import buildcraft.BuildCraftCore;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EmcPipeIconProvider implements IIconProvider {
	
	public static final EmcPipeIconProvider INSTANCE = new EmcPipeIconProvider();

	public enum TYPE {

		PipeAirStone("pipeEmc1Stone"),
		PipeAirCobblestone("pipeEmc1Cobblestone"),
		PipeAirWood_Standard("pipeEmc1Wood_standard"),
		PipeAirQuartz("pipeEmc1Quartz"),
		PipeAirDiamond("pipeEmc1Diamond"),
		PipeAirGold("pipeEmc1Gold"),
		
		PipeEmcHeatStone("pipeEmc2Stone"),
		PipeEmcHeatCobblestone("pipeEmc2Cobblestone"),
		PipeEmcHeatWood_Standard("pipeEmc2Wood_standard"),
		PipeEmcHeatQuartz("pipeEmc2Quartz"),
		PipeEmcHeatDiamond("pipeEmc2Diamond"),
		PipeEmcHeatGold("pipeEmc2Gold");
		
		public static final TYPE[] VALUES = values();
		private final String iconTag;
		private final String iconTagColorBlind;
		private IIcon icon;

		private TYPE(String iconTag, String IconTagColorBlind) {
			this.iconTag = iconTag;
			this.iconTagColorBlind = IconTagColorBlind;
		}

		private TYPE(String iconTag) {
			this(iconTag, iconTag);
		}

		private void registerIcon(IIconRegister iconRegister) {
			icon = iconRegister.registerIcon("uee4:" + (BuildCraftCore.colorBlindMode ? iconTagColorBlind : iconTag));
		}

		public IIcon getIcon() {
			return icon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int pipeIconIndex) {
		if (pipeIconIndex == -1)
			return null;
		return TYPE.VALUES[pipeIconIndex].icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for (TYPE type : TYPE.VALUES) {
			type.registerIcon(iconRegister);
		}
	}
}
