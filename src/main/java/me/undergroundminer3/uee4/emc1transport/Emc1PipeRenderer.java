package me.undergroundminer3.uee4.emc1transport;

import me.undergroundminer3.uee4.emctransport.AdvPipeRenderer;
import me.undergroundminer3.uee4.emctransport.PipeSpecialRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import buildcraft.BuildCraftTransport;
import buildcraft.core.CoreConstants;
import buildcraft.core.render.RenderEntityBlock;
import buildcraft.core.render.RenderEntityBlock.RenderInfo;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.TileGenericPipe;

public class Emc1PipeRenderer implements PipeSpecialRenderer{

	@Override
	public void renderPipeInsidesAt(TileGenericPipe pipe, double x,
			double y, double z, AdvPipeRenderer parent) {
		
		doRender(pipe.pipe, x, y, z, parent);
	}

	final static private int EMC1_STAGES = 100;

	public int[] displayEmc1List = new int[EMC1_STAGES];
	public int[] displayEmc1ListOverload = new int[EMC1_STAGES];
	
	private final int[] angleY = {0, 0, 270, 90, 0, 180};
	private final int[] angleZ = {90, 270, 0, 0, 0, 0};

	private void doRender(Pipe<PipeTransportEmc1> pipe, double x, double y, double z, AdvPipeRenderer parent) {
		initializeDisplayEmc1List(pipe.container.getWorldObj());

		PipeTransportEmc1 pow = pipe.transport;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		//					GL11.glEnable(GL11.GL_BLEND);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		parent.bindTexture(TextureMap.locationBlocksTexture);

		int[] displayList = pow.overload > 0 ? displayEmc1ListOverload : displayEmc1List;

		for (int side = 0; side < 6; ++side) {
			GL11.glPushMatrix();

			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(angleY[side], 0, 1, 0);
			GL11.glRotatef(angleZ[side], 0, 0, 1);
			float scale = 1.0F - side * 0.0001F;
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			short stage = pow.clientDisplayEmc1[side];
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
	
	boolean initialized = false;

	private void initializeDisplayEmc1List(World world) {
		if (initialized)
			return;

		initialized = true;

		RenderInfo block = new RenderInfo();
		block.texture = BuildCraftTransport.instance.pipeIconProvider.getIcon(PipeIconProvider.TYPE.Power_Normal.ordinal());

		float size = CoreConstants.PIPE_MAX_POS - CoreConstants.PIPE_MIN_POS;

		for (int s = 0; s < EMC1_STAGES; ++s) {
			displayEmc1List[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayEmc1List[s], 4864 /* GL_COMPILE */);

			float minSize = 0.005F;

			float unit = (size - minSize) / 2F / EMC1_STAGES;

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

		for (int s = 0; s < EMC1_STAGES; ++s) {
			displayEmc1ListOverload[s] = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(displayEmc1ListOverload[s], 4864 /* GL_COMPILE */);

			float minSize = 0.005F;

			float unit = (size - minSize) / 2F / EMC1_STAGES;

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
