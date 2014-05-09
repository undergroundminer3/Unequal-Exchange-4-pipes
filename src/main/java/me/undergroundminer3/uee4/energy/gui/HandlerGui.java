package me.undergroundminer3.uee4.energy.gui;

import me.undergroundminer3.uee4.bcReplacements.TileEnginePlus;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class HandlerGui implements IGuiHandler {

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player,
			final World world, final int x, final int y, final int z) {

		if (!world.blockExists(x, y, z)) return null;

		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null) return null;

		//engineplus
		if (ID == Names2.GuiIDS.GUI_ENGINEPLUS) {
			if (tile instanceof TileEnginePlus) {
				return new GuiEnginePlusImpl(player.inventory, (TileEnginePlus) tile);
			}
		}

		return null; //default

	}

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player,
			final World world, final int x, final int y, final int z) {

		if (!world.blockExists(x, y, z)) return null;

		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null) return null;

		//engineplus
		if (ID == Names2.GuiIDS.GUI_ENGINEPLUS) {
			if (tile instanceof TileEnginePlus) {
				if (tile instanceof TileEnginePlus) {
					return new ContainerEnginePlus(player.inventory, (TileEnginePlus) tile);
				}
			}
		}

		return null; //default
	}

}
