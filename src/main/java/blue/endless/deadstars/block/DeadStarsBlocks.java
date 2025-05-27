package blue.endless.deadstars.block;

import blue.endless.deadstars.DeadStarsMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SandBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class DeadStarsBlocks {
	public static final SandBlock DUST = register("dust", settings ->
			new SandBlock(
					// TODO: Change the color once we have a texture
					new ColorCode(0xffffff),
					settings
							.sounds(BlockSoundGroup.SAND)
			)
	);
	
	private DeadStarsBlocks() {
		throw new IllegalCallerException("DeadStarsBlocks cannot be constructed.");
	}
	
	/**
	 * Empty function to get the class loaded.
	 * */
	public static void init() {}
	
	public static RegistryKey<Block> registryKey(String path) {
		return RegistryKey.of(Registries.BLOCK.getKey(), DeadStarsMod.identifier(path));
	}
	
	public static <TBlock extends Block> TBlock register(String path, Function<AbstractBlock.Settings, TBlock> blockSupplier) {
		RegistryKey<Block> key = registryKey(path);
		
		return Registry.register(Registries.BLOCK, key.getValue(), blockSupplier.apply(AbstractBlock.Settings.create().registryKey(key)));
	}
}
