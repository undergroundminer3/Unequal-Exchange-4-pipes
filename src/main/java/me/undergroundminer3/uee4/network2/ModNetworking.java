package me.undergroundminer3.uee4.network2;

import me.undergroundminer3.uee4.energy.gui.HandlerGui;
import me.undergroundminer3.uee4.init.ModInstance;
import me.undergroundminer3.uee4.util2.Names2;
import cpw.mods.fml.common.network.NetworkRegistry;

public final class ModNetworking {
	
	private ModNetworking() {};

	public final static void init() {
		ChannelHandler.channels = NetworkRegistry.INSTANCE.newChannel
				(Names2.NETIDS.PIPE_CHANNEL_NAME, new PacketHandlerPipes());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(ModInstance.getMod(), new HandlerGui());
	}
}
