package blue.endless.deadstars.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import blue.endless.deadstars.DeadStarsMod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;

public class DataLoader implements IdentifiableResourceReloadListener {

	private static final DataLoader INSTANCE = new DataLoader();
	
	public static DataLoader instance() { return INSTANCE; }
	
	private DataLoader() {}
	
	private ImageData dunesImage = new ImageData.OpaqueGray(1,1);
	
	public ImageData getDunesImage() { return dunesImage; }
	
	
	
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
		
		DeadStarsMod.LOGGER.info("Finding structures...");
		for(Map.Entry<Identifier, Resource> resource : resources.entrySet()) {
			DeadStarsMod.LOGGER.info("  "+resource.getKey());
		}
		
		Optional<Resource> dunesHeightMapFile = manager.getResource(DeadStarsMod.identifier("heightmap/dunes.bmp"));
		if (dunesHeightMapFile.isPresent()) {
			try(InputStream stream = dunesHeightMapFile.get().getInputStream()) {
				
				dunesImage = ImageData.loadBmp(stream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			//TODO: Synthetic 1x1 50%-gray fallback image
			DeadStarsMod.LOGGER.warn("Dunes bitmap not present. Forgotten Sands will be flat.");
		}
		
		return combinedFuture;
	}

	public Map<Identifier, List<StructureTemplate.StructureBlockInfo>> prepareStructures(ResourceManager manager) {
		//TODO: Process structure NBT in datapacks and return the block data.
		
		return Map.of();
	}
	
	public void applyStructures(Map<Identifier, List<StructureTemplate.StructureBlockInfo>> structures) {
		//TODO: Turn extracted block data into CityStructures and register them in StructureRegistry.
	}
}
