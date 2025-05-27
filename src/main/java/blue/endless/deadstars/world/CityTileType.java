package blue.endless.deadstars.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

import blue.endless.deadstars.DeadStarsMod;
import net.minecraft.registry.RegistryEntryLookup.RegistryLookup;

/**
 * A tile that can be picked for structure generation.
 */
public class CityTileType {
	/**
	 * This is the minimum height-density value at which this CityBlock will be picked
	 */
	protected int minFloors = 0;
	protected int maxFloors = 0;
	
	protected RegistryEntry<CityStructure> bottomStructure;
	protected Optional<RegistryEntry<CityStructure>> middleStructure;
	protected Optional<RegistryEntry<CityStructure>> topStructure;
	
	public int getMinHeight() {
		CityStructure structure = bottomStructure.value();
		
		// Temporarily disabled to get it compiling
		//return structure.ySize();
		return 0;
	}
}
