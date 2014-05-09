package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emcAirEnergy.BlockAirConsumer;
import me.undergroundminer3.uee4.emcAirEnergy.BlockAirProducer;
import me.undergroundminer3.uee4.emcAirEnergy.TileAirConsumer;
import me.undergroundminer3.uee4.emcAirEnergy.TileAirProducer;
import me.undergroundminer3.uee4.util2.Names2;
import cpw.mods.fml.common.registry.GameRegistry;
import static me.undergroundminer3.uee4.emcAirEnergy.EmcAirBlocks.*;

public final class Emc1BlocksInit {

	private Emc1BlocksInit() {};
	
	public final static void init0() {
		MJTOEMCAIR = new BlockAirProducer();
		EMCAIRTOMJ = new BlockAirConsumer();
	}
	
	public final static void init1() {
		GameRegistry.registerBlock(MJTOEMCAIR, Names2.EmcMachines.CONV_MJ_EMCAIR);
		GameRegistry.registerBlock(EMCAIRTOMJ, Names2.EmcMachines.CONV_EMCAIR_MJ);
		GameRegistry.registerTileEntity(TileAirProducer.class, Names2.EmcTiles.CONV_MJ_EMC1);
		GameRegistry.registerTileEntity(TileAirConsumer.class, Names2.EmcTiles.CONV_EMC1_MJ);
	}
}
