package blue.endless.deadstars.world;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.block.DeadStarsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

public class SednaChunkGenerator extends ChunkGenerator {
	private static final RegistryKey<Biome> FORGOTTEN_SANDS = RegistryKey.of(RegistryKeys.BIOME, DeadStarsMod.identifier("forgotten_sands"));
	
	public static final MapCodec<SednaChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) ->
		instance.group(RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME))
			.apply(instance, instance.stable(SednaChunkGenerator::new)));
	
	public SednaChunkGenerator(RegistryEntryLookup<Biome> biomeRegistry) {
		super((BiomeSource) new FixedBiomeSource(biomeRegistry.getOrThrow(FORGOTTEN_SANDS)));
	}

	@Override
	protected MapCodec<SednaChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {
		// We don't carve anything - unless we want to use this step to create rips and holes in buildings
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		// We don't use this step.
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		// No entities in Sedna
	}

	@Override
	public int getWorldHeight() {
		return 384;
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		int minY = getMinimumY();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap oceanFloorWorldgen = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap surfaceWorldgen = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		BlockState bedrock = Blocks.BEDROCK.getDefaultState();
		BlockState dust = DeadStarsBlocks.DUST.get().getDefaultState();
		
		for(int z=0; z<16; z++) {
			for(int x=0; x<16; x++) {
				mutable.set(x, minY, z);
				chunk.setBlockState(mutable, bedrock);
				oceanFloorWorldgen.trackUpdate(mutable.getX(), mutable.getY(), mutable.getZ(), dust);
				surfaceWorldgen.trackUpdate(mutable.getX(), mutable.getY(), mutable.getZ(), dust);
				
				mutable.set(x, minY+1, z);
				chunk.setBlockState(mutable, dust);
				oceanFloorWorldgen.trackUpdate(mutable.getX(), mutable.getY(), mutable.getZ(), dust);
				surfaceWorldgen.trackUpdate(mutable.getX(), mutable.getY(), mutable.getZ(), dust);
			}
		}
		
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getSeaLevel() {
		return -63;
	}

	@Override
	public int getMinimumY() {
		return -63;
	}

	@Override
	public int getHeight(int x, int z, Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return 8;
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		return new VerticalBlockSample(getMinimumY(), new BlockState[] {
				Blocks.BEDROCK.getDefaultState(),
				DeadStarsBlocks.DUST.get().getDefaultState()
		});
	}

	@Override
	public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
		// Do nothing for now
		text.add("Be gay, do crimes.");
	}
}
