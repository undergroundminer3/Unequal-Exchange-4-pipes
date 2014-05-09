package me.undergroundminer3.uee4.emcHeatTransport;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;

public final class PipeRenderersHeat {

	private PipeRenderersHeat() {};

	public static final void init3() {
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_WOOD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_COBBLESTONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_STONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_QUARTZ, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_GOLD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipesEmcHeat.PIPE_EMCHEAT_DIAMOND, TransportProxyClient.pipeItemRenderer);
	}
}
