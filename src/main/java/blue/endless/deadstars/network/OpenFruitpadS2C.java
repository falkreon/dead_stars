package blue.endless.deadstars.network;

import blue.endless.deadstars.DeadStarsMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record OpenFruitpadS2C(Identifier logId, int fruitpadColor) implements CustomPayload {
	public static final PacketCodec<RegistryByteBuf, OpenFruitpadS2C> PACKET_CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC, OpenFruitpadS2C::logId,
			PacketCodecs.VAR_INT, OpenFruitpadS2C::fruitpadColor,
			OpenFruitpadS2C::new
			);
	public static final Identifier ID = DeadStarsMod.identifier("open_fruitpad");
	public static final Id<OpenFruitpadS2C> PAYLOAD_ID = new CustomPayload.Id<>(ID);
	
	public void send(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, this);
	}

	@Override
	public Id<OpenFruitpadS2C> getId() {
		return PAYLOAD_ID;
	}
}
