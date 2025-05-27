package blue.endless.deadstars;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class DataLoader implements IdentifiableResourceReloadListener {

	private static final DataLoader INSTANCE = new DataLoader();
	
	public static DataLoader instance() { return INSTANCE; }
	
	private DataLoader() {}
	
	@Override
	public Identifier getFabricId() {
		return DeadStarsMod.identifier("data_loader");
	}
	
	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		Map<Identifier, Resource> resources = manager.findResources("dead_stars/structures", (it)->it.getPath().endsWith(".json")); // will these be json or nbt??
		
		
		var future = CompletableFuture.supplyAsync(() -> prepareStructures(manager), prepareExecutor);
		var combinedFuture = future
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync(this::applyStructures, applyExecutor);
		//TODO: Process these resources
		DeadStarsMod.LOGGER.info("Finding structures...");
		for(Map.Entry<Identifier, Resource> resource : resources.entrySet()) {
			DeadStarsMod.LOGGER.info("  "+resource.getKey());
		}
		
		return combinedFuture;
	}

	public Map<Identifier, List<StructureTemplate.StructureBlockInfo>> prepareStructures(ResourceManager manager) {
		return Map.of();
	}
	
	public void applyStructures(Map<Identifier, List<StructureTemplate.StructureBlockInfo>> structures) {
		
	}
}
