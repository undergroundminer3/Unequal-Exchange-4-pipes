package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.bcReplacements.BlockEnginePlus;
import me.undergroundminer3.uee4.bcReplacements.RenderEnginePlus;
import me.undergroundminer3.uee4.bcReplacements.TileEnginePlus;
import me.undergroundminer3.uee4.bcReplacements.TileRenderDelegateRegistry;

public final class ThermalRenderersInit {
	
	private ThermalRenderersInit() {};

	public static final void init3() {
		TileRenderDelegateRegistry.register(BlockEnginePlus.INSTANCE , TileEnginePlus.class, new RenderEnginePlus());
	}
}
