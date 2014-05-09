package me.undergroundminer3.uee4.emctransport;

import java.lang.reflect.Constructor;

import me.undergroundminer3.uee4.util2.CheatDetector;
import me.undergroundminer3.uee4.util2.EnumHack;
import me.undergroundminer3.uee4.util2.LogHelper;
import buildcraft.api.transport.IPipeTile.PipeType;

public final class EmcPipeTypes {

	private EmcPipeTypes() {};

	public static final PipeType EmcAir;
	public static final PipeType EmcThermal;
	public static final PipeType EmcLight;
	public static final PipeType EmcPotenta = PipeType.POWER;
	public static final PipeType EmcVoid;

	public static final PipeType EU;
	
	private static int numCounter = 4;

	static {
		EmcAir = newPipe("EmcAir");
		EmcThermal = newPipe("EmcThermal");
		EmcLight = newPipe("EmcLight");
		EmcVoid = newPipe("EmcVoid");

		EU = newPipe("EU");

		LogHelper.info("[UEE4] If you get a crash including \"ArrayIndexOutOfBounds\", I probabally caused it.");
		LogHelper.info("[UEE4] If that happens, remove ALL other BuildCraft addons, and yell at UndergroundMiner3.");
	}

	public static final PipeType newPipe(final String name) {
		final int num = numCounter;
		numCounter++;
		Constructor<?>[] ctors = PipeType.class.getDeclaredConstructors();
		Constructor<?> ctor = null;
		PipeType tempInstance = null;

		for (final Constructor<?> c : ctors) {
			if (c.getGenericParameterTypes().length == 2) {
				ctor = c;
				break;
			}
		}

		try {
			ctor.setAccessible(true);
			tempInstance = (PipeType) EnumHack.newInstance(ctor, new Object[] { name, num });
		} catch (final Exception e) {
			CheatDetector.shutdown();
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		if (tempInstance == null) {
			CheatDetector.shutdown();
			(new Throwable("Null enum created! This will be very bad.")).printStackTrace();
			throw new RuntimeException("Null enum created!");
		}
		LogHelper.info("[UEE4] Sucessfully hacked in a pipe type of name: " + name + ", and id: " + num);
		return tempInstance;
	}
}
