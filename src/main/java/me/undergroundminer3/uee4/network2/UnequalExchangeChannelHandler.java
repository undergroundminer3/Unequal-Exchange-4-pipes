/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.network2;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UnequalExchangeChannelHandler extends FMLIndexedMessageToMessageCodec<UEEPacket> {
	
    public UnequalExchangeChannelHandler() {
    	addDiscriminator(0, PacketEmcPipeUpdate.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, UEEPacket packet, ByteBuf data) throws Exception {
        packet.writeData(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, UEEPacket packet) {
        packet.readData(data);
    }
}
