package piotro15.omnicompass.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;

public record CompassSelectEntryPacket (
        ResourceLocation compassType,
        ResourceLocation targetType,
        ResourceLocation targetId
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompassSelectEntryPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "select_entry"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, CompassSelectEntryPacket> CODEC =
            StreamCodec.ofMember(CompassSelectEntryPacket::encode, CompassSelectEntryPacket::decode);

    public static void encode(CompassSelectEntryPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.compassType);
        buf.writeResourceLocation(msg.targetType);
        buf.writeResourceLocation(msg.targetId);
    }

    public static CompassSelectEntryPacket decode(FriendlyByteBuf buf) {
        ResourceLocation compassType = buf.readResourceLocation();
        ResourceLocation targetType = buf.readResourceLocation();
        ResourceLocation targetId = buf.readResourceLocation();
        return new CompassSelectEntryPacket(compassType, targetType, targetId);
    }
}
