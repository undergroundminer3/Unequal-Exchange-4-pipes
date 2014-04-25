package me.undergroundminer3.uee4.emctransport;

import java.lang.reflect.Constructor;

import me.undergroundminer3.uee4.util2.CheatDetector;
import me.undergroundminer3.uee4.util2.EnumHack;
import me.undergroundminer3.uee4.util2.LogHelper;
import buildcraft.api.transport.IPipeTile.PipeType;

public final class EmcPipeTypes {

	private EmcPipeTypes() {};

	public static final PipeType EMC1;

	static {
		EMC1 = newPipe("EMC1");
		LogHelper.info("[UEE4] If you get a crash including \"ArrayIndexOutOfBounds\", I probabally caused it.");
		LogHelper.info("[UEE4] If that happens, remove ALL other BuildCraft addons.");
	}
	
	private static int numCounter = 4;

	public static PipeType newPipe(final String name) {
		final int num = numCounter;
		numCounter++;
		Constructor<?>[] ctors = PipeType.class.getDeclaredConstructors();
		Constructor<?> ctor = null;
		PipeType tempInstance = null;

		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
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
