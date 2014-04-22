package me.undergroundminer3.uee4.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import me.undergroundminer3.uee4.util2.LogHelper;

public final class Config {

	private Config() {};

	private static boolean loaded = false;

	public static boolean allowCheats = false;

	public static void load(final File file) {
		if (loaded) return;

		if (!file.isDirectory()) {
			LogHelper.warn("Config path fail!");
			return;
		}

		try {
			final File dirFile = new File(file.getAbsolutePath() + "/uee4/");
			if (!dirFile.isDirectory()) {
				dirFile.mkdirs();
			}
			final File mainFile = new File(dirFile.getAbsolutePath() + "/uee4.cfg");
			if (!mainFile.exists()) {
				mainFile.createNewFile();
			}
			loadMisc(mainFile);
		} catch (final Exception e) {
			LogHelper.warn("Config load fail!");
			LogHelper.fatal(e);
		}

	}

	private static void loadMisc(final File file) {
		final Configuration config = new Configuration(file);
		config.load();

		allowCheats = config.get("misc", "allowModCheats", false, "Determines if energy conversion mods will be allowed.").getBoolean(false);

		config.save();
	}
}
