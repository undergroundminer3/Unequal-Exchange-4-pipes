package me.undergroundminer3.uee4.init2;

import cpw.mods.fml.common.registry.GameRegistry;
import me.undergroundminer3.uee4.bcReplacements.BlockEnginePlus;
import me.undergroundminer3.uee4.bcReplacements.ItemEnginePlus;
import me.undergroundminer3.uee4.bcReplacements.TileEnginePlusImpl;
import me.undergroundminer3.uee4.util2.Names2;

public final class EnginesInit {

	private EnginesInit() {};
	
	public static void init0() {
		BlockEnginePlus.INSTANCE = new BlockEnginePlus();
//		EngineRegistry.register(Names2.Engines.ENGINE_WOOD_INTERN_NAME, Names2.Engines.ENGINE_WOOD_LOC_NAME, TileEnginePlusImpl.class);
	}
	
	public static void init1() {
		GameRegistry.registerBlock(BlockEnginePlus.INSTANCE, ItemEnginePlus.class, Names2.Engines.ENGINE_TILE_NAME);
		GameRegistry.registerTileEntity(TileEnginePlusImpl.class, Names2.Engines.ENGINE_WOOD_TE);
	}
	
	public static void init2() {
		
	}
	
	public static void init3() {
		
	}
}
