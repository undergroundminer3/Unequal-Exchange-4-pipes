package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emc1energy.BlockAirConsumer;
import me.undergroundminer3.uee4.emc1energy.BlockAirProducer;
import me.undergroundminer3.uee4.emc1energy.TileAirConsumer;
import me.undergroundminer3.uee4.emc1energy.TileAirProducer;
import me.undergroundminer3.uee4.util2.Names2;
import cpw.mods.fml.common.registry.GameRegistry;
import static me.undergroundminer3.uee4.emc1energy.Emc1Blocks.*;

public final class Emc1BlocksInit {

	private Emc1BlocksInit() {};
	
	public final static void init0() {
		MJTOEMC1 = new BlockAirProducer();
		EMC1TOMJ = new BlockAirConsumer();
	}
	
	public final static void init1() {
		GameRegistry.registerBlock(MJTOEMC1, Names2.EmcMachines.CONV_MJ_EMC1);
		GameRegistry.registerBlock(EMC1TOMJ, Names2.EmcMachines.CONV_EMC1_MJ);
		GameRegistry.registerTileEntity(TileAirProducer.class, Names2.EmcTiles.CONV_MJ_EMC1);
		GameRegistry.registerTileEntity(TileAirConsumer.class, Names2.EmcTiles.CONV_EMC1_MJ);
	}
}
