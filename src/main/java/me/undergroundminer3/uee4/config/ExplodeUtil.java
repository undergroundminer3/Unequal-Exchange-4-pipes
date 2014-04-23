package me.undergroundminer3.uee4.config;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public final class ExplodeUtil {

	private ExplodeUtil() {};
	
	public static void blowUp(final World w, final int x, final int y, final int z, /* var */float intensity) {
		//always destroy blocks, if not, use the one in world
		
		if (!Config.safeMachines) {
			if (Config.machineExplosions && ((w.difficultySetting != EnumDifficulty.PEACEFUL) || Config.explosionsIgnorePeaceful)) {
				if (Config.difficultyAffectsExplosionSize) {
					switch (w.difficultySetting) {
					case PEACEFUL:
					case EASY:
						intensity /= 2;
						break;
					case HARD:
						intensity *= 2;
						//break;
					case NORMAL:
						//break;
					default:
						//break;
					}
				}
				w.createExplosion(null, (double) x, (double) y, (double) z, intensity, true); //BOOM
			}
			//always remove the block in case of an anti grief plugin causing lag.
			w.removeTileEntity(x, y, z);
			w.setBlockToAir(x, y, z);
		}
	}
}
