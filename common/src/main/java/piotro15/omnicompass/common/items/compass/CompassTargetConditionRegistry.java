package piotro15.omnicompass.common.items.compass;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import piotro15.omnicompass.common.items.compass.conditions.AdvancementCondition;
import piotro15.omnicompass.common.items.compass.conditions.CompassTargetCondition;

public class CompassTargetConditionRegistry {
    private static final BiMap<ResourceLocation, MapCodec<? extends CompassTargetCondition>> conditionCodecs = HashBiMap.create();

    public static final Codec<CompassTargetCondition> CODEC = ResourceLocation.CODEC.dispatch(CompassTargetCondition::id, conditionCodecs::get);

    public static <T extends CompassTargetCondition> void registerCondition(ResourceLocation id, MapCodec<T> codec) {
        if (conditionCodecs.containsKey(id)) {
            throw new IllegalArgumentException("Compass target condition with id " + id + " is already registered.");
        }
        conditionCodecs.put(id, codec);
    }

    public static void registerConditions() {
        registerCondition(AdvancementCondition.id, AdvancementCondition.CODEC);
    }
}
