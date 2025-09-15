package piotro15.omnicompass.fabric;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import piotro15.omnicompass.util.Platform;

import java.util.function.Supplier;

public class FabricPlatform extends Platform {
    @Override
    public <T> void registerDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        DynamicRegistries.registerSynced(key, codec, codec);
    }

    @Override
    public void registerItemTint(ItemColor itemColor, Supplier<Item> itemSupplier) {
        ColorProviderRegistry.ITEM.register(itemColor, itemSupplier.get());
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
