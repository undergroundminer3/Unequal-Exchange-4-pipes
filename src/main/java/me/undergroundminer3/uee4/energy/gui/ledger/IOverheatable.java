package me.undergroundminer3.uee4.energy.gui.ledger;

public abstract interface IOverheatable extends IHasTemperature {

	public abstract int getTicksUntilNonOperational();

	public abstract double getMaxHeat();
}
