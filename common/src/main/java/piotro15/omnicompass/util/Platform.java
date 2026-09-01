package piotro15.omnicompass.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.function.Function;
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

    public abstract Supplier<Item> registerItem(String name, Function<Item.Properties, ? extends Item> func, Supplier<Item.Properties> properties);

    public abstract <R> Supplier<DataComponentType<R>> registerDataComponentType(Identifier identifier, Supplier<DataComponentType<R>> supplier);

    public abstract void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    public abstract void sendToServer(CustomPacketPayload payload);
}
