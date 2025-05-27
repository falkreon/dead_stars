package blue.endless.deadstars;

import blue.endless.deadstars.block.DeadStarsBlocks;
import blue.endless.deadstars.item.DeadStarsItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.deadstars.world.CityStructure;
import blue.endless.deadstars.world.CityTileType;

public class DeadStarsMod implements ModInitializer {
	private static final String MOD_ID = "dead_stars";
	
	public static final Logger LOGGER = LoggerFactory.getLogger("Dead Stars");
	
	public static final RegistryKey<Registry<CityTileType>> CITY_TILE_TYPE = RegistryKey.ofRegistry(identifier("city_tile_type"));
	public static final RegistryKey<Registry<CityStructure>> CITY_STRUCTURE = RegistryKey.ofRegistry(identifier("city_structure"));
	
	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		DeadStarsBlocks.init();
		DeadStarsItems.init();
		
		//ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(DataLoader.instance());
		// Dynamic registries can't be registered to this early
		//DynamicRegistries.<CityTileType>register(RegistryKey.ofRegistry(identifier("city_tile")), null);
	}
}