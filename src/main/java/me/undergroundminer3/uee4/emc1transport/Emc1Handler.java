/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package me.undergroundminer3.uee4.emc1transport;

import buildcraft.api.core.SafeTimeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * The emc1Handler is similar to FluidTank in that it holds your emc1 and
 * allows standardized interaction between machines.
 *
 * To receive emc1 to your machine you needs create an instance of emc1Handler
 * and implement Iemc1Receptor on the TileEntity.
 *
 * If you plan emit emc1, you need only implement Iemc1Emitter. You do not
 * need a emc1Handler. Engines have a emc1Handler because they can also
 * receive emc1 from other Engines.
 *
 * See TileRefinery for a simple example of a emc1 using machine.
 *
 * @see Iemc1Receptor
 * @see Iemc1Emitter
 */
public final class Emc1Handler {

	public static enum Type {

		ENGINE, GATE, MACHINE, PIPE, STORAGE;

		public boolean canReceiveFromPipes() {
			switch (this) {
				case MACHINE:
				case STORAGE:
					return true;
				default:
					return false;
			}
		}

		public boolean eatsEngineExcess() {
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
	public static class PerditionCalculator {

		public static final float DEFAULT_EMC1LOSS = 1F;
		public static final float MIN_EMC1LOSS = 0.01F;
		private final double emc1Loss;

		public PerditionCalculator() {
			emc1Loss = DEFAULT_EMC1LOSS;
		}

		/**
		 * Simple constructor for simple Perdition per tick.
		 *
		 * @param emc1Loss emc1 loss per tick
		 */
		public PerditionCalculator(double emc1Loss) {
			if (emc1Loss < MIN_EMC1LOSS) {
				emc1Loss = MIN_EMC1LOSS;
			}
			this.emc1Loss = emc1Loss;
		}

		/**
		 * Apply the perdition algorithm to the current stored emc1. This
		 * function can only be called once per tick, but it might not be called
		 * every tick. It is triggered by any manipulation of the stored emc1.
		 *
		 * @param emc1Handler the emc1Handler requesting the perdition update
		 * @param current the current stored emc1
		 * @param ticksPassed ticks since the last time this function was called
		 * @return
		 */
		public double applyPerdition(Emc1Handler emc1Handler, double current, long ticksPassed) {
			current -= emc1Loss * ticksPassed;
			if (current < 0) {
				current = 0;
			}
			return current;
		}

		/**
		 * Taxes a flat rate on all incoming emc1.
		 *
		 * Defaults to 0% tax rate.
		 *
		 * @return percent of input to tax
		 */
		public double getTaxPercent() {
			return 0;
		}
	}
	public static final PerditionCalculator DEFAULT_PERDITION = new PerditionCalculator();
	public static final double ROLLING_AVERAGE_WEIGHT = 100.0;
	public static final double ROLLING_AVERAGE_NUMERATOR = ROLLING_AVERAGE_WEIGHT - 1;
	public static final double ROLLING_AVERAGE_DENOMINATOR  = 1.0 / ROLLING_AVERAGE_WEIGHT;
	private double minEmc1Received;
	private double maxEmc1Received;
	private double maxEmc1Stored;
	private double activationEmc1;
	private double emc1Stored = 0;
	private final SafeTimeTracker doWorkTracker = new SafeTimeTracker();
	private final SafeTimeTracker sourcesTracker = new SafeTimeTracker();
	private final SafeTimeTracker perditionTracker = new SafeTimeTracker();
	public final int[] emc1Sources = new int[6];
	public final IEmc1Receptor receptor;
	private PerditionCalculator perdition;
	private final Emc1Receiver receiver;
	private final Type type;
	// Tracking
	private double averageLostEmc1 = 0;
	private double averageReceivedEmc1 = 0;
	private double averageUsedEmc1 = 0;

	public Emc1Handler(IEmc1Receptor receptor, Type type) {
		this.receptor = receptor;
		this.type = type;
		this.receiver = new Emc1Receiver();
		this.perdition = DEFAULT_PERDITION;
	}

	public Emc1Receiver getEmc1Receiver() {
		return receiver;
	}

	public double getMinEmc1Received() {
		return minEmc1Received;
	}

	public double getMaxEmc1Received() {
		return maxEmc1Received;
	}

	public double getMaxEmc1Stored() {
		return maxEmc1Stored;
	}

	public double getActivationEmc1() {
		return activationEmc1;
	}

	public double getEmc1Stored() {
		return emc1Stored;
	}

	/**
	 * Setup your emc1Handler's settings.
	 *
	 * @param minEmc1Received This is the minimum about of emc1 that will be
	 * accepted by the emc1Handler. This should generally be greater than the
	 * activationemc1 if you plan to use the doWork() callback. Anything
	 * greater than 1 will prevent Redstone Engines from emc1ing this Provider.
	 * @param maxEmc1Received The maximum amount of emc1 accepted by the
	 * emc1Handler. This should generally be less than 500. Too low and larger
	 * engines will overheat while trying to emc1 the machine. Too high, and
	 * the engines will never warm up. Greater values also place greater strain
	 * on the emc1 net.
	 * @param activationEmc1 If the stored emc1 is greater than this value,
	 * the doWork() callback is called (once per tick).
	 * @param maxStoredEmc1 The maximum amount of emc1 this emc1Handler can
	 * store. Values tend to range between 100 and 5000. With 1000 and 1500
	 * being common.
	 */
	public void configure(double minEmc1Received, double maxEmc1Received, double activationEmc1, double maxStoredEmc1) {
		if (minEmc1Received > maxEmc1Received) {
			maxEmc1Received = minEmc1Received;
		}
		this.minEmc1Received = minEmc1Received;
		this.maxEmc1Received = maxEmc1Received;
		this.maxEmc1Stored = maxStoredEmc1;
		this.activationEmc1 = activationEmc1;
	}

	/**
	 * Allows you define perdition in terms of loss/ticks.
	 *
	 * This function is mostly for legacy implementations. See
	 * PerditionCalculator for more complex perdition formulas.
	 *
	 * @param emc1Loss
	 * @param emc1LossRegularity
	 * @see PerditionCalculator
	 */
	public void configureEmc1Perdition(int emc1Loss, int emc1LossRegularity) {
		if (emc1Loss == 0 || emc1LossRegularity == 0) {
			perdition = new PerditionCalculator(0);
			return;
		}
		perdition = new PerditionCalculator((float) emc1Loss / (float) emc1LossRegularity);
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
	public void setPerdition(PerditionCalculator perdition) {
		if (perdition == null)
			perdition = DEFAULT_PERDITION;
		this.perdition = perdition;
	}

	public PerditionCalculator getPerdition() {
		if (perdition == null)
			return DEFAULT_PERDITION;
		return perdition;
	}

	/**
	 * Ticks the emc1 handler. You should call this if you can, but its not
	 * required.
	 *
	 * If you don't call it, the possibility exists for some weirdness with the
	 * perdition algorithm and work callback as its possible they will not be
	 * called on every tick they otherwise would be. You should be able to
	 * design around this though if you are aware of the limitations.
	 */
	public void update() {
		applyPerdition();
		applyWork();
		validateEmc1();
	}

	private void applyPerdition() {
		if (perditionTracker.markTimeIfDelay(receptor.getWorld(), 1) && emc1Stored > 0) {
			double prev = emc1Stored;
			double newemc1 = getPerdition().applyPerdition(this, emc1Stored, perditionTracker.durationOfLastDelay());
			if (newemc1 == 0 || newemc1 < emc1Stored)
				emc1Stored = newemc1;
			else
				emc1Stored = DEFAULT_PERDITION.applyPerdition(this, emc1Stored, perditionTracker.durationOfLastDelay());
			validateEmc1();

			averageLostEmc1 = (averageLostEmc1 * ROLLING_AVERAGE_NUMERATOR + (prev - emc1Stored)) * ROLLING_AVERAGE_DENOMINATOR;
		}
	}

	private void applyWork() {
		if (emc1Stored >= activationEmc1) {
			if (doWorkTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
				receptor.doWork(this);
			}
		}
	}

	private void updateSources(ForgeDirection source) {
		if (sourcesTracker.markTimeIfDelay(receptor.getWorld(), 1)) {
			for (int i = 0; i < 6; ++i) {
				emc1Sources[i] -= sourcesTracker.durationOfLastDelay();
				if (emc1Sources[i] < 0) {
					emc1Sources[i] = 0;
				}
			}
		}

		if (source != null)
			emc1Sources[source.ordinal()] = 10;
	}

	/**
	 * Extract emc1 from the emc1Handler. You must call this even if doWork()
	 * triggers.
	 *
	 * @param min
	 * @param max
	 * @param doUse
	 * @return amount used
	 */
	public double useEmc1(double min, double max, boolean doUse) {
		applyPerdition();

		double result = 0;

		if (emc1Stored >= min) {
			if (emc1Stored <= max) {
				result = emc1Stored;
				if (doUse) {
					emc1Stored = 0;
				}
			} else {
				result = max;
				if (doUse) {
					emc1Stored -= max;
				}
			}
		}

		validateEmc1();

		if (doUse)
			averageUsedEmc1 = (averageUsedEmc1 * ROLLING_AVERAGE_NUMERATOR + result) * ROLLING_AVERAGE_DENOMINATOR;

		return result;
	}

	public void readFromNBT(NBTTagCompound data) {
		readFromNBT(data, "emc1Provider");
	}

	public void readFromNBT(NBTTagCompound data, String tag) {
		NBTTagCompound nbt = data.getCompoundTag(tag);
		emc1Stored = nbt.getDouble("emc1Stored");
	}

	public void writeToNBT(NBTTagCompound data) {
		writeToNBT(data, "emc1Provider");
	}

	public void writeToNBT(NBTTagCompound data, String tag) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("emc1Stored", emc1Stored);
		data.setTag(tag, nbt);
	}

	public final class Emc1Receiver {

		private Emc1Receiver() {
		}

		public double getMinEmc1Received() {
			return minEmc1Received;
		}

		public double getMaxEmc1Received() {
			return maxEmc1Received;
		}

		public double getMaxEmc1Stored() {
			return maxEmc1Stored;
		}

		public double getActivationEmc1() {
			return activationEmc1;
		}

		public double getEmc1Stored() {
			return emc1Stored;
		}

		public double getAverageEmc1Received() {
			return averageReceivedEmc1;
		}

		public double getAverageEmc1Used() {
			return averageUsedEmc1;
		}

		public double getAverageEmc1Lost() {
			return averageLostEmc1;
		}

		public Type getType() {
			return type;
		}

		public void update() {
			Emc1Handler.this.update();
		}

		/**
		 * The amount of emc1 that this emc1Handler currently needs.
		 *
		 * @return
		 */
		public double emc1Request() {
			update();
			return Math.min(maxEmc1Received, maxEmc1Stored - emc1Stored);
		}

		/**
		 * Add emc1 to the emc1Receiver from an external source.
		 *
		 * Iemc1Emitters are responsible for calling this themselves.
		 *
		 * @param quantity
		 * @param from
		 * @return the amount of emc1 used
		 */
		public double receiveEmc1(Type source, final double quantity, ForgeDirection from) {
			double used = quantity;
			if (source == Type.ENGINE) {
				if (used < minEmc1Received) {
					return 0;
				} else if (used > maxEmc1Received) {
					used = maxEmc1Received;
				}
			}

			updateSources(from);

			used -= used * getPerdition().getTaxPercent();

			used = addEmc1(used);

			applyWork();

			if (source == Type.ENGINE && type.eatsEngineExcess()) {
				used = Math.min(quantity, maxEmc1Received);
			}

			averageReceivedEmc1 = (averageReceivedEmc1 * ROLLING_AVERAGE_NUMERATOR + used) * ROLLING_AVERAGE_DENOMINATOR;

			return used;
		}
	}

	/**
	 *
	 * @return the amount the emc1 changed by
	 */
	public double addEmc1(double quantity) {
		emc1Stored += quantity;

		if (emc1Stored > maxEmc1Stored) {
			quantity -= emc1Stored - maxEmc1Stored;
			emc1Stored = maxEmc1Stored;
		} else if (emc1Stored < 0) {
			quantity -= emc1Stored;
			emc1Stored = 0;
		}

		applyPerdition();

		return quantity;
	}

	public void setEmc1(double quantity) {
		this.emc1Stored = quantity;
		validateEmc1();
	}

	public boolean isEmc1Source(ForgeDirection from) {
		return emc1Sources[from.ordinal()] != 0;
	}

	private void validateEmc1() {
		if (emc1Stored < 0) {
			emc1Stored = 0;
		}
		if (emc1Stored > maxEmc1Stored) {
			emc1Stored = maxEmc1Stored;
		}
	}
}
