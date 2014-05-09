/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

import buildcraft.core.ICustomHighlight;
import buildcraft.core.IItemPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import me.undergroundminer3.uee4.abstacts.BlockEE_BC;
import me.undergroundminer3.uee4.client.renderer.blockentity.Renderers;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEnginePlus extends BlockEE_BC implements ICustomHighlight {

	public static BlockEnginePlus INSTANCE;

	public static IIcon texture;

	private static final AxisAlignedBB[][] boxes = {
		{AxisAlignedBB.getBoundingBox(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.0, 0.25, 0.75, 0.5, 0.75)}, // -Y
		{AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.5, 0.25, 0.75, 1.0, 0.75)}, // +Y
		{AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.0, 0.75, 0.75, 0.5)}, // -Z
		{AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 0.5), AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.5, 0.75, 0.75, 1.0)}, // +Z
		{AxisAlignedBB.getBoundingBox(0.5, 0.0, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.0, 0.25, 0.25, 0.5, 0.75, 0.75)}, // -X
		{AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.5, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.5, 0.25, 0.25, 1.0, 0.75, 0.75)} // +X
	};

	public BlockEnginePlus() {
		super(Material.iron);
		setBlockName(Names2.Engines.ENGINE_TILE_NAME);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister par1IconRegister) {
		//TODO
		texture = par1IconRegister.registerIcon("buildcraft:engineWoodBottom");
	}

	@Override
	public int getRenderType() {
		return Renderers.dynamicBlockEntityRendererID;
	}

	@Override
	public TileEntity createTileEntity(final World world, final int metadata) {
		//		if (metadata == 1)
		//			return new TileEnginePlusStone();
		//		else if (metadata == 2)
		//			return new TileEnginePlusIron();
		//		else
		//			return new TileEnginePlusWood();
		//		return EngineRegistry.newTile(metadata);
		return new TileEnginePlusImpl();
	}

	@Override
	public boolean isSideSolid(final IBlockAccess world,
			final int x, final int y, final int z, final ForgeDirection side) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			return ((TileEnginePlus) tile).orientation.getOpposite() == side;
		}
		return false;
	}

	@Override
	public boolean rotateBlock(final World world, final int x, final int y, final int z,
			final ForgeDirection axis) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			return ((TileEnginePlus) tile).switchOrientation(false);
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(final World world,
			final int i, final int j, final int k, final EntityPlayer player,
			final int side, final float par7, final float par8, final float par9) {

		final TileEnginePlus tile = (TileEnginePlus) world.getTileEntity(i, j, k);

		// Drop through if the player is sneaking
		if (player.isSneaking())
			return false;

		// Do not open guis when having a pipe in hand
		if (player.getCurrentEquippedItem() != null) {
			if (player.getCurrentEquippedItem().getItem() instanceof IItemPipe) {
				return false;
			}
		}

		if (tile instanceof TileEnginePlus) {
			return ((TileEnginePlus) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
		}

		return false;
	}

	@Override
	public void onPostBlockPlaced(final World world,
			final int x, final int y, final int z, final int par5) {
		final TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			TileEnginePlus engine = (TileEnginePlus) tile;
			engine.orientation = ForgeDirection.UP;
			if (!engine.isOrientationValid()) {
				engine.switchOrientation(true);
			}
		}
	}

	@Override
	public int damageDropped(final int i) {
		return i;
	}

	@SuppressWarnings({"all"})
	@Override
	public void randomDisplayTick(final World world, final int i, final int j, final int k, final Random random) {
		final TileEntity tile = world.getTileEntity(i, j, k);

		if (tile instanceof TileEnginePlus && !((TileEnginePlus) tile).isBurning()) {
			return;
		}

		float f = i + 0.5F;
		float f1 = j + 0.0F + (random.nextFloat() * 6F) / 16F;
		float f2 = k + 0.5F;
		float f3 = 0.52F;
		float f4 = random.nextFloat() * 0.6F - 0.3F;

		world.spawnParticle("reddust", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle("reddust", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle("reddust", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
		world.spawnParticle("reddust", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public void onNeighborBlockChange(final World world,
			final int x, final int y, final int z, final Block block) {
		final TileEnginePlus tile = (TileEnginePlus) world.getTileEntity(x, y, z);

		if (tile != null) {
			tile.checkRedstonePower();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		return texture;
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int metadata) {
		return null;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addCollisionBoxesToList(final World wrd, final int x, final int y, final int z, final AxisAlignedBB mask, final List list, final Entity ent) {
		final TileEntity tile = wrd.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			final AxisAlignedBB[] aabbs = boxes[((TileEnginePlus) tile).orientation.ordinal()];
			for (final AxisAlignedBB aabb : aabbs) {
				final AxisAlignedBB aabbTmp = aabb.getOffsetBoundingBox(x, y, z);
				if (mask.intersectsWith(aabbTmp)) {
					list.add(aabbTmp);
				}
			}
		} else {
			super.addCollisionBoxesToList(wrd, x, y, z, mask, list, ent);
		}
	}

	@Override
	public AxisAlignedBB[] getBoxes(final World wrd, final int x, final int y, final int z, final EntityPlayer player) {
		final TileEntity tile = wrd.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			return boxes[((TileEnginePlus) tile).orientation.ordinal()];
		} else {
			return new AxisAlignedBB[]{AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};
		}
	}

	@Override
	public double getExpansion() {
		return 0.0075;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(final World wrd, final int x, final int y, final int z, final Vec3 origin, final Vec3 direction) {
		final TileEntity tile = wrd.getTileEntity(x, y, z);
		if (tile instanceof TileEnginePlus) {
			final AxisAlignedBB[] aabbs = boxes[((TileEnginePlus) tile).orientation.ordinal()];
			MovingObjectPosition closest = null;
			for (final AxisAlignedBB aabb : aabbs) {
				final MovingObjectPosition mop = aabb.getOffsetBoundingBox(x, y, z).calculateIntercept(origin, direction);
				if (mop != null) {
					if (closest != null && mop.hitVec.distanceTo(origin) < closest.hitVec.distanceTo(origin)) {
						closest = mop;
					} else {
						closest = mop;
					}
				}
			}
			if (closest != null) {
				closest.blockX = x;
				closest.blockY = y;
				closest.blockZ = z;
			}
			return closest;
		} else {
			return super.collisionRayTrace(wrd, x, y, z, origin, direction);
		}
	}
}
