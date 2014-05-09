package me.undergroundminer3.uee4.emcAirTransport;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;

public final class PipeRenderersAir {

	private PipeRenderersAir() {};

	public static final void init3() {
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_WOOD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_COBBLESTONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_STONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_QUARTZ, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_GOLD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcAir.PIPE_EMCAIR_DIAMOND, TransportProxyClient.pipeItemRenderer);
	}
}
