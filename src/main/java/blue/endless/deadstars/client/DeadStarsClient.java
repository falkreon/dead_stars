package blue.endless.deadstars.client;

import blue.endless.deadstars.DeadStarsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DeadStarsClient implements ClientModInitializer {
	public static final DimensionEffects SEDNA_EFFECTS = new DimensionEffects(Float.NaN, false, DimensionEffects.SkyType.NORMAL, false, false) {
		
		@Override
		public int getSkyColor(float skyAngle) {
			//TODO: Modify sky colors to something more alien.
			
			/* In overworld: there's an ambient (0.7, 0.2, 0.2) term.
			 * This is increased by up to (0.3 * s, 0.7 * s^2, 1) during certain times of day, based on s
			 * 
			 */
			
			float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2));
			float sunsetness = f / 0.4F * 0.5F + 0.5F;
			float alpha = MathHelper.square(1.0F - (1.0F - MathHelper.sin(sunsetness * (float) Math.PI)) * 0.99F);
			alpha -= 0.5f; // Always show the stars clearly, even in mid-day.
			if (alpha < 0) alpha = 0;
			//return ColorHelper.fromFloats(
			//		alpha,
			//		sunsetness * 0.3F + 0.7F,
			//		sunsetness * sunsetness * 0.7F + 0.2F,
			//		0.2F);
			
			int r = ColorHelper.channelFromFloat(0.4f); if (r>1) r=1;
			int g = ColorHelper.channelFromFloat(sunsetness * sunsetness * 0.5F + 0.7F); if (g>1) g=1;
			int b = ColorHelper.channelFromFloat(sunsetness * 0.3F + 0.7F); if (b>1) b=1;
			
			return ColorHelper.fromFloats(
					alpha,
					r,
					g,
					b
					);
		}
		
		@Override
		public boolean isSunRisingOrSetting(float skyAngle) {
			// Same as overworld
			float f = MathHelper.cos(skyAngle * (float) (Math.PI * 2));
			return f >= -0.4F && f <= 0.4F;
		}
		
		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			// Same as overworld
			return color.multiply(sunHeight * 0.94F + 0.06F, sunHeight * 0.94F + 0.06F, sunHeight * 0.91F + 0.09F);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}
		
	};
	
	@Override
	public void onInitializeClient() {
		DimensionRenderingRegistry.registerDimensionEffects(DeadStarsMod.identifier("sedna_effects"), SEDNA_EFFECTS);
	}
}