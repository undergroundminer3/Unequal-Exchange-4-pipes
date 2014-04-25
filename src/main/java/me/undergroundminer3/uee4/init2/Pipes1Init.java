package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emc1transport.PipeEmc1Cobblestone;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Diamond;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Gold;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Quartz;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Stone;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Wood;
import me.undergroundminer3.uee4.emctransport.EmcPipeTypes;
import me.undergroundminer3.uee4.emctransport.EmcRegistryHack;
import me.undergroundminer3.uee4.pipesModded.AdvPipeRenderer;
import me.undergroundminer3.uee4.pipesModded.ModdedPipeRenderer;
import me.undergroundminer3.uee4.util2.Names2;
import static me.undergroundminer3.uee4.emc1transport.ModPipes1.*;

public final class Pipes1Init {

	private Pipes1Init() {};

	public static final void init0() {
		PIPE_EMC1_WOOD = EmcRegistryHack.registerPipe(PipeEmc1Wood.class, Names2.Pipes.EMC1_WOOD_PIPE);
		PIPE_EMC1_STONE = EmcRegistryHack.registerPipe(PipeEmc1Stone.class, Names2.Pipes.EMC1_STONE_PIPE);
		PIPE_EMC1_COBBLESTONE = EmcRegistryHack.registerPipe(PipeEmc1Cobblestone.class, Names2.Pipes.EMC1_COBBLESTONE_PIPE);
		PIPE_EMC1_QUARTZ = EmcRegistryHack.registerPipe(PipeEmc1Quartz.class, Names2.Pipes.EMC1_QUARTZ_PIPE);
		PIPE_EMC1_GOLD = EmcRegistryHack.registerPipe(PipeEmc1Gold.class, Names2.Pipes.EMC1_GOLD_PIPE);
		PIPE_EMC1_DIAMOND = EmcRegistryHack.registerPipe(PipeEmc1Diamond.class, Names2.Pipes.EMC1_DIAMOND_PIPE);


	}

	@SuppressWarnings("all")
	public static final void init1() {
		AdvPipeRenderer.registerRenderer(EmcPipeTypes.EMC1, new ModdedPipeRenderer() );
	}

}
