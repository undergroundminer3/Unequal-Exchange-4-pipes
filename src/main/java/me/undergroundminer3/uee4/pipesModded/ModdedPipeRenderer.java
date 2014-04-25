package me.undergroundminer3.uee4.pipesModded;

import javax.vecmath.Vector3d; //i have head that there has been some trouble with this, i might implement vec3d myself

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import buildcraft.BuildCraftTransport;
import buildcraft.core.CoreConstants;
import buildcraft.core.render.RenderEntityBlock;
import buildcraft.core.render.RenderEntityBlock.RenderInfo;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.TileGenericPipe;

public /*nonfinal*/
class ModdedPipeRenderer implements PipeSpecialRenderer {

	public ModdedPipeRenderer() {};

	public ModdedPipeRenderer(final Vector3d colorNormal, final Vector3d colorOverload) {
		
		//never crash this way
		overrideColors = !((colorNormal == null) && (colorOverload == null));
		
		colNorm = colorNormal == null ? vectorPipeInsideColorBlue : colorNormal;
		colOverl = colorOverload == null ? vectorPipeInsideColorRed : colorOverload; 

	}

	private boolean overrideColors;
	private Vector3d colNorm;
	private Vector3d colOverl;

	@Override
	public void renderPipeInsidesAt(final TileGenericPipe pipe, final double x,
			final double y, final double z, final AdvPipeRenderer parent) {
		
		//pipe container MUST IMPLEMENT IModdedPipeRenderable !
		doRender(pipe.pipe.getWorld(), (IModdedPipeRenderable) pipe.pipe.transport, x, y, z, parent);
	}

	public final static int DEFAULT_POW_STAGES = 100;

	public int[] displayEnergList = new int[DEFAULT_POW_STAGES];
	public int[] displayEnergListOverload = new int[DEFAULT_POW_STAGES];

	public final int[] angleY = {0, 0, 270, 90, 0, 180};
	public final int[] angleZ = {90, 270, 0, 0, 0, 0};

	public static final Vector3d vectorPipeInsideColorBlue = new Vector3d(0, 1.0D, 1.0D);
	public static final Vector3d vectorPipeInsideColorRed = new Vector3d(1.0D, 0.2D, 0.2D); //DO NOT HURT THE PLAYERS' EYES

	protected void doRender(final World w, final IModdedPipeRenderable mpr,
			final double x, final double y, final double z, final AdvPipeRenderer parent) {

		if (overrideColors) {
			initializeDisplayList(w,
					colNorm, colOverl);
		} else {
			initializeDisplayList(w,
					vectorPipeInsideColorBlue, vectorPipeInsideColorRed);
		}

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		//					GL11.glEnable(GL11.GL_BLEND);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		parent.bindTexture(TextureMap.locationBlocksTexture);

		int[] displayList = mpr.getOverload() > 0 ? displayEnergListOverload : displayEnergList;
		short[] powerDisplay = mpr.getClientDisplayPower();

		for (int side = 0; side < 6; ++side) {
			GL11.glPushMatrix();

			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(angleY[side], 0, 1, 0);
			GL11.glRotatef(angleZ[side], 0, 0, 1);
			float scale = 1.0F - side * 0.0001F;
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			short stage = powerDisplay[side];
			if (stage >= 1) {
				if (stage < displayList.length) {
					GL11.glCallList(displayList[stage]);
				} else {
					GL11.glCallList(displayList[displayList.length - 1]);
				}
			}

			GL11.glPopMatrix();
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	protected boolean dpList_initialized = false;

	protected void initializeDisplayList(final World world, final Vector3d colorNormal, final Vector3d colorOverload) {

		//TODO implement color

		if (dpList_initialized)
			return;

		dpList_initialized = true;

		RenderInfo block = new RenderInfo();
		block.texture = BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.Power_Normal.ordinal());

		float size = CoreConstants.PIPE_MAX_POS - CoreConstants.PIPE_MIN_POS;

		for (int s = 0; s < DEFAULT_POW_STAGES; ++s) {
			displayEnergList[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayEnergList[s], 4864 /* GL_COMPILE */);

			float minSize = 0.005F;

			float unit = (size - minSize) / 2F / DEFAULT_POW_STAGES;

			block.minY = 0.5 - (minSize / 2F) - unit * s;
			block.maxY = 0.5 + (minSize / 2F) + unit * s;

			block.minZ = 0.5 - (minSize / 2F) - unit * s;
			block.maxZ = 0.5 + (minSize / 2F) + unit * s;

			block.minX = 0;
			block.maxX = 0.5 + (minSize / 2F) + unit * s;

			RenderEntityBlock.INSTANCE.renderBlock(block, world, 0, 0, 0, false, true);

			GL11.glEndList();
		}

		block.texture = BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.Power_Overload.ordinal());

		size = CoreConstants.PIPE_MAX_POS - CoreConstants.PIPE_MIN_POS;

		for (int s = 0; s < DEFAULT_POW_STAGES; ++s) {
			displayEnergListOverload[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayEnergListOverload[s], 4864 /* GL_COMPILE */);

			float minSize = 0.005F;

			float unit = (size - minSize) / 2F / DEFAULT_POW_STAGES;

			block.minY = 0.5 - (minSize / 2F) - unit * s;
			block.maxY = 0.5 + (minSize / 2F) + unit * s;

			block.minZ = 0.5 - (minSize / 2F) - unit * s;
			block.maxZ = 0.5 + (minSize / 2F) + unit * s;

			block.minX = 0;
			block.maxX = 0.5 + (minSize / 2F) + unit * s;

			RenderEntityBlock.INSTANCE.renderBlock(block, world, 0, 0, 0, false, true);

			GL11.glEndList();
		}
	}


}
