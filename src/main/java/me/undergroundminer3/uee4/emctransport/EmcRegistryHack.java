package me.undergroundminer3.uee4.emctransport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import me.undergroundminer3.uee4.util2.CheatDetector;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxy;
import cpw.mods.fml.common.registry.GameRegistry;

public final class EmcRegistryHack {

	private EmcRegistryHack() {};

	public static final ItemPipe registerPipe(Class<? extends Pipe<?>> clas, final String prefix, final String name) {
		ItemPipe item = hackPipe();
		item.setUnlocalizedName(prefix + name);
		GameRegistry.registerItem(item, item.getUnlocalizedName());

		BlockGenericPipe.pipes.put(item, clas);

		Pipe<?> dummyPipe = BlockGenericPipe.createPipe(item);
		if (dummyPipe != null) {
			item.setPipeIconIndex(dummyPipe.getIconIndexForItem());
			TransportProxy.proxy.setIconProviderFromPipe(item, dummyPipe);
		}
		return item;
	}

	public static final ItemPipe registerPipe(Class<? extends Pipe<?>> clazz, final String name) {
		return registerPipe(clazz, "uee4Pipe.", name);
	}

	public static final ItemPipe hackPipe() {
		Constructor<?>[] ctors = ItemPipe.class.getDeclaredConstructors();
		Constructor<?> ctor = null;
		ItemPipe tempInstance = null;

		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}

		try {
			ctor.setAccessible(true);
			tempInstance = (ItemPipe) ctor.newInstance(CreativeTabBuildCraft.PIPES);
		} catch (final InstantiationException e) {
			CheatDetector.shutdown();
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			CheatDetector.shutdown();
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			CheatDetector.shutdown();
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			CheatDetector.shutdown();
			e.printStackTrace();
		}

		if (tempInstance == null) {
			CheatDetector.shutdown();
			(new Throwable("Null pipe created! This will be very bad.")).printStackTrace();
		}

		return tempInstance;
	}
}
