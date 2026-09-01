package piotro15.omnicompass.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import piotro15.omnicompass.util.Platform;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricPlatform extends Platform {
    @Override
    public <T> void registerDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        DynamicRegistries.registerSynced(key, codec, codec);
    }

    @Override
    public Supplier<Item> registerItem(String name, Function<Item.Properties, ? extends Item> func, Supplier<Item.Properties> properties) {
        Item item = OmniCompassFabric.register(name, func, properties.get());
        return () -> item;
    }

    @Override
    public <R> Supplier<DataComponentType<R>> registerDataComponentType(Identifier identifier, Supplier<DataComponentType<R>> supplier) {
        DataComponentType<R> dataComponentType = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, identifier, supplier.get());
        return () -> dataComponentType;
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
