/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.energy.gui.widget;

import cpw.mods.fml.client.FMLClientHandler;
import io.netty.buffer.ByteBuf;
import me.undergroundminer3.uee4.energy.gui.ContainerEE_BC;
import me.undergroundminer3.uee4.network2.UEEPipesPacket;
import me.undergroundminer3.uee4.util2.Names2;
import net.minecraft.entity.player.EntityPlayer;

public class PacketWidget extends UEEPipesPacket {

    private byte windowId, widgetId;
    private byte[] payload;

    public PacketWidget() {
        super();
    }

    public PacketWidget(final int windowId, final int widgetId, final byte[] data) {
        this.windowId = (byte) windowId;
        this.widgetId = (byte) widgetId;
        this.payload = data;
    }

    @Override
    public void writeData(final ByteBuf data) {
        data.writeByte(windowId);
        data.writeByte(widgetId);
        data.writeBytes(payload);
    }

    @Override
    public void readData(final ByteBuf data) {
        windowId = data.readByte();
        widgetId = data.readByte();

        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

        if (player.openContainer instanceof ContainerEE_BC && player.openContainer.windowId == windowId) {
            ((ContainerEE_BC) player.openContainer).handleWidgetClientData(widgetId, data);
        }
    }

    @Override
    public int getID() {
        return Names2.NETIDS.GUI_WIDGET;
    }

}
