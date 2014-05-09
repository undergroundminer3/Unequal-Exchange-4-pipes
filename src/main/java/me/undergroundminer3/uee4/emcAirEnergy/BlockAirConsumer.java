package me.undergroundminer3.uee4.emcAirEnergy;

import me.undergroundminer3.uee4.abstacts.BlockEE_BC;
import me.undergroundminer3.uee4.creativetab.CreativeTab;
import me.undergroundminer3.uee4.reference.Textures;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.core.IItemPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAirConsumer extends BlockEE_BC {

	public BlockAirConsumer() {
		super(Material.iron);
		this.setBlockName(Names2.EmcMachines.CONV_EMCAIR_MJ);
	}

	@Override
	public boolean isOpaqueCube() {
		this.setCreativeTab(CreativeTab.EE3_TAB);
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	protected String getUnwrappedUnlocalizedName(final String unlocalizedName)
	{
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public String getUnlocalizedName()
	{
		return String.format("tile.%s%s", Textures.RESOURCE_PREFIX, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	private IIcon grateIcon;
	private IIcon gearIcon;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		grateIcon = register.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())+"_grate"));
		gearIcon = register.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())+"_gear"));
	}

	@Override
	public int getRenderType() {
		//		return BuildCraftCore.blockByEntityModel;
		return super.getRenderType();
	}

	@Override
	public TileEntity createTileEntity(final World world, final int metadata) {
		//		if (metadata == 1)
		return new TileAirConsumer();
		//		else if (metadata == 2)
		//			return new TileEngineIron();
		//		else
		//			return new TileEngineWood();
	}

	@Override
	public boolean isSideSolid(final IBlockAccess world,
			final int x, final int y, final int z, final ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileAirConsumer) {
			//			return ((TileAirConsumer) tile).orientation.getOpposite() == side;
			return true;
		}
		return false;
	}

	@Override
	public boolean rotateBlock(final World world,
			final int x, final int y, final int z, final ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileAirConsumer) {
			//			return ((TileEngine) tile).switchOrientation(false);
			return false;
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(final World world,
			final int i, final int j, final int k, final EntityPlayer player,
			final int side, final float par7, final float par8, final float par9) {

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

//	@Override
//	public void onPostBlockPlaced(final World world, final int x, final int y, final int z, final int par5) {
////		TileAirConsumer tile = (TileAirConsumer) world.getTileEntity(x, y, z);
//		//		tile.orientation = ForgeDirection.UP;
//		//		if (!tile.isOrientationValid())
//		//			tile.switchOrientation(true);
//	}

//	@Override
//	public int damageDropped(final int i) {
//		return i;
//	}

//	@SuppressWarnings({"all"})
//	@Override
//	public void randomDisplayTick(final World world,
//			final int i, final int j, final int k, final Random random) {
//		//		TileEngine tile = (TileEngine) world.getTileEntity(i, j, k);
//		//
//		//		if (!tile.isBurning())
//		//			return;
//		//
//		//		float f = (float) i + 0.5F;
//		//		float f1 = (float) j + 0.0F + (random.nextFloat() * 6F) / 16F;
//		//		float f2 = (float) k + 0.5F;
//		//		float f3 = 0.52F;
//		//		float f4 = random.nextFloat() * 0.6F - 0.3F;
//		//
//		//		world.spawnParticle("reddust", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
//		//		world.spawnParticle("reddust", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
//		//		world.spawnParticle("reddust", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
//		//		world.spawnParticle("reddust", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
//	}

////	@SuppressWarnings({"unchecked", "rawtypes"})
//	@SuppressWarnings({"rawtypes"})
//	@Override
//	public void getSubBlocks(final Item item, final CreativeTabs par2CreativeTabs, final List itemList) {
//		//		itemList.add(new ItemStack(this, 1, 0));
//		//		itemList.add(new ItemStack(this, 1, 1));
//		//		itemList.add(new ItemStack(this, 1, 2));
//	}

//	@Override
//	public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block block) {
//		TileAirConsumer tile = (TileAirConsumer) world.getTileEntity(x, y, z);
//
//		if (tile != null) {
//			//			tile.checkRedstonePower();
//		}
//	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		//		return grateIcon;
		//		switch (meta) {
		////			case 0:
		////				return woodTexture;
		////			case 1:
		////				return stoneTexture;
		////			case 2:
		////				return ironTexture;
		////			default:
		////				return null;
		//		}
		switch (side) {
		case 0:
		case 1:
			return grateIcon;
		default:
			return gearIcon;
		}
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int metadata) {
		//		return new TileAirConsumer();
		return null;
	}
}
