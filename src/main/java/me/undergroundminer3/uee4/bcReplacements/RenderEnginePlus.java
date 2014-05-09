package me.undergroundminer3.uee4.bcReplacements;

import org.lwjgl.opengl.GL11;

import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftCore.RenderMode;
import buildcraft.energy.TileEngine;
import me.undergroundminer3.uee4.client.renderer.blockentity.Renderers;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import static me.undergroundminer3.uee4.bcReplacements.TileRenderDelegateRegistry.angleMap;

public class RenderEnginePlus implements IBlockTileRenderDelegate {

	@Override
	public void inventoryRender(final double x, final double y, final double z,
			final float f, final float f1, final Block b, final int meta) {
//		renderEngine(0.25F, ForgeDirection.UP, x, y, z);
		renderEngine(RENDER_PLAIN, (float) x, (float) y, (float) z, ForgeDirection.UP);
	}
	
	public static final EnginePlusRenderData RENDER_PLAIN = new EnginePlusRenderData(
		TileEngine.TRUNK_BLUE_TEXTURE, TileEngine.CHAMBER_TEXTURES[0], TileEngine.BASE_TEXTURES[0], 0.25F
	);

	private static ModelBase model = new ModelBase() {};

	public static ModelRenderer box;
	public static ModelRenderer trunk;
	public static ModelRenderer movingBox;
	public static ModelRenderer chamber;

	static {
		box = new ModelRenderer(model, 0, 1);
		box.addBox(-8F, -8F, -8F, 16, 4, 16);
		box.rotationPointX = 8;
		box.rotationPointY = 8;
		box.rotationPointZ = 8;

		trunk = new ModelRenderer(model, 1, 1);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.rotationPointX = 8F;
		trunk.rotationPointY = 8F;
		trunk.rotationPointZ = 8F;

		movingBox = new ModelRenderer(model, 0, 1);
		movingBox.addBox(-8F, -4, -8F, 16, 4, 16);
		movingBox.rotationPointX = 8F;
		movingBox.rotationPointY = 8F;
		movingBox.rotationPointZ = 8F;

		chamber = new ModelRenderer(model, 1, 1);
		chamber.addBox(-5F, -4, -5F, 10, 2, 10);
		chamber.rotationPointX = 8F;
		chamber.rotationPointY = 8F;
		chamber.rotationPointZ = 8F;
	}

	public static void renderEngine(final EnginePlusRenderData data,
			final float x, final float y, final float z, final ForgeDirection orientation) {
		if (BuildCraftCore.render == RenderMode.NoDynamic) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColor3f(1, 1, 1);

		GL11.glTranslatef(x, y, z);

		float step;

		if (data.progress > 0.5) {
			step = 7.99F - (data.progress - 0.5F) * 2F * 7.99F;
		} else {
			step = data.progress * 2F * 7.99F;
		}

		float translatefact = step / 16;

		float[] angle = {0, 0, 0};
		float[] translate = {orientation.offsetX, orientation.offsetY, orientation.offsetZ};

		switch (orientation) {
		case EAST:
		case WEST:
		case DOWN:
			angle[2] = angleMap[orientation.ordinal()];
			break;
		case SOUTH:
		case NORTH:
		default:
			angle[0] = angleMap[orientation.ordinal()];
			break;
		}

		box.rotateAngleX = angle[0];
		box.rotateAngleY = angle[1];
		box.rotateAngleZ = angle[2];

		trunk.rotateAngleX = angle[0];
		trunk.rotateAngleY = angle[1];
		trunk.rotateAngleZ = angle[2];

		movingBox.rotateAngleX = angle[0];
		movingBox.rotateAngleY = angle[1];
		movingBox.rotateAngleZ = angle[2];

		chamber.rotateAngleX = angle[0];
		chamber.rotateAngleY = angle[1];
		chamber.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		((RenderTileEntity_BC) Renderers.dynamicBlockEntityRendererWorld).bindTexture(data.BASE_TEXTURE);

		box.render(factor);

		GL11.glTranslatef(translate[0] * translatefact, translate[1] * translatefact, translate[2] * translatefact);
		movingBox.render(factor);
		GL11.glTranslatef(-translate[0] * translatefact, -translate[1] * translatefact, -translate[2] * translatefact);

		((RenderTileEntity_BC) Renderers.dynamicBlockEntityRendererWorld).bindTexture(data.CHAMBER_TEXTURE);

		float chamberf = 2F / 16F;

		for (int i = 0; i <= step + 2; i += 2) {
			chamber.render(factor);
			GL11.glTranslatef(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
		}

		for (int i = 0; i <= step + 2; i += 2) {
			GL11.glTranslatef(-translate[0] * chamberf, -translate[1] * chamberf, -translate[2] * chamberf);
		}

		((RenderTileEntity_BC) Renderers.dynamicBlockEntityRendererWorld).bindTexture(data.TRUNK_TEXTURE);

		trunk.render(factor);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void worldRender(final TileEntity tile, final double x,
			final double y, final double z, final double f) {
		if ((tile != null)&&(tile instanceof TileEnginePlus)) {
			((TileEnginePlus) tile).doRender_internalcall(x, y, z, f);
		}

	}

}
