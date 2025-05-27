package blue.endless.deadstars;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryContext<TValue> {
	private final Registry<TValue> registry;
	private final List<Lazy<? extends TValue>> values = new ArrayList<>();
	
	public RegistryContext(Registry<TValue> registry) {
		this.registry = registry;
	}
	
	public <TValueReturn extends TValue> Lazy<TValueReturn> register(Identifier id, Supplier<TValueReturn> supplier) {
		Lazy<TValueReturn> lazySupplier = Lazy.create(() -> Registry.register(registry, id, supplier.get()));
		
		values.add(lazySupplier);
		
		return lazySupplier;
	}
	
	public void registerAll() {
		for (Lazy<? extends TValue> lazy : values) {
			lazy.get();
		}
		
		values.clear();
	}
}
