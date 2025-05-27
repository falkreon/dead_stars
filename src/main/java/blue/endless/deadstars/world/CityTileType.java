package blue.endless.deadstars.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

import blue.endless.deadstars.DeadStarsMod;

/**
 * A tile that can be picked for structure generation.
 * 
 * A one-floor CityTile only uses the "bottom" CityStructure. A two-floor tile uses the bottom and top structures. A
 * three-floor tile uses the bottom, middle, and top structures. Any additional heights invoke additional repeats of the
 * middle structure.
 */
public class CityTileType {
	/**
	 * This is the minimum number of floors that the tile manager can pick. The minimum height of the structure will be
	 */
	protected int minFloors = 1;
	protected int maxFloors = 1;
	
	protected Identifier bottom;
	protected Optional<Identifier> middle;
	protected Optional<Identifier> top;
	
	public void getHeight(int floorCount) {
		
		int bottomHeight = 0;
	}
}
