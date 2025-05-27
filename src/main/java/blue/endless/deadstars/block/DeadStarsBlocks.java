package blue.endless.deadstars.block;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.Lazy;
import blue.endless.deadstars.RegistryContext;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SandBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ColorCode;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class DeadStarsBlocks {
	private static final RegistryContext<Block> REGISTRY_CONTEXT = new RegistryContext<>(Registries.BLOCK);
	
	public static final Lazy<SandBlock> DUST = register("dust", settings ->
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
	
	public static void register() {
		REGISTRY_CONTEXT.registerAll();
	}
	
	public static RegistryKey<Block> registryKey(String path) {
		return RegistryKey.of(Registries.BLOCK.getKey(), DeadStarsMod.identifier(path));
	}
	
	public static <TBlock extends Block> Lazy<TBlock> register(String path, Function<AbstractBlock.Settings, TBlock> blockSupplier) {
		RegistryKey<Block> key = registryKey(path);
		
		return REGISTRY_CONTEXT.register(key.getValue(), () -> blockSupplier.apply(AbstractBlock.Settings.create().registryKey(key)));
	}
}
