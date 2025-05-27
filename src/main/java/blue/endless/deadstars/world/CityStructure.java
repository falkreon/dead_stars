package blue.endless.deadstars.world;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.Chunk;

/**
 * Structure-like data that can be poured into a chunk during the generateNoise step instead of noise. City tiles are
 * built from 1 to 3 of these parts.
 */
public class CityStructure {
	private static final int NO_DRAW_OR_UPDATE = Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS | Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK;
	
	protected final List<StructureTemplate.StructureBlockInfo> blocks;
	protected int xStart = 0;
	protected int yStart = 0;
	protected int zStart = 0;
	protected Box bounds;
	
	public CityStructure(List<StructureTemplate.StructureBlockInfo> blocks) {
		this.blocks = List.copyOf(blocks); // Ensure deep immutability
	}
	
	public CityStructure(StructureTemplate.PalettedBlockInfoList infoList) {
		this(infoList.getAll());
	}
	
	public void generate(Chunk chunk, int xofs, int yofs, int zofs, Random random, WrapperLookup registryLookup) {
		BlockPos.Mutable mutPos = new BlockPos.Mutable();
		
		//TODO: ROTATE THE STRUCTURE
		for(StructureTemplate.StructureBlockInfo blockInfo : blocks) {
			
			BlockPos basePos = blockInfo.pos();
			int x = basePos.getX() + xofs;
			int y = basePos.getY() + yofs;
			int z = basePos.getZ() + zofs;
			
			if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < chunk.getBottomY() || y > chunk.getTopYInclusive()) continue;
			
			mutPos.set(x, y, z);
			
			if (blockInfo.nbt() != null) {
				//Set the blockstate to a known no-NBT state to clear it
				//This doesn't matter so much for our current use inside generateNoise() but may avoid bugs in the future
				chunk.setBlockState( mutPos, Blocks.BARRIER.getDefaultState(), NO_DRAW_OR_UPDATE );
			}
			
			chunk.setBlockState(mutPos, blockInfo.state(), NO_DRAW_OR_UPDATE);
			if (blockInfo.nbt() != null) {
				BlockEntity blockEntity = chunk.getBlockEntity(mutPos);
				if (blockEntity != null) {
					if (blockEntity instanceof LootableInventory) {
						blockInfo.nbt().putLong("LootTableSeed", random.nextLong());
					}
					
					blockEntity.read(blockInfo.nbt(), registryLookup);
				}
			}
		}
		
	}
}
