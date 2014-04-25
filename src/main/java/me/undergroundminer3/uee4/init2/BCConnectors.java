package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emc1transport.PipeEmc1Cobblestone;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Quartz;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Stone;
import me.undergroundminer3.uee4.emc1transport.PipeEmc1Wood;
import me.undergroundminer3.uee4.util2.Names2;
import buildcraft.transport.ItemFacade;
import buildcraft.transport.PipeConnectionBans;

public final class BCConnectors { //buildcraft connectors

	private BCConnectors() {};
	
	public static final void init2() {
		ItemFacade.blacklistedFacades.add(Names2.FacadeBlacklist.CONV_EMC1_MJ);
		ItemFacade.blacklistedFacades.add(Names2.FacadeBlacklist.CONV_MJ_EMC1);
		
		PipeConnectionBans.banConnection(PipeEmc1Wood.class);
		PipeConnectionBans.banConnection(PipeEmc1Stone.class, PipeEmc1Cobblestone.class);
		PipeConnectionBans.banConnection(PipeEmc1Stone.class, PipeEmc1Quartz.class);
		PipeConnectionBans.banConnection(PipeEmc1Cobblestone.class, PipeEmc1Quartz.class);
	}
	
	public static final void init3() {
		
	}
}
