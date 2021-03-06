/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirTransport;

import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.emctransport.EmcPipeIconProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

public class PipeEmcAirDiamond extends Pipe<PipeTransportEmcAir> {

	public PipeEmcAirDiamond(Item item) {
		super(new PipeTransportEmcAir(), item);
		transport.initFromPipe(getClass());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return EmcPipeIconProvider.INSTANCE;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return EmcPipeIconProvider.TYPE.PipeAirDiamond.ordinal();
	}
}
