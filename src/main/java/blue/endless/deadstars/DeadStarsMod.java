package blue.endless.deadstars;

import blue.endless.deadstars.block.DeadStarsBlocks;
import blue.endless.deadstars.data.DataLoader;
import blue.endless.deadstars.data.PrivateRegistry;
import blue.endless.deadstars.item.DeadStarsItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSources;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import java.util.OptionalLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.deadstars.world.CityStructure;
import blue.endless.deadstars.world.CityTileType;
import blue.endless.deadstars.world.SednaChunkGenerator;

public class DeadStarsMod implements ModInitializer {
	private static final String MOD_ID = "dead_stars";
	
	public static final Logger LOGGER = LoggerFactory.getLogger("Dead Stars");
	
	public static final RegistryKey<Registry<CityTileType>> CITY_TILE_TYPE = RegistryKey.ofRegistry(identifier("city_tile_type"));
	public static final PrivateRegistry<CityStructure> STRUCTURE_REGISTRY = new PrivateRegistry<CityStructure>("city_structure", CityStructure.EMPTY);
	
	public static final Identifier SEDNA_ID = identifier("sedna");
	
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
		DeadStarsAudio.register();
		
		Registry.register(Registries.CHUNK_GENERATOR, identifier("sedna"), SednaChunkGenerator.CODEC);
		
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(DataLoader.instance());
	}
}