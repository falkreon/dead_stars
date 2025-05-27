package blue.endless.deadstars.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.util.Identifier;

/**
 * A miniature, unindexed registry that isn't present in the registry-registry, and whose data must be explicitly
 * inserted. Supports freeze states to strongly discourage mutation outside of predefined safe windows.
 * @param <T>
 */
public class PrivateRegistry<T> {
	private String id;
	private Object freezerKey = null;
	private T defaultValue = null;
	private final Map<Identifier,T> registrations = new HashMap<>();
	private List<Consumer<PrivateRegistry<T>>> registryMutationCallbacks = new ArrayList<>();
	
	public PrivateRegistry(String id) {
		this.id = id;
	}
	
	public PrivateRegistry(String id, T defaultValue) {
		this(id);
		this.defaultValue = defaultValue;
	}
	
	public String id() {
		return id;
	}
	
	public boolean isFrozen() {
		return (freezerKey != null);
	}
	
	public void freeze(Object freezerKey) {
		if (this.freezerKey != null) throw new IllegalStateException("Attempted to freeze a frozen registry.");
	}
	
	/**
	 * Unfreezes the registry, allowing its contents to be mutated.
	 * @param freezerKey The object that was used as a key to freeze this registry. Must be reference-equal.
	 */
	public void unfreeze(Object freezerKey) {
		if (this.freezerKey == null) throw new IllegalStateException("Attempted to unfreeze an unfrozen registry.");
		if (this.freezerKey != freezerKey) throw new IllegalStateException("Attempted to unfreeze a registry with the wrong key");
		this.freezerKey = null;
	}
	
	/**
	 * Clears the entire registry. Since this is a mutation operation, the registry MUST NOT be frozen.
	 */
	public void clear() {
		if (this.freezerKey != null) throw new IllegalStateException("Attempted to clear a frozen registry.");
		
		registrations.clear();
	}
	
	/**
	 * Notifies listeners that the registry is "clean" (a.k.a. free from the influence of previous callbacks) and ready
	 * to modify. Typically, during a data reload, the registry is unfrozen, cleared, prepped, and then this method is
	 * called. Finally, the registry is re-frozen.
	 */
	public void fireListeners() {
		if (this.freezerKey != null) throw new IllegalStateException("Attempted to trigger listeners for a frozen registry.");
		
		for(Consumer<PrivateRegistry<T>> listener : registryMutationCallbacks) {
			listener.accept(this);
		}
	}
	
	/**
	 * Registers a value in this registry. Since this is a mutation operation, the registry MUST NOT be frozen.
	 * @param id    The Identifier uniquely referring to this resource.
	 * @param value The value to be associated with this Identifier.
	 * @return True if the key-value pair was successfully associated. False if there was an existing mapping.
	 */
	public boolean register(Identifier id, T value) {
		if (freezerKey != null) throw new IllegalStateException("Registry is frozen.");
		return (registrations.putIfAbsent(id, value) == null);
	}
	
	public T unregister(Identifier id) {
		if (freezerKey != null) throw new IllegalStateException("Registry is frozen.");
		return registrations.remove(id);
	}
	
	public Optional<T> get(Identifier id) {
		return Optional.ofNullable(registrations.getOrDefault(id, defaultValue));
	}
	
	public T getOrThrow(Identifier id) {
		T t = registrations.getOrDefault(id, defaultValue);
		if (t == null) throw new NoSuchElementException("No such element with id \""+id+"\".");
		return t;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Registers a consumer to fire modifiers when this registry is unfrozen.
	 * @param consumer
	 */
	public void registerModifier(Consumer<PrivateRegistry<T>> consumer) {
		
	}
}
