package piotro15.omnicompass.fabric.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;
import piotro15.omnicompass.config.CommonConfig;
import piotro15.omnicompass.fabric.OmniCompassFabric;

public record ConfigResourceCondition(String key) implements ResourceCondition {
    public static final MapCodec<ConfigResourceCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.fieldOf("key").forGetter(ConfigResourceCondition::key)
            ).apply(instance, ConfigResourceCondition::new)
    );

    @Override
    public ResourceConditionType<?> getType() {
        return OmniCompassFabric.CONFIG_RESOURCE_CONDITION;
    }

    @Override
    public boolean test(HolderLookup.@Nullable Provider provider) {
        return switch (key) {
            case "enable_biome_compass" -> CommonConfig.INSTANCE.enableBiomeCompass.get();
            case "enable_structure_compass" -> CommonConfig.INSTANCE.enableStructureCompass.get();
            default -> false;
        };
    }
}
