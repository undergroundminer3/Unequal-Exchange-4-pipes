package me.undergroundminer3.uee4.bcReplacements;

import net.minecraft.util.ResourceLocation;

public final class EnginePlusRenderData {

	public final ResourceLocation TRUNK_TEXTURE;
	public final ResourceLocation CHAMBER_TEXTURE;
	public final ResourceLocation BASE_TEXTURE;

	public final float progress;

	public EnginePlusRenderData(final ResourceLocation trunk, final ResourceLocation chamber, final ResourceLocation base,
			final float progress) {
		this.TRUNK_TEXTURE = trunk;
		this.CHAMBER_TEXTURE = chamber;
		this.BASE_TEXTURE = base;

		this.progress = progress;
	}
}
