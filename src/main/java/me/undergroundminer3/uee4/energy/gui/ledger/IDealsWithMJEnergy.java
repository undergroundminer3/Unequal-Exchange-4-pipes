package me.undergroundminer3.uee4.energy.gui.ledger;

public abstract interface IDealsWithMJEnergy extends IDoesWork {

	public abstract double getMJEnergyStored();

	public abstract double getMJEnergyStoredMax();
}
