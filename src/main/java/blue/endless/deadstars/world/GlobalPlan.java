package blue.endless.deadstars.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.annotation.FieldsAreNonnullByDefault;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;

/**
 * Sedna has a lot of "contextual" generation. Each city and mare is named. The radial highways are generated at a
 * massive scale. We need to know about the layout of these things way before chunks are generated.
 * 
 * <p>
 * Luckily, the information about this layout is both quick to repeatably generate, and the resulting data is very
 * compact, so the ChunkGenerator can just create this with the world-seed when the world is loaded, and then not bother
 * to save it since it can be regenerated from the seed next boot. 
 */
@FieldsAreNonnullByDefault
public class GlobalPlan {
	public final Info info;
	public final long seed;
	
	public List<Mare> mara = new ArrayList<>();
	public List<Circle> cities = new ArrayList<>();
	public List<Ray> rays = new ArrayList<>();
	
	public GlobalPlan(Info info, long seed) {
		this.info = info;
		this.seed = seed;
		
		Random globalRandom = Random.create(seed);
		long cityPlacementSeed = globalRandom.nextLong();
		Random cityPlacementRandom = Random.create(cityPlacementSeed);
		long maraSeed = globalRandom.nextLong();
		Random maraRandom = Random.create(maraSeed);
		
		for(int i=0; i<10; i++) {
			Mare m = new Mare();
			
			float cr = maraRandom.nextTriangular(400, 200);
			Circle c = splat(cr, maraRandom);
			System.out.println("Splat at "+c.center.x+", "+c.center.y);
			if (c == null) continue;
			
			m.add(c);
			
			for(int j=0; j<15; j++) {
				m.grow(maraRandom);
			}
			
			mara.add(m);
		}
		
		for(int i=0; i<15; i++) {
			float cityRadius = cityPlacementRandom.nextTriangular(info.cities.radius, info.cities.radiusVariation);
			int cx = cityPlacementRandom.nextBetween((int) Math.ceil(cityRadius), info.size - (int) Math.ceil(cityRadius));
			int cz = cityPlacementRandom.nextBetween((int) Math.ceil(cityRadius), info.size - (int) Math.ceil(cityRadius));
			Circle cur = new Circle(new Vec2f(cx, cz), cityRadius);
			
			if (intersectsAny(cur)) continue;
			
			cities.add(cur);
			
			int rayCount = cityPlacementRandom.nextBetween(4, 13);
			for(int j=0; j<rayCount; j++) {
				float inner = cityRadius + 8 + cityPlacementRandom.nextBetween(0, 200);
				float outer = inner + cityPlacementRandom.nextBetween(200, 3000);
				float angle = (float) (cityPlacementRandom.nextBetween(0, 359) * Math.PI / 180.0);
				rays.add(new Ray(cur.center(), angle, inner, outer));
			}
		}
		
		System.out.println("Placed "+cities.size()+" cities, "+rays.size()+" rays, and "+mara.size()+" mara.");
	}
	
	
	
	public @Nullable Circle splat(float radius, Random rnd) {
		for(int i=0; i<5; i++) {
			int cx = rnd.nextBetween((int) Math.ceil(radius), info.size - (int) Math.ceil(radius));
			int cz = rnd.nextBetween((int) Math.ceil(radius), info.size - (int) Math.ceil(radius));
			Circle cur = new Circle(new Vec2f(cx, cz), radius);
			
			if (intersectsAny(cur)) continue;
			
			return cur;
		}
		
		return null;
	}
	
	public boolean intersectsAny(Circle c) {
		for(Circle d : cities) {
			if (d.intersects(c)) return true;
		}
		
		for(Mare m : mara) {
			if (m.intersects(c)) return true;
		}
		
		return false;
	}
	
	public static class Mare implements Iterable<Circle> {
		private List<Circle> areas = new ArrayList<>();
		
		public void add(Circle c) {
			this.areas.add(c);
		}
		
		public boolean intersects(Circle other) {
			for(Circle c : areas) if (c.intersects(other)) return true;
			return false;
		}
		
		public void grow(Random r) {
			if (areas.isEmpty()) return;
			
			Circle start = areas.get(r.nextBetween(0, areas.size() - 1));
			float size = r.nextBetween(100, 500);
			float distance = (start.radius + size) * 0.8f; // Overlap
			float angle = (float) (r.nextBetween(0, 359) * Math.PI / 180);
			float x = start.center.x + (MathHelper.cos(angle) * distance);
			float y = start.center.y + (MathHelper.sin(angle) * distance);
			areas.add(new Circle(new Vec2f(x, y), size));
		}
		
		@Override
		public Iterator<Circle> iterator() {
			return areas.iterator();
		}
	}
	
	public static record Circle(Vec2f center, float radius) {
		public boolean intersects(Circle other) {
			final float touching = this.radius + other.radius;
			final float touchingSquared = touching * touching;
			final float dist = this.center.distanceSquared(other.center);
			return dist <= touchingSquared;
		}
	}
	
	public static record Ray (Vec2f center, float angle, float min, float max) {
		public Vec2f nearPoint() {
			float x = center.x + (MathHelper.cos(angle) * min);
			float y = center.y + (MathHelper.sin(angle) * min);
			
			return new Vec2f(x, y);
		}
		
		public Vec2f farPoint() {
			float x = center.x + (MathHelper.cos(angle) * max);
			float y = center.y + (MathHelper.sin(angle) * max);
			
			return new Vec2f(x, y);
		}
	}
	
	public static class Info {
		public int size = 10_000;
		
		/**
		 * The distance from the center of the map inside of which a fixed teleport ratio is maintained
		 */
		public int flatInnerRadius;
		
		/**
		 * The distance from the center of the overworld map inside which a fixed teleport ratio is maintained
		 */
		public int flatOuterRadius;
		
		public CityInfo cities = new CityInfo();
		public CraterInfo craters = new CraterInfo();
	}
	
	public static class CityInfo {
		/** The median radius of a city, in meters */
		public float radius = 150;
		
		/** The size difference, in meters, that a city radius could be smaller OR larger than the median */
		public float radiusVariation = 50;
	}
	
	public static class CraterInfo {
		/** How far the crater floor sits below the desert's lowest point */
		public float floorDepth = 4;
		
		/** How much space is left outside the city edge before the crater rim starts, in meters */
		public float innerMargin = 10;
		
		/** Horizontal distance from the crater's bottom edge to its peak */
		public float distanceToRim = 8;
		
		/** Horizontal distance from the peak of the crater rim to the outer edge of the crater. */
		public float distanceToOuterEdge = 16;
		
		/** Distance fom the desert's lowest point to the peak of the crater rim */
		public float peakHeight = 16;
	}
}
