package piotro15.omnicompass.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;

public record CompassSelectEntryPacket (
        Identifier compassType,
        Identifier targetType,
        Identifier targetId
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompassSelectEntryPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "select_entry"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, CompassSelectEntryPacket> CODEC =
            StreamCodec.ofMember(CompassSelectEntryPacket::encode, CompassSelectEntryPacket::decode);

    public static void encode(CompassSelectEntryPacket msg, FriendlyByteBuf buf) {
        buf.writeIdentifier(msg.compassType);
        buf.writeIdentifier(msg.targetType);
        buf.writeIdentifier(msg.targetId);
    }

    public static CompassSelectEntryPacket decode(FriendlyByteBuf buf) {
        Identifier compassType = buf.readIdentifier();
        Identifier targetType = buf.readIdentifier();
        Identifier targetId = buf.readIdentifier();
        return new CompassSelectEntryPacket(compassType, targetType, targetId);
    }
}
