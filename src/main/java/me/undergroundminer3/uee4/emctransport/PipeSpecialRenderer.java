package me.undergroundminer3.uee4.emctransport;

import buildcraft.transport.TileGenericPipe;

public interface PipeSpecialRenderer {

	public void renderPipeInsidesAt(TileGenericPipe pipe, double x, double y, double z, AdvPipeRenderer parent);
}
