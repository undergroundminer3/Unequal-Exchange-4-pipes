package me.undergroundminer3.uee4.network2;

import me.undergroundminer3.uee4.util2.Names2;
import buildcraft.core.DefaultProps;
import buildcraft.transport.network.PacketHandlerTransport;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ModNetworking {

	public static void init() {
		ChannelHandler.channels = NetworkRegistry.INSTANCE.newChannel
				(Names2.NETIDS.PIPE_CHANNEL_NAME, new PacketHandlerPipes());
	}
}
