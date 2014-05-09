package me.undergroundminer3.uee4.init2;

import me.undergroundminer3.uee4.emcAirTransport.PipeEmcAirCobblestone;
import me.undergroundminer3.uee4.emcAirTransport.PipeEmcAirQuartz;
import me.undergroundminer3.uee4.emcAirTransport.PipeEmcAirStone;
import me.undergroundminer3.uee4.emcAirTransport.PipeEmcAirWood;
import me.undergroundminer3.uee4.util2.Names2;
import buildcraft.transport.ItemFacade;
import buildcraft.transport.PipeConnectionBans;

public final class BCConnectors { //buildcraft connectors

	private BCConnectors() {};
	
	public static final void init2() {
		ItemFacade.blacklistedFacades.add(Names2.FacadeBlacklist.CONV_EMC1_MJ);
		ItemFacade.blacklistedFacades.add(Names2.FacadeBlacklist.CONV_MJ_EMC1);
		
		PipeConnectionBans.banConnection(PipeEmcAirWood.class);
		PipeConnectionBans.banConnection(PipeEmcAirStone.class, PipeEmcAirCobblestone.class);
		PipeConnectionBans.banConnection(PipeEmcAirStone.class, PipeEmcAirQuartz.class);
		PipeConnectionBans.banConnection(PipeEmcAirCobblestone.class, PipeEmcAirQuartz.class);
	}
	
	public static final void init3() {
		//bc will have been init before this
		GateHack.rmTriggerProvider();
	}
}
