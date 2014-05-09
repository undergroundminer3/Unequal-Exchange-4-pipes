package me.undergroundminer3.uee4.energy.gui.ledger;

public abstract interface IDoesWork { //any type of energy

	/**
	 * A double between 1 and 0, where 0 being doing no work, and 1 being working at the max.
	 */
	public abstract double getEfficiencyPrecentage();

}
