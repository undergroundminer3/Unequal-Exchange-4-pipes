package me.undergroundminer3.uee4.energy.gui.ledger;

public abstract interface IGeneratorTabbable {

	public abstract double getInternalEnergyGeneration();

	public abstract int getBurningTicks();

	public abstract boolean isBurning();
}
