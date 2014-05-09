/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcAirTransport;

import buildcraft.api.core.SafeTimeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * The emcHandler is similar to FluidTank in that it holds your emc and
 * allows standardized interaction between machines.
 *
 * To receive emc to your machine you needs create an instance of emcHandler
 * and implement IemcReceptor on the TileEntity.
 *
 * If you plan emit emc, you need only implement IemcEmitter. You do not
 * need a emcHandler. Engines have a emcHandler because they can also
 * receive emc from other Engines.
 *
 * See TileRefinery for a simple example of a emc using machine.
 */
public final class EmcAirHandler {

	public static enum Type {

		ENGINE, GATE, MACHINE, PIPE, STORAGE;

		public final boolean canReceiveFromPipes() {
			switch (this) {
				case MACHINE:
				case STORAGE:
					return true;
				default:
					return false;
			}
		}

		public final boolean eatsEngineExcess() {
			switch (this) {
				case MACHINE:
				case STORAGE:
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * Extend this class to create custom Perdition algorithms (its not final).
	 *
	 * NOTE: It is not possible to create a Zero perdition algorithm.
	 */
	public static final class PerditionCalculator {

		public static final float DEFAULT_EMCAIRLOSS = 1F;
		public static final float MIN_EMCAIRLOSS = 0.01F;
		private final double emcAirLoss;

		public PerditionCalculator() {
			emcAirLoss = DEFAULT_EMCAIRLOSS;
		}

		/**
		 * Simple constructor for simple Perdition per tick.
		 *
		 * @param emcAirLoss emc loss per tick
		 */
		public PerditionCalculator(final double emcAirLoss) {
//			if (emcAirLoss < MIN_EMCAIRLOSS) {
//				emcAirLoss = MIN_EMCAIRLOSS;
//			}
//			this.emcAirLoss = emcAirLoss;
			this.emcAirLoss = emcAirLoss < MIN_EMCAIRLOSS ? MIN_EMCAIRLOSS : emcAirLoss;
		}

		/**
		 * Apply the perdition algorithm to the current stored emc. This
		 * function can only be called once per tick, but it might not be called
		 * every tick. It is triggered by any manipulation of the stored emc.
		 *
		 * @param emcAirHandler the emcHandler requesting the perdition update
		 * @param current the current stored emc
		 * @param ticksPassed ticks since the last time this function was called
		 * @return
		 */
		public final double applyPerdition(final EmcAirHandler emcAirHandler, /*var*/ double current, final long ticksPassed) {
			current -= emcAirLoss * ticksPassed;
//			if (current < 0) {
//				current = 0;
//			}
//			return current;
			return current < 0 ? 0 : current;
		}

		/**
		 * Taxes a flat rate on all incoming emc.
		 *
		 * Defaults to 0% tax rate.
		 *
		 * @return percent of input to tax
		 */
		public final double getTaxPercent() {
			return 0;
		}
	}
	public static final PerditionCalculator DEFAULT_PERDITION = new PerditionCalculator();
	public static final double ROLLING_AVERAGE_WEIGHT = 100.0;
	public static final double ROLLING_AVERAGE_NUMERATOR = ROLLING_AVERAGE_WEIGHT - 1;
	public static final double ROLLING_AVERAGE_DENOMINATOR  = 1.0 / ROLLING_AVERAGE_WEIGHT;
	private double minEmcAirReceived;
	private double maxEmcAirReceived;
	private double maxEmcAirStored;
	private double activationEmcAir;
	private double emcAirStored = 0;
	private final SafeTimeTracker doWorkTracker = new SafeTimeTracker();
	private final SafeTimeTracker sourcesTracker = new SafeTimeTracker();
	private final SafeTimeTracker perditionTracker = new SafeTimeTracker();
	public final int[] emcAirSources = new int[6];
	public final IEmcAirReceptor receptor;
	private PerditionCalculator perdition;
	private final EmcAirReceiver receiver;
	private final Type type;
	// Tracking
	private double averageLostEmcAir = 0;
	private double averageReceivedEmcAir = 0;
	private double averageUsedEmcAir = 0;

	public EmcAirHandler(final IEmcAirReceptor receptor, final Type type) {
		this.receptor = receptor;
		this.type = type;
		this.receiver = new EmcAirReceiver();
		this.perdition = DEFAULT_PERDITION;
	}

	public final EmcAirReceiver getEmcAirReceiver() {
		return receiver;
	}

	public final double getMinEmcAirReceived() {
		return minEmcAirReceived;
	}

	public final double getMaxEmcAirReceived() {
		return maxEmcAirReceived;
	}

	public final double getMaxEmcAirStored() {
		return maxEmcAirStored;
	}

	public final double getActivationEmcAir() {
		return activationEmcAir;
	}

	public final double getEmcAirStored() {
		return emcAirStored;
	}

	/**
	 * Setup your emcHandler's settings.
	 *
	 * @param minEmcReceived This is the minimum about of emc that will be
	 * accepted by the emcHandler. This should generally be greater than the
	 * activationemc if you plan to use the doWork() callback. Anything
	 * greater than 1 will prevent Redstone Engines from emcing this Provider.
	 * @param maxEmcReceived The maximum amount of emc accepted by the
	 * emcHandler. This should generally be less than 500. Too low and larger
	 * engines will overheat while trying to emc the machine. Too high, and
	 * the engines will never warm up. Greater values also place greater strain
	 * on the emc net.
	 * @param activationEmc If the stored emc is greater than this value,
	 * the doWork() callback is called (once per tick).
	 * @param maxStoredEmc The maximum amount of emc this emcHandler can
	 * store. Values tend to range between 100 and 5000. With 1000 and 1500
	 * being common.
	 */
	public final void configure(final double minEmcAirReceived, final double maxEmcAirReceived, final double activationEmcAir, final double maxStoredEmcAir) {
//		if (minEmcAirReceived > maxEmcAirReceived) {
//			maxEmcAirReceived = minEmcAirReceived;
//		}
		this.minEmcAirReceived = minEmcAirReceived;
//		this.maxEmcAirReceived = maxEmcAirReceived;
		this.maxEmcAirReceived = minEmcAirReceived > maxEmcAirReceived ? minEmcAirReceived : maxEmcAirReceived;
		this.maxEmcAirStored = maxStoredEmcAir;
		this.activationEmcAir = activationEmcAir;
	}

	/**
	 * Allows you define perdition in terms of loss/ticks.
	 *
	 * This function is mostly for legacy implementations. See
	 * PerditionCalculator for more complex perdition formulas.
	 *
	 * @param emcLoss
	 * @param emcLossRegularity
	 * @see PerditionCalculator
	 */
	public final void configureEmcAirPerdition(final int emcAirLoss, final int emcAirLossRegularity) {
		if (emcAirLoss == 0 || emcAirLossRegularity == 0) {
			perdition = new PerditionCalculator(0);
			return;
		}
		perdition = new PerditionCalculator((float) emcAirLoss / (float) emcAirLossRegularity);
	}

	/**
	 * Allows you to define a new PerditionCalculator class to handler perdition
	 * calculations.
	 *
	 * For example if you want exponentially increasing loss based on amount
	 * stored.
	 *
	 * @param perdition
	 */
	public final void setPerdition(final PerditionCalculator perdition) {
//		if (perdition == null)
//			perdition = DEFAULT_PERDITION;
//		this.perdition = perdition;
		this.perdition = perdition == null ? DEFAULT_PERDITION : perdition;
	}

	public final PerditionCalculator getPerdition() {
//		if (perdition == null)
//			return DEFAULT_PERDITION;
//		return perdition;
		return perdition == null ? DEFAULT_PERDITION : perdition;
	}

	/**
	 * Ticks the emc handler. You should call this if you can, but its not
	 * required.
	 *
	 * If you don't call it, the possibility exists for some weirdness with the
	 * perdition algorithm and work callback as its possible they will not be
	 * called on every tick they otherwise would be. You should be able to
	 * design around this though if you are aware of the limitations.
	 */
	public final void update() {
		applyPerdition();
		applyWork();
		validateEmcAir();
	}

	private final void applyPerdition() {
		if (perditionTracker.markTimeIfDelay(receptor.getWorld(), 1) && emcAirStored > 0) {
			double prev = emcAirStored;
			double newemc = getPerdition().applyPerdition(this, emcAirStored, perditionTracker.durationOfLastDelay());
			if (newemc == 0 || newemc < emcAirStored)
				emcAirStored = newemc;
			else
				emcAirStored = DEFAULT_PERDITION.applyPerdition(this, emcAirStored, perditionTracker.durationOfLastDelay());
			validateEmcAir();

			averageLostEmcAir = (averageLostEmcAir * ROLLING_AVERAGE_NUMERATOR + (prev - emcAirStored)) * ROLLING_AVERAGE_DENOMINATOR;
		}
	}

	private final void applyWork() {
		if (emcAirStored >= activationEmcAir) {
			if (doWorkTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
				receptor.doWork(this);
			}
		}
	}

	private final void updateSources(final ForgeDirection source) {
		if (sourcesTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
			for (int i = 0; i < 6; ++i) {
				emcAirSources[i] -= sourcesTracker.durationOfLastDelay();
				if (emcAirSources[i] < 0) {
					emcAirSources[i] = 0;
				}
			}
		}

		if (source != null)
			emcAirSources[source.ordinal()] = 10;
	}

	/**
	 * Extract emc from the emcHandler. You must call this even if doWork()
	 * triggers.
	 *
	 * @param min
	 * @param max
	 * @param doUse
	 * @return amount used
	 */
	public final double useEmcAir(final double min, final double max, final boolean doUse) {
		applyPerdition();

		double result = 0;

		if (emcAirStored >= min) {
			if (emcAirStored <= max) {
				result = emcAirStored;
				if (doUse) {
					emcAirStored = 0;
				}
			} else {
				result = max;
				if (doUse) {
					emcAirStored -= max;
				}
			}
		}

		validateEmcAir();

		if (doUse)
			averageUsedEmcAir = (averageUsedEmcAir * ROLLING_AVERAGE_NUMERATOR + result) * ROLLING_AVERAGE_DENOMINATOR;

		return result;
	}

	public final void readFromNBT(final NBTTagCompound data) {
		readFromNBT(data, "emcAirProvider");
	}

	public final void readFromNBT(final NBTTagCompound data, final String tag) {
		NBTTagCompound nbt = data.getCompoundTag(tag);
		emcAirStored = nbt.getDouble("emcAirStored");
	}

	public final void writeToNBT(final NBTTagCompound data) {
		writeToNBT(data, "emcAirProvider");
	}

	public final void writeToNBT(final NBTTagCompound data, final String tag) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("emcAirStored", emcAirStored);
		data.setTag(tag, nbt);
	}

	public final class EmcAirReceiver {

		private EmcAirReceiver() {};

		public final double getMinEmcAirReceived() {
			return minEmcAirReceived;
		}

		public final double getMaxEmcAirReceived() {
			return maxEmcAirReceived;
		}

		public final double getMaxEmcAirStored() {
			return maxEmcAirStored;
		}

		public final double getActivationEmcAir() {
			return activationEmcAir;
		}

		public final double getEmcAirStored() {
			return emcAirStored;
		}

		public final double getAverageEmcAirReceived() {
			return averageReceivedEmcAir;
		}

		public final double getAverageEmcAirUsed() {
			return averageUsedEmcAir;
		}

		public final double getAverageEmcAirLost() {
			return averageLostEmcAir;
		}

		public final Type getType() {
			return type;
		}

		public final void update() {
			EmcAirHandler.this.update();
		}

		/**
		 * The amount of emc that this emcHandler currently needs.
		 *
		 * @return
		 */
		public final double emcAirRequest() {
			update();
			return Math.min(maxEmcAirReceived, maxEmcAirStored - emcAirStored);
		}

		/**
		 * Add emc to the emcairReceiver from an external source.
		 *
		 * IemcEmitters are responsible for calling this themselves.
		 *
		 * @param quantity
		 * @param from
		 * @return the amount of emcair used
		 */
		public final double receiveEmcAir(final Type source, final double quantity, final ForgeDirection from) {
			double used = quantity;
			if (source == Type.ENGINE) {
				if (used < minEmcAirReceived) {
					return 0;
				} else if (used > maxEmcAirReceived) {
					used = maxEmcAirReceived;
				}
			}

			updateSources(from);

			used -= used * getPerdition().getTaxPercent();

			used = addEmcAir(used);

			applyWork();

			if (source == Type.ENGINE && type.eatsEngineExcess()) {
				used = Math.min(quantity, maxEmcAirReceived);
			}

			averageReceivedEmcAir = (averageReceivedEmcAir * ROLLING_AVERAGE_NUMERATOR + used) * ROLLING_AVERAGE_DENOMINATOR;

			return used;
		}
	}

	/**
	 *
	 * @return the amount the emc changed by
	 */
	public final double addEmcAir(/*var*/ double quantity) {
		emcAirStored += quantity;

		if (emcAirStored > maxEmcAirStored) {
			quantity -= emcAirStored - maxEmcAirStored;
			emcAirStored = maxEmcAirStored;
		} else if (emcAirStored < 0) {
			quantity -= emcAirStored;
			emcAirStored = 0;
		}

		applyPerdition();

		return quantity;
	}

	public final void setEmcAir(final double quantity) {
		this.emcAirStored = quantity;
		validateEmcAir();
	}

	public final boolean isEmcAirSource(final ForgeDirection from) {
		return emcAirSources[from.ordinal()] != 0;
	}

	private final void validateEmcAir() {
		if (emcAirStored < 0) {
			emcAirStored = 0;
		}
		if (emcAirStored > maxEmcAirStored) {
			emcAirStored = maxEmcAirStored;
		}
	}
}
