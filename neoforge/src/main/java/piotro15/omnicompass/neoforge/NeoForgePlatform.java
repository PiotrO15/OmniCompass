package piotro15.omnicompass.neoforge;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.util.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgePlatform extends Platform {
    public static final List<DataRegistryRegisterable<?>> dataRegistryRegisterables = new ArrayList<>();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OmniCompass.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OmniCompass.MOD_ID);


    @Override
    public <T> void registerDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
        dataRegistryRegisterables.add(new DataRegistryRegisterable<>(key, codec, codec));
    }

    @Override
    public Supplier<Item> registerItem(String name, Function<Item.Properties, ? extends Item> func, Supplier<Item.Properties> properties) {
        return ITEMS.registerItem(name, func, properties);
    }

    @Override
    public <R> Supplier<DataComponentType<R>> registerDataComponentType(Identifier identifier, Supplier<DataComponentType<R>> supplier) {
        return DATA_COMPONENTS.register(identifier.getPath(), supplier);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
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
