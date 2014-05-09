package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emcAirTransport.PipeRenderersAir;
import me.undergroundminer3.uee4.emcHeatTransport.PipeRenderersHeat;
import me.undergroundminer3.uee4.pipesModded.AdvPipeRenderer;
import me.undergroundminer3.uee4.util2.LogHelper;
import buildcraft.transport.TileDummyGenericPipe;
import buildcraft.transport.TileDummyGenericPipe2;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.client.registry.ClientRegistry;

public final class InitPipeRenderers {

	private InitPipeRenderers() {};

	public final static void init3() {
		AdvPipeRenderer rp = new AdvPipeRenderer();

		ClientRegistry.bindTileEntitySpecialRenderer(TileDummyGenericPipe.class, rp);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDummyGenericPipe2.class, rp);
		ClientRegistry.bindTileEntitySpecialRenderer(TileGenericPipe.class, rp);

		LogHelper.info("[UEE4] Sucessfully injected Pipe Render Hack");

		PipeRenderersAir.init3();
		PipeRenderersHeat.init3();
	}
}
