package piotro15.omnicompass.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.registry.CompassTargetType;

import java.util.List;

public record CompassScreenPacket(
        Identifier compassType,
        List<CompassTargetType> targets
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompassScreenPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "screen"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, CompassScreenPacket> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, CompassScreenPacket::compassType,
            CompassTargetType.STREAM_CODEC.apply(ByteBufCodecs.list()), CompassScreenPacket::targets,
            CompassScreenPacket::new
    );
}
