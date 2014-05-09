package me.undergroundminer3.uee4.init2;

import java.lang.reflect.Field;
import java.util.List;

import me.undergroundminer3.uee4.util2.CheatDetector;
import me.undergroundminer3.uee4.util2.LogHelper;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITriggerProvider;

public final class GateHack {

	private GateHack() {};

	public static final void rmTriggerProvider() {

		ActionManager.registerTriggerProvider(null);

		try {

			Field triggerField = ActionManager.class.getDeclaredField("triggerProviders");
			triggerField.setAccessible(true);

			List<ITriggerProvider> triggerList = (List<ITriggerProvider>) triggerField.get(null);

			for (final ITriggerProvider t : triggerList) {
				final String name = t.getClass().getName();
				System.out.println(name);

				if (name.equalsIgnoreCase("buildcraft.transport.PipeTriggerProvider")) {
					triggerList.remove(t);
					LogHelper.info("[UEE4] Sucessfully hacked out buildcraft gates!");
					return;
				}
			}

			throw new Exception("Didn't find the pipe trigger provider!");

		} catch (final Exception e) {
			LogHelper.fatal("Failed to remove buildcraft pipe trigger provider!");
			LogHelper.fatal(e);
			CheatDetector.shutdown();
			throw new RuntimeException(e);
		}

	}
}
