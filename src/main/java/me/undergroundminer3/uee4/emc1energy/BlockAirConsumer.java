package me.undergroundminer3.uee4.emc1energy;

import java.util.List;
import java.util.Random;

import me.undergroundminer3.uee4.creativetab.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.IItemPipe;
import buildcraft.energy.TileEngine;
import buildcraft.energy.TileEngineIron;
import buildcraft.energy.TileEngineStone;
import buildcraft.energy.TileEngineWood;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAirConsumer extends BlockBuildCraft {

	public BlockAirConsumer() {
		super(Material.iron);
		setBlockName("emc1ToMjBlock");
		this.setCreativeTab(CreativeTab.EE3_TAB);
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
	}

	@Override
	public int getRenderType() {
//		return BuildCraftCore.blockByEntityModel;
		return super.getRenderType();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
//		if (metadata == 1)
			return new TileAirConsumer();
//		else if (metadata == 2)
//			return new TileEngineIron();
//		else
//			return new TileEngineWood();
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileAirConsumer) {
//			return ((TileAirConsumer) tile).orientation.getOpposite() == side;
			return true;
		}
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileAirConsumer) {
//			return ((TileEngine) tile).switchOrientation(false);
			return false;
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7, float par8, float par9) {

		TileAirConsumer tile = (TileAirConsumer) world.getTileEntity(i, j, k);

		// Drop through if the player is sneaking
		if (player.isSneaking())
			return false;

		// Do not open guis when having a pipe in hand
		if (player.getCurrentEquippedItem() != null) {
			if (player.getCurrentEquippedItem().getItem() instanceof IItemPipe) {
				return false;
			}
		}

		if (tile instanceof TileAirConsumer) {
//			return ((TileAirConsumer) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
			return false;
		}

		return false;
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int par5) {
		TileAirConsumer tile = (TileAirConsumer) world.getTileEntity(x, y, z);
//		tile.orientation = ForgeDirection.UP;
//		if (!tile.isOrientationValid())
//			tile.switchOrientation(true);
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@SuppressWarnings({"all"})
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
//		TileEngine tile = (TileEngine) world.getTileEntity(i, j, k);
//
//		if (!tile.isBurning())
//			return;
//
//		float f = (float) i + 0.5F;
//		float f1 = (float) j + 0.0F + (random.nextFloat() * 6F) / 16F;
//		float f2 = (float) k + 0.5F;
//		float f3 = 0.52F;
//		float f4 = random.nextFloat() * 0.6F - 0.3F;
//
//		world.spawnParticle("reddust", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
//		world.spawnParticle("reddust", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
//		world.spawnParticle("reddust", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
//		world.spawnParticle("reddust", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
//		itemList.add(new ItemStack(this, 1, 0));
//		itemList.add(new ItemStack(this, 1, 1));
//		itemList.add(new ItemStack(this, 1, 2));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileAirConsumer tile = (TileAirConsumer) world.getTileEntity(x, y, z);

		if (tile != null) {
//			tile.checkRedstonePower();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch (meta) {
//			case 0:
//				return woodTexture;
//			case 1:
//				return stoneTexture;
//			case 2:
//				return ironTexture;
			default:
				return null;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
//		return new TileAirConsumer();
		return null;
	}
}
