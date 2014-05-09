package me.undergroundminer3.uee4.energy.gui.ledger;

public abstract interface IMJEnergyEmitterTabbable extends IDealsWithMJEnergy {
	
	/**
	 * @return The ammount of MJ currently sent out
	 */
	public abstract double getMJEnergyOutput();
	
	/**
	 * @return The ammount of MJ that the TYPE of machine can sent out
	 */
	public abstract double getMJEnergyOutputMax();
}
