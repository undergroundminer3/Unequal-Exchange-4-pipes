/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.network2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.core.network.BuildCraftChannelHandler;
import buildcraft.core.network.BuildCraftPacket;
import buildcraft.core.network.PacketCoordinates;
import buildcraft.core.network.PacketIds;
import buildcraft.core.network.PacketSlotChange;
import buildcraft.core.network.PacketUpdate;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.gui.ContainerGateInterface;
import buildcraft.transport.pipes.PipeItemsDiamond;
import buildcraft.transport.pipes.PipeItemsEmerald;
import cpw.mods.fml.common.network.NetworkRegistry;

public class PacketHandlerPipes extends UnequalExchangeChannelHandler {

	/**
	 * TODO: A lot of this is based on the player to retrieve the world.
	 * Passing a dimension id would be more appropriate. More generally, it
	 * seems like a lot of these packets could be replaced with tile-based
	 * RPCs.
	 */
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, UEEPacket packet) {
		super.decodeInto(ctx, data, packet);
		try {
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			EntityPlayer player = CoreProxy.proxy.getPlayerFromNetHandler(netHandler);

			int packetID = packet.getID();

			switch (packetID) {
				case Names2.NETIDS.POWER_UPDATE:
					onPacketPower(player, (PacketEmcPipeUpdate) packet);
					break;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the display power on a power pipe
	 *
	 * @param packetPower
	 */
	private void onPacketPower(EntityPlayer player, PacketEmcPipeUpdate packetPower) {
		World world = player.worldObj;
		if (!world.blockExists(packetPower.posX, packetPower.posY, packetPower.posZ))
			return;

		TileEntity entity = world.getTileEntity(packetPower.posX, packetPower.posY, packetPower.posZ);
		if (!(entity instanceof TileGenericPipe))
			return;

		TileGenericPipe pipe = (TileGenericPipe) entity;
		if (pipe.pipe == null)
			return;

		if (!(pipe.pipe.transport instanceof IEmcPacketAble))
			return;

		((IEmcPacketAble) pipe.pipe.transport).handlePowerPacket(packetPower);

	}
}
