package me.undergroundminer3.uee4.emc1energy;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class Emc1Blocks {

	public static Block MJTOEMC1 = new BlockAirProducer();
	public static Block EMC1TOMJ = new BlockAirConsumer();
	
	public static void init() {
		GameRegistry.registerBlock(MJTOEMC1, "mjToEmc1Block");
		GameRegistry.registerBlock(EMC1TOMJ, "emc1ToMjBlock");
		GameRegistry.registerTileEntity(TileAirProducer.class, "mjToEmc1Tile");
		GameRegistry.registerTileEntity(TileAirConsumer.class, "emc1ToMjTile");
	}
}
