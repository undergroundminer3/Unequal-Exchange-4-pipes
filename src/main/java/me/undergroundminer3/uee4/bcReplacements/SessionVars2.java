/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.bcReplacements;

public class SessionVars2 {

	@SuppressWarnings("rawtypes")
	private static Class openedLedger2;

	@SuppressWarnings("rawtypes")
	public static void setOpenedLedger2(final Class ledgerClass) {
		openedLedger2 = ledgerClass;
	}

	@SuppressWarnings("rawtypes")
	public static Class getOpenedLedger2() {
		return openedLedger2;
	}
}
