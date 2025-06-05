package blue.endless.deadstars.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;

import blue.endless.deadstars.DeadStarsMod;
import blue.endless.deadstars.data.PrivateRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * 
 */

@Environment(EnvType.CLIENT)
public class LocalizedResourcePool<T> implements IdentifiableResourceReloadListener {
	private static final String FALLBACK_LOCALE = "en_us";
	private final boolean stripExtension = true;
	private final Map<String, PrivateRegistry<T>> data = new HashMap<>();
	private final Identifier reloaderId;
	private final String basePath;
	private final Predicate<Identifier> resourcePredicate;
	private final BiFunction<Identifier, Resource, Optional<T>> resourceLoader;
	private Function<Identifier, T> defaultEntrySupplier = null;
	
	public LocalizedResourcePool(Identifier reloaderId, String basePath, Predicate<Identifier> resourcePredicate, boolean stripExtension, BiFunction<Identifier, Resource, Optional<T>> resourceLoader) {
		this.reloaderId = reloaderId;
		this.basePath = (basePath.startsWith("/")) ? basePath.substring(1) : basePath;
		this.resourcePredicate = resourcePredicate;
		this.resourceLoader = resourceLoader;
	}
	
	public Optional<PrivateRegistry<T>> getRegistry(String locale) {
		return Optional.ofNullable(data.get(locale));
	}
	
	public PrivateRegistry<T> getActiveRegistry() {
		String locale = MinecraftClient.getInstance().getLanguageManager().getLanguage();
		Optional<PrivateRegistry<T>> result = getRegistry(locale);
		return result.orElseGet(() -> {
			return getRegistry(FALLBACK_LOCALE).orElseThrow();
		});
	}
	
	public T getEntry(Identifier id) {
		String locale = MinecraftClient.getInstance().getLanguageManager().getLanguage();
		PrivateRegistry<T> localeRegistry = data.get(locale);
		if (localeRegistry != null) {
			Optional<T> result = localeRegistry.get(id);
			if (result.isPresent()) return result.get();
		}
		
		PrivateRegistry<T> fallbackRegistry = data.get(FALLBACK_LOCALE);
		if (fallbackRegistry != null) {
			Optional<T> result = fallbackRegistry.get(id);
			if (result.isPresent()) return result.get();
		}
		
		if (defaultEntrySupplier != null) {
			return defaultEntrySupplier.apply(id);
		}
		
		// Absolutely a last resort.
		return null;
	}
	
	public LocalizedResourcePool<T> setDefaultEntrySupplier(Function<Identifier, T> supplier) {
		this.defaultEntrySupplier = supplier;
		return this;
	}
	
	public List<LocalizedResource<T>> prepareResources(ResourceManager manager) {
		final Logger LOGGER = DeadStarsMod.LOGGER;
		List<LocalizedResource<T>> result = new ArrayList<>();
		
		Map<Identifier, Resource> resources = manager.findResources(basePath, resourcePredicate);
		for(Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
			LOGGER.info("Considering resource: "+entry.getKey());
			Identifier id = entry.getKey();
			String path = id.getPath();
			
			if (path.startsWith("/")) path = path.substring(1);
			if (path.startsWith(basePath)) {
				path = path.substring(basePath.length());
			}
			
			if (path.startsWith("/")) path = path.substring(1);
			
			// Grab locale
			int localeEnd = path.indexOf("/");
			if (localeEnd == -1) {
				LOGGER.warn("Resource \""+id+"\" is supposed to be in a locale folder! Skipping.");
				continue;
			}
			String locale = path.substring(0, localeEnd);
			String remainingPath = path.substring(localeEnd+1);
			if (stripExtension) {
				int dot = remainingPath.lastIndexOf('.');
				if (dot != -1) remainingPath = remainingPath.substring(0, dot);
			}
			
			Identifier registrationId = Identifier.of(id.getNamespace(), remainingPath);
			
			try {
				// Load the resource
				Optional<T> t = resourceLoader.apply(registrationId, entry.getValue());
				if (t.isPresent()) result.add(new LocalizedResource<T>(locale, registrationId, t.get()));
			} catch (Throwable t) {
				LOGGER.warn("Resource loader function errored out", t);
			}
		}
		
		return result;
	}
	
	public void applyResources(List<LocalizedResource<T>> resources) {
		data.clear();
		data.computeIfAbsent(FALLBACK_LOCALE, PrivateRegistry::new);
		
		for(LocalizedResource<T> resource : resources) {
			PrivateRegistry<T> localeRegistry = data.computeIfAbsent(resource.locale(), PrivateRegistry::new);
			localeRegistry.register(resource.id(), resource.value());
			DeadStarsMod.LOGGER.info("Registered "+resource.id()+" in locale "+resource.locale());
		}
	}
	
	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		var future = CompletableFuture.supplyAsync(() -> prepareResources(manager), prepareExecutor);
		var combinedFuture = future
				.thenCompose(synchronizer::whenPrepared)
				.thenAcceptAsync(this::applyResources, applyExecutor);
		
		return combinedFuture;
	}

	@Override
	public Identifier getFabricId() {
		return reloaderId;
	}
	
	private static record LocalizedResource<T>(String locale, Identifier id, T value) {}
}
