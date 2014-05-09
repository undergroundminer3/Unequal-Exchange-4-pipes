package me.undergroundminer3.uee4.emcAirTransport2;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;

public final class PipeRenderersAir {

	private PipeRenderersAir() {};

	public static final void init3() {
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_WOOD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_COBBLESTONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_STONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_QUARTZ, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_GOLD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesAir.PIPE_AIR_DIAMOND, TransportProxyClient.pipeItemRenderer);
	}
}
