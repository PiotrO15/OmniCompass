package piotro15.omnicompass.common.items.compass;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.common.items.compass.targets.*;

public class CompassTargetRegistry {
    private static final BiMap<ResourceLocation, MapCodec<? extends CompassTarget>> entryCodecs = HashBiMap.create();

    public static final Codec<CompassTarget> CODEC = ResourceLocation.CODEC.dispatch(CompassTarget::targetType, entryCodecs::get);

    public static <T extends CompassTarget> void registerEntry(ResourceLocation id, MapCodec<T> codec) {
        if (entryCodecs.containsKey(id)) {
            throw new IllegalArgumentException("Compass entry with id " + id + " is already registered.");
        }
        entryCodecs.put(id, codec);
    }

    public static void registerEntries() {
        registerEntry(BiomeTarget.id, BiomeTarget.CODEC);
        registerEntry(StructureTarget.id, StructureTarget.CODEC);
        registerEntry(AllOfTarget.id, AllOfTarget.CODEC);
    }
}
