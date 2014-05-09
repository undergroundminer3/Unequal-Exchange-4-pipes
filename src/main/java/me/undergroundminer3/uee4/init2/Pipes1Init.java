package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emcAirTransport2.PipeAirCobblestone;
import me.undergroundminer3.uee4.emcAirTransport2.PipeAirDiamond;
import me.undergroundminer3.uee4.emcAirTransport2.PipeAirGold;
import me.undergroundminer3.uee4.emcAirTransport2.PipeAirQuartz;
import me.undergroundminer3.uee4.emcAirTransport2.PipeAirStone;
import me.undergroundminer3.uee4.emcAirTransport2.PipeAirWood;
import me.undergroundminer3.uee4.emctransport.EmcPipeTypes;
import me.undergroundminer3.uee4.emctransport.EmcRegistryHack;
import me.undergroundminer3.uee4.pipesModded.AdvPipeRenderer;
import me.undergroundminer3.uee4.pipesModded.ModdedPipeRenderer;
import me.undergroundminer3.uee4.util2.Names2;
//import static me.undergroundminer3.uee4.emcAirTransport.ModPipesEmcAir.*;
import static me.undergroundminer3.uee4.emcAirTransport2.ModPipesAir.*;

public final class Pipes1Init {

	private Pipes1Init() {};

	public static final void init0() {
		PIPE_AIR_WOOD = EmcRegistryHack.registerPipe(PipeAirWood.class, Names2.Pipes.EMC1_WOOD_PIPE);
		PIPE_AIR_STONE = EmcRegistryHack.registerPipe(PipeAirStone.class, Names2.Pipes.EMC1_STONE_PIPE);
		PIPE_AIR_COBBLESTONE = EmcRegistryHack.registerPipe(PipeAirCobblestone.class, Names2.Pipes.EMC1_COBBLESTONE_PIPE);
		PIPE_AIR_QUARTZ = EmcRegistryHack.registerPipe(PipeAirQuartz.class, Names2.Pipes.EMC1_QUARTZ_PIPE);
		PIPE_AIR_GOLD = EmcRegistryHack.registerPipe(PipeAirGold.class, Names2.Pipes.EMC1_GOLD_PIPE);
		PIPE_AIR_DIAMOND = EmcRegistryHack.registerPipe(PipeAirDiamond.class, Names2.Pipes.EMC1_DIAMOND_PIPE);

	}

	@SuppressWarnings("all")
	public static final void init1() {
		AdvPipeRenderer.registerRenderer(EmcPipeTypes.EmcAir, new ModdedPipeRenderer() );
	}

}
