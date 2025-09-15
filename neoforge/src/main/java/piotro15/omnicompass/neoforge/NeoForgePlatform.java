package piotro15.omnicompass.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import piotro15.omnicompass.util.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoForgePlatform extends Platform {
    public static final List<DataRegistryRegisterable<?>> dataRegistryRegisterables = new ArrayList<>();
    public static final BiMap<Supplier<Item>, ItemColor> itemColors = HashBiMap.create();

    @Override
    public <T> void registerDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        dataRegistryRegisterables.add(new DataRegistryRegisterable<>(key, codec, codec));
    }

    @Override
    public void registerItemTint(ItemColor itemColor, Supplier<Item> itemSupplier) {
        itemColors.put(itemSupplier, itemColor);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public record DataRegistryRegisterable<T>(ResourceKey<Registry<T>> key, Codec<T> codec, Codec<T> networkCodec) {
        public void register(DataPackRegistryEvent.NewRegistry event) {
            if(networkCodec == null)
                event.dataPackRegistry(key, codec);
            else
                event.dataPackRegistry(key, codec, networkCodec);
        }
    }
}
