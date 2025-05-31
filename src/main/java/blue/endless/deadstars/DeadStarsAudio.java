package blue.endless.deadstars;

import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class DeadStarsAudio {
	private static final RegistryContext<SoundEvent> REGISTRY_CONTEXT = new RegistryContext<>(Registries.SOUND_EVENT);
	
	public static final Lazy<SoundEvent> EPILOGUE_OF_ERAS = register("music.epilogue_of_eras");
	public static final Lazy<SoundEvent> CITY_BESIEGED = register("music.city_besieged");
	
	public static void register() {
		REGISTRY_CONTEXT.registerAll();
	}
	
	private static Lazy<SoundEvent> register(String path) {
		Identifier soundId = DeadStarsMod.identifier(path);
		
		return REGISTRY_CONTEXT.register(soundId, () -> SoundEvent.of(soundId));
	}
}
