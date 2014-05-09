package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatCobblestone;
import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatDiamond;
import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatGold;
import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatQuartz;
import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatStone;
import me.undergroundminer3.uee4.emcHeatTransport.PipeEmcHeatWood;
import me.undergroundminer3.uee4.emctransport.EmcPipeTypes;
import me.undergroundminer3.uee4.emctransport.EmcRegistryHack;
import me.undergroundminer3.uee4.pipesModded.AdvPipeRenderer;
import me.undergroundminer3.uee4.pipesModded.ModdedPipeRenderer;
import me.undergroundminer3.uee4.util2.Names2;
import static me.undergroundminer3.uee4.emcHeatTransport.ModPipesEmcHeat.*;

public final class Pipes2Init {

	private Pipes2Init() {};

	public static final void init0() {
		PIPE_EMCHEAT_WOOD = EmcRegistryHack.registerPipe(PipeEmcHeatWood.class, Names2.Pipes.EMC2_WOOD_PIPE);
		PIPE_EMCHEAT_STONE = EmcRegistryHack.registerPipe(PipeEmcHeatStone.class, Names2.Pipes.EMC2_STONE_PIPE);
		PIPE_EMCHEAT_COBBLESTONE = EmcRegistryHack.registerPipe(PipeEmcHeatCobblestone.class, Names2.Pipes.EMC2_COBBLESTONE_PIPE);
		PIPE_EMCHEAT_QUARTZ = EmcRegistryHack.registerPipe(PipeEmcHeatQuartz.class, Names2.Pipes.EMC2_QUARTZ_PIPE);
		PIPE_EMCHEAT_GOLD = EmcRegistryHack.registerPipe(PipeEmcHeatGold.class, Names2.Pipes.EMC2_GOLD_PIPE);
		PIPE_EMCHEAT_DIAMOND = EmcRegistryHack.registerPipe(PipeEmcHeatDiamond.class, Names2.Pipes.EMC2_DIAMOND_PIPE);

	}

	@SuppressWarnings("all")
	public static final void init1() {
		AdvPipeRenderer.registerRenderer(EmcPipeTypes.EmcThermal, new ModdedPipeRenderer() );
	}

}
