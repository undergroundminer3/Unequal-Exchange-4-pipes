package me.undergroundminer3.uee4.bcReplacements;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.undergroundminer3.uee4.client.renderer.blockentity.Renderers;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public final class TileRenderDelegateRegistry {

	private TileRenderDelegateRegistry() {};
	
	public static final float[] angleMap = new float[6];

	static {
		angleMap[EAST.ordinal()] = (float) -Math.PI / 2;
		angleMap[WEST.ordinal()] = (float) Math.PI / 2;
		angleMap[UP.ordinal()] = 0;
		angleMap[DOWN.ordinal()] = (float) Math.PI;
		angleMap[SOUTH.ordinal()] = (float) Math.PI / 2;
		angleMap[NORTH.ordinal()] = (float) -Math.PI / 2;
	}

	public static final Map<Block, IBlockTileRenderDelegate> blockByEntityRenders = new HashMap<Block, IBlockTileRenderDelegate>();

	public static void register(final Block b, final Class<? extends TileEntity> c, final IBlockTileRenderDelegate r) {
		blockByEntityRenders.put(b, r);
		ClientRegistry.bindTileEntitySpecialRenderer(c, Renderers.dynamicBlockEntityRendererWorld);
	}

}
