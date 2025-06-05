package blue.endless.deadstars.item;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.Lazy;
import blue.endless.deadstars.RegistryContext;
import blue.endless.deadstars.block.DeadStarsBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DeadStarsItems {
	private static final RegistryContext<Item> REGISTRY_CONTEXT = new RegistryContext<>(Registries.ITEM);
	
	public static final Lazy<BlockItem> DUST = register("dust", settings ->
			new BlockItem(
					DeadStarsBlocks.DUST.get(),
					settings
			)
	);
	
	public static final Lazy<FruitpadItem> FRUITPAD = register(FruitpadItem.PATH, FruitpadItem::new);
	
	private DeadStarsItems() {
		throw new IllegalCallerException("DeadStarsItems cannot be constructed.");
	}
	
	public static void register() {
		REGISTRY_CONTEXT.registerAll();
	}
	
	public static RegistryKey<Item> registryKey(String path) {
		return RegistryKey.of(Registries.ITEM.getKey(), DeadStarsMod.identifier(path));
	}
	
	public static <TItem extends Item> Lazy<TItem> register(String path, Function<Item.Settings, TItem> blockSupplier) {
		RegistryKey<Item> key = registryKey(path);
		
		return REGISTRY_CONTEXT.register(key.getValue(), () -> blockSupplier.apply(new Item.Settings().registryKey(key)));
	}
	
	public static <TItem extends Item> Lazy<TItem> register(String path, Supplier<TItem> supplier) {
		return REGISTRY_CONTEXT.register(DeadStarsMod.identifier(path), supplier);
	}
}
