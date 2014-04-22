package me.undergroundminer3.uee4.emc1transport;

import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;

public class PipeRenderers1 {

	public static void init() {
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_WOOD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_COBBLESTONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_STONE, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_QUARTZ, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_GOLD, TransportProxyClient.pipeItemRenderer);
		MinecraftForgeClient.registerItemRenderer(ModPipes1.PIPE_EMC1_DIAMOND, TransportProxyClient.pipeItemRenderer);
	}
}
