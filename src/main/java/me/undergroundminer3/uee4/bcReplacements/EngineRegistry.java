package me.undergroundminer3.uee4.bcReplacements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.undergroundminer3.uee4.util2.AbstractStringType;
import me.undergroundminer3.uee4.util2.LogHelper;

public final class EngineRegistry {

	private EngineRegistry() {};

	private static final Map<Integer, String> nameMap = new HashMap<Integer, String>();
	private static final Map<Integer, Class<? extends TileEnginePlus>> tileMap = new HashMap<Integer, Class<? extends TileEnginePlus>>();
	private static final ArrayList<Integer> validEngineList = new ArrayList<Integer>();
	
	//here's a list of hashcodes NOT to collide with:
	
	/*
	 * If there's a collision with your engine type, please tell undergroundminer3, he can try to fix it.
	 * 
	 * coal | -25353
	 * coalfired | 32367
	 * redstone | 28180
	 * blue | -25710
	 * red | 6393
	 * yellow | 25620
	 * green | 30027
	 * purple | -19476
	 * orange | 16086
	 * magmantic | 5071
	 * black | 15751
	 * iron | -23048
	 * gold | -23912
	 * obsidian | -17261
	 * lapis | -28763
	 * lapiz | -28756
	 * charcoal | 27869
	 * steam | -6040
	 * hobby | 31242
	 * hobbyist | -25832
	 * hobbyistSteam | 240
	 * commercial | 3058
	 * commercialSteam | -1754
	 * industrial | -27983
	 * industrialSteam | 24255
	 * sterling | 6388
	 * bio | 5648
	 * biogas | -22975
	 * biofuel | -29106
	 * electrical | 2696
	 * dry | 5819
	 * clock | 18838
	 * coil | -25297
	 */
	

	public static final String getEngineName(final int damage) {
		final String name = nameMap.get(damage);

		return ((name == null) || (name == "")) ? "EnginePlus@" + damage : name;
	}

	public static final Class<? extends TileEnginePlus> getTile(final int damage) {
		return tileMap.get(damage);
	}

	public static final TileEnginePlus newTile(final int damage) {
		try {
			Class<? extends TileEnginePlus> tileClazz = getTile(damage);
			Constructor<? extends TileEnginePlus> tileConstructor = tileClazz.getConstructor();
			return tileConstructor.newInstance(new Object[] {});
		} catch (final Exception e) {
			LogHelper.warn("Failed to create engine: " + damage);
			LogHelper.error(e);
			e.printStackTrace();
			return null;
		}
	}

	public static final List<Integer> engineList() {
		return (List<Integer>) validEngineList.clone();
	}

	public static final void register(final String metaName, final String displayName, final Class<? extends TileEnginePlus> tile) {
		int hash = AbstractStringType.shortHash(metaName);
		LogHelper.info("[UEE4] Registering an engine meta: " + metaName + "|" + hash + ", called: " + displayName);
		nameMap.put(hash, displayName);
		tileMap.put(hash, tile);
		validEngineList.add(hash);
	}
}
