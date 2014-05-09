/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emcHeatTransport;

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
public final class EmcHeatHandler {

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

		public static final float DEFAULT_EMCHEATLOSS = 1F;
		public static final float MIN_EMCHEATLOSS = 0.01F;
		private final double emcHeatLoss;

		public PerditionCalculator() {
			emcHeatLoss = DEFAULT_EMCHEATLOSS;
		}

		/**
		 * Simple constructor for simple Perdition per tick.
		 *
		 * @param emchearLoss emc loss per tick
		 */
		public PerditionCalculator(final double emcHeatLoss) {
			this.emcHeatLoss = emcHeatLoss < MIN_EMCHEATLOSS ? MIN_EMCHEATLOSS : emcHeatLoss;
		}

		/**
		 * Apply the perdition algorithm to the current stored emc. This
		 * function can only be called once per tick, but it might not be called
		 * every tick. It is triggered by any manipulation of the stored emc.
		 *
		 * @param emcHeatHandler the emcHandler requesting the perdition update
		 * @param current the current stored emc
		 * @param ticksPassed ticks since the last time this function was called
		 * @return
		 */
		public final double applyPerdition(final EmcHeatHandler emcHeatHandler, /*var*/ double current, final long ticksPassed) {
			current -= emcHeatLoss * ticksPassed;
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
	private double minEmcHeatReceived;
	private double maxEmcHeatReceived;
	private double maxEmcHeatStored;
	private double activationEmcHeat;
	private double emcHeatStored = 0;
	private final SafeTimeTracker doWorkTracker = new SafeTimeTracker();
	private final SafeTimeTracker sourcesTracker = new SafeTimeTracker();
	private final SafeTimeTracker perditionTracker = new SafeTimeTracker();
	public final int[] emcHeatSources = new int[6];
	public final IEmcHeatReceptor receptor;
	private PerditionCalculator perdition;
	private final EmcHeatReceiver receiver;
	private final Type type;
	// Tracking
	private double averageLostEmcHeat = 0;
	private double averageReceivedEmcHeat = 0;
	private double averageUsedEmcHeat = 0;

	public EmcHeatHandler(final IEmcHeatReceptor receptor, final Type type) {
		this.receptor = receptor;
		this.type = type;
		this.receiver = new EmcHeatReceiver();
		this.perdition = DEFAULT_PERDITION;
	}

	public final EmcHeatReceiver getEmcHeatReceiver() {
		return receiver;
	}

	public final double getMinEmcHeatReceived() {
		return minEmcHeatReceived;
	}

	public final double getMaxEmcHeatReceived() {
		return maxEmcHeatReceived;
	}

	public final double getMaxEmcHeatStored() {
		return maxEmcHeatStored;
	}

	public final double getActivationEmcHeat() {
		return activationEmcHeat;
	}

	public final double getEmcHeatStored() {
		return emcHeatStored;
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
	public final void configure(final double minEmcHeatReceived, final double maxEmcHeatReceived, final double activationEmcHeat, final double maxStoredEmcHeat) {
		this.minEmcHeatReceived = minEmcHeatReceived;
		this.maxEmcHeatReceived = minEmcHeatReceived > maxEmcHeatReceived ? minEmcHeatReceived : maxEmcHeatReceived;
		this.maxEmcHeatStored = maxStoredEmcHeat;
		this.activationEmcHeat = activationEmcHeat;
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
	public final void configureEmcHeatPerdition(final int emcHeatLoss, final int emcHeatLossRegularity) {
		if (emcHeatLoss == 0 || emcHeatLossRegularity == 0) {
			perdition = new PerditionCalculator(0);
			return;
		}
		perdition = new PerditionCalculator((float) emcHeatLoss / (float) emcHeatLossRegularity);
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
		validateEmcHeat();
	}

	private final void applyPerdition() {
		if (perditionTracker.markTimeIfDelay(receptor.getWorld(), 1) && emcHeatStored > 0) {
			double prev = emcHeatStored;
			double newemc = getPerdition().applyPerdition(this, emcHeatStored, perditionTracker.durationOfLastDelay());
			if (newemc == 0 || newemc < emcHeatStored)
				emcHeatStored = newemc;
			else
				emcHeatStored = DEFAULT_PERDITION.applyPerdition(this, emcHeatStored, perditionTracker.durationOfLastDelay());
			validateEmcHeat();

			averageLostEmcHeat = (averageLostEmcHeat * ROLLING_AVERAGE_NUMERATOR + (prev - emcHeatStored)) * ROLLING_AVERAGE_DENOMINATOR;
		}
	}

	private final void applyWork() {
		if (emcHeatStored >= activationEmcHeat) {
			if (doWorkTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
				receptor.doWork(this);
			}
		}
	}

	private final void updateSources(final ForgeDirection source) {
		if (sourcesTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
			for (int i = 0; i < 6; ++i) {
				emcHeatSources[i] -= sourcesTracker.durationOfLastDelay();
				if (emcHeatSources[i] < 0) {
					emcHeatSources[i] = 0;
				}
			}
		}

		if (source != null)
			emcHeatSources[source.ordinal()] = 10;
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
	public final double useEmcHeat(final double min, final double max, final boolean doUse) {
		applyPerdition();

		double result = 0;

		if (emcHeatStored >= min) {
			if (emcHeatStored <= max) {
				result = emcHeatStored;
				if (doUse) {
					emcHeatStored = 0;
				}
			} else {
				result = max;
				if (doUse) {
					emcHeatStored -= max;
				}
			}
		}

		validateEmcHeat();

		if (doUse)
			averageUsedEmcHeat = (averageUsedEmcHeat * ROLLING_AVERAGE_NUMERATOR + result) * ROLLING_AVERAGE_DENOMINATOR;

		return result;
	}

	public final void readFromNBT(final NBTTagCompound data) {
		readFromNBT(data, "emcHeatProvider");
	}

	public final void readFromNBT(final NBTTagCompound data, final String tag) {
		NBTTagCompound nbt = data.getCompoundTag(tag);
		emcHeatStored = nbt.getDouble("emcHeatStored");
	}

	public final void writeToNBT(final NBTTagCompound data) {
		writeToNBT(data, "emcHeatProvider");
	}

	public final void writeToNBT(final NBTTagCompound data, final String tag) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("emcHeatStored", emcHeatStored);
		data.setTag(tag, nbt);
	}

	public final class EmcHeatReceiver {

		private EmcHeatReceiver() {};

		public final double getMinEmcHeatReceived() {
			return minEmcHeatReceived;
		}

		public final double getMaxEmcHeatReceived() {
			return maxEmcHeatReceived;
		}

		public final double getMaxEmcHeatStored() {
			return maxEmcHeatStored;
		}

		public final double getActivationEmcHeat() {
			return activationEmcHeat;
		}

		public final double getEmcHeatStored() {
			return emcHeatStored;
		}

		public final double getAverageEmcHeatReceived() {
			return averageReceivedEmcHeat;
		}

		public final double getAverageEmcHeatUsed() {
			return averageUsedEmcHeat;
		}

		public final double getAverageEmcHeatLost() {
			return averageLostEmcHeat;
		}

		public final Type getType() {
			return type;
		}

		public final void update() {
			EmcHeatHandler.this.update();
		}

		/**
		 * The amount of emc that this emcHandler currently needs.
		 *
		 * @return
		 */
		public final double emcHeatRequest() {
			update();
			return Math.min(maxEmcHeatReceived, maxEmcHeatStored - emcHeatStored);
		}

		/**
		 * Add emc to the emcheatReceiver from an external source.
		 *
		 * IemcEmitters are responsible for calling this themselves.
		 *
		 * @param quantity
		 * @param from
		 * @return the amount of emcheat used
		 */
		public final double receiveEmcHeat(final Type source, final double quantity, final ForgeDirection from) {
			double used = quantity;
			if (source == Type.ENGINE) {
				if (used < minEmcHeatReceived) {
					return 0;
				} else if (used > maxEmcHeatReceived) {
					used = maxEmcHeatReceived;
				}
			}

			updateSources(from);

			used -= used * getPerdition().getTaxPercent();

			used = addEmcHeat(used);

			applyWork();

			if (source == Type.ENGINE && type.eatsEngineExcess()) {
				used = Math.min(quantity, maxEmcHeatReceived);
			}

			averageReceivedEmcHeat = (averageReceivedEmcHeat * ROLLING_AVERAGE_NUMERATOR + used) * ROLLING_AVERAGE_DENOMINATOR;

			return used;
		}
	}

	/**
	 *
	 * @return the amount the emc changed by
	 */
	public final double addEmcHeat(/*var*/ double quantity) {
		emcHeatStored += quantity;

		if (emcHeatStored > maxEmcHeatStored) {
			quantity -= emcHeatStored - maxEmcHeatStored;
			emcHeatStored = maxEmcHeatStored;
		} else if (emcHeatStored < 0) {
			quantity -= emcHeatStored;
			emcHeatStored = 0;
		}

		applyPerdition();

		return quantity;
	}

	public final void setEmcHeat(final double quantity) {
		this.emcHeatStored = quantity;
		validateEmcHeat();
	}

	public final boolean isEmcHeatSource(final ForgeDirection from) {
		return emcHeatSources[from.ordinal()] != 0;
	}

	private final void validateEmcHeat() {
		if (emcHeatStored < 0) {
			emcHeatStored = 0;
		}
		if (emcHeatStored > maxEmcHeatStored) {
			emcHeatStored = maxEmcHeatStored;
		}
	}
}
