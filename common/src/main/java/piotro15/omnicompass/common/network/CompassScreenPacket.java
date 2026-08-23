package piotro15.omnicompass.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.items.compass.CompassTargetRegistry;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;

import java.util.List;

public record CompassScreenPacket(
        ResourceLocation compassType,
        List<SingleTarget> targets,
        RegistryAccess registryAccess
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CompassScreenPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "screen"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, CompassScreenPacket> CODEC =
            StreamCodec.ofMember(CompassScreenPacket::encode, CompassScreenPacket::decode);

    public static void encode(CompassScreenPacket msg, FriendlyByteBuf buf) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, msg.registryAccess);

        buf.writeResourceLocation(msg.compassType);
        buf.writeInt(msg.targets.size());
        for (SingleTarget target : msg.targets) {
            CompoundTag tag = (CompoundTag) CompassTargetRegistry.CODEC
                    .encodeStart(ops, target)
                    .resultOrPartial()
                    .orElseThrow();
            buf.writeNbt(tag);
        }
    }

    public static CompassScreenPacket decode(FriendlyByteBuf buf) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, Minecraft.getInstance().level.registryAccess());

        ResourceLocation type = buf.readResourceLocation();
        int size = buf.readInt();
        List<SingleTarget> targets = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            CompoundTag tag = buf.readNbt();
            SingleTarget target = (SingleTarget) CompassTargetRegistry.CODEC
                    .parse(ops, tag)
                    .resultOrPartial()
                    .orElseThrow();
            targets.add(target);
        }
        return new CompassScreenPacket(type, targets, Minecraft.getInstance().level.registryAccess());
    }
}
