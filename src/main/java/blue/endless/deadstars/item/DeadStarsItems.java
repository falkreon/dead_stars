package blue.endless.deadstars.item;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.block.DeadStarsBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.function.Function;

@SuppressWarnings("unused")
public class DeadStarsItems {
	public static final BlockItem DUST = register("dust", settings ->
			new BlockItem(
					DeadStarsBlocks.DUST,
					settings
			)
	);
	
	private DeadStarsItems() {
		throw new IllegalCallerException("DeadStarsItems cannot be constructed.");
	}
	
	/**
	 * Empty function to get the class loaded.
	 * */
	public static void init() {}
	
	public static RegistryKey<Item> registryKey(String path) {
		return RegistryKey.of(Registries.ITEM.getKey(), DeadStarsMod.identifier(path));
	}
	
	public static <TItem extends Item> TItem register(String path, Function<Item.Settings, TItem> blockSupplier) {
		RegistryKey<Item> key = registryKey(path);
		
		return Registry.register(Registries.ITEM, key.getValue(), blockSupplier.apply(new Item.Settings().registryKey(key)));
	}
}
