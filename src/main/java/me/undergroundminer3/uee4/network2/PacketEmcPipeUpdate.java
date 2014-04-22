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


import me.undergroundminer3.uee4.util2.Names2;

public class PacketEmcPipeUpdate extends PacketCoord {

	public boolean overload;
	public short[] displayPower;

	public PacketEmcPipeUpdate() {
	}

	public PacketEmcPipeUpdate(int x, int y, int z) {
		super(Names2.NETIDS.POWER_UPDATE, x, y, z);
	}

	@Override
	public void readData(ByteBuf data) {		
		super.readData(data);
		displayPower = new short[] { 0, 0, 0, 0, 0, 0 };
		overload = data.readBoolean();
		for (int i = 0; i < displayPower.length; i++) {
			displayPower[i] = data.readByte();
		}
	}

	@Override
	public void writeData(ByteBuf data) {
		super.writeData(data);
		data.writeBoolean(overload);
		for (int i = 0; i < displayPower.length; i++) {
			data.writeByte(displayPower[i]);
		}
	}
}
