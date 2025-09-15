package piotro15.omnicompass.util;

import com.mojang.serialization.Codec;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public abstract class Platform {
    private static Platform platform = null;

    public static void setup(Platform platform) {
        Platform.platform = platform;
    }

    public static Platform getInstance() {
        return platform;
    }

    public abstract <T> void registerDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec);

    public abstract void registerItemTint(ItemColor itemColor, Supplier<Item> itemSupplier);

    public abstract void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    public abstract void sendToServer(CustomPacketPayload payload);
}
