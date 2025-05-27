package blue.endless.deadstars;

import blue.endless.deadstars.block.DeadStarsBlocks;
import blue.endless.deadstars.data.DataLoader;
import blue.endless.deadstars.data.PrivateRegistry;
import blue.endless.deadstars.item.DeadStarsItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.deadstars.world.CityStructure;
import blue.endless.deadstars.world.CityTileType;

public class DeadStarsMod implements ModInitializer {
	private static final String MOD_ID = "dead_stars";
	
	public static final Logger LOGGER = LoggerFactory.getLogger("Dead Stars");
	
	public static final RegistryKey<Registry<CityTileType>> CITY_TILE_TYPE = RegistryKey.ofRegistry(identifier("city_tile_type"));
	public static final PrivateRegistry<CityStructure> STRUCTURE_REGISTRY = new PrivateRegistry<CityStructure>("city_structure", CityStructure.EMPTY);
	
	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
	
	public static Registry<CityTileType> cityTileTypeRegistry(World world) {
		if (world.isClient()) throw new IllegalArgumentException("Please access the CityTileType registry from the logical server.");
		return world.getRegistryManager().getOrThrow(CITY_TILE_TYPE);
	}
	
	@Override
	public void onInitialize() {
		DeadStarsBlocks.register();
		DeadStarsItems.register();
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(DataLoader.instance());
		// Dynamic registries can't be registered to this early
		//DynamicRegistries.<CityTileType>register(RegistryKey.ofRegistry(identifier("city_tile")), null);
	}
}