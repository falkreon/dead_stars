package blue.endless.deadstars.item;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.network.OpenFruitpadS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FruitpadItem extends Item {
	public static String PATH = "fruitpad";
	public static Identifier ID = DeadStarsMod.identifier(PATH);
	public static RegistryKey<Item> REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, ID);

	public FruitpadItem(Settings settings) {
		super(new Settings()
				.registryKey(REGISTRY_KEY)
				.fireproof()
				);
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) return ActionResult.SUCCESS;
		
		if (user instanceof ServerPlayerEntity serverUser) {
			ServerPlayNetworking.send(serverUser, new OpenFruitpadS2C(Identifier.of("dead_stars", "test"), 0xFF_FF00FF));
		}
		
		return ActionResult.SUCCESS;
	}
}
