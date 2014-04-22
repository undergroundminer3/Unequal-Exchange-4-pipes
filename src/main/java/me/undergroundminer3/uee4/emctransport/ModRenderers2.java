package me.undergroundminer3.uee4.emctransport;

import me.undergroundminer3.uee4.emc1transport.PipeRenderers1;
import me.undergroundminer3.uee4.util2.LogHelper;
import buildcraft.transport.TileDummyGenericPipe;
import buildcraft.transport.TileDummyGenericPipe2;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.render.PipeRendererTESR;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ModRenderers2 {

	public static void init() {
		AdvPipeRenderer rp = new AdvPipeRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileDummyGenericPipe.class, rp);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDummyGenericPipe2.class, rp);
		ClientRegistry.bindTileEntitySpecialRenderer(TileGenericPipe.class, rp);
		LogHelper.info("[UEE4] Sucessfully injected Pipe Render Hack");
		PipeRenderers1.init();
	}
}
