package me.undergroundminer3.uee4.emc1transport;

import net.minecraft.item.Item;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.pipes.PipePowerWood;
import me.undergroundminer3.uee4.emctransport.EmcRegistryHack;
import me.undergroundminer3.uee4.util2.Names2;

public class ModPipes1 {
	
	public static Item PIPE_EMC1_WOOD;
	public static Item PIPE_EMC1_STONE;
	public static Item PIPE_EMC1_COBBLESTONE;
	public static Item PIPE_EMC1_QUARTZ;
	public static Item PIPE_EMC1_GOLD;
	public static Item PIPE_EMC1_DIAMOND;

	public static void init() {
		PIPE_EMC1_WOOD = EmcRegistryHack.registerPipe(PipeEmc1Wood.class, Names2.Pipes.EMC1_WOOD_PIPE);
		PIPE_EMC1_STONE = EmcRegistryHack.registerPipe(PipeEmc1Stone.class, Names2.Pipes.EMC1_STONE_PIPE);
		PIPE_EMC1_COBBLESTONE = EmcRegistryHack.registerPipe(PipeEmc1Cobblestone.class, Names2.Pipes.EMC1_COBBLESTONE_PIPE);
		PIPE_EMC1_QUARTZ = EmcRegistryHack.registerPipe(PipeEmc1Quartz.class, Names2.Pipes.EMC1_QUARTZ_PIPE);
		PIPE_EMC1_GOLD = EmcRegistryHack.registerPipe(PipeEmc1Gold.class, Names2.Pipes.EMC1_GOLD_PIPE);
		PIPE_EMC1_DIAMOND = EmcRegistryHack.registerPipe(PipeEmc1Diamond.class, Names2.Pipes.EMC1_DIAMOND_PIPE);
		
		PipeConnectionBans.banConnection(PipeEmc1Wood.class);
		PipeConnectionBans.banConnection(PipeEmc1Stone.class, PipeEmc1Cobblestone.class);
		PipeConnectionBans.banConnection(PipeEmc1Stone.class, PipeEmc1Quartz.class);
		PipeConnectionBans.banConnection(PipeEmc1Cobblestone.class, PipeEmc1Quartz.class);
	}
}
