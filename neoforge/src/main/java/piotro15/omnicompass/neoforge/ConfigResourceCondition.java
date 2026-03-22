package piotro15.omnicompass.neoforge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.config.CommonConfig;

public record ConfigResourceCondition(String key) implements ICondition {
    public static final MapCodec<ConfigResourceCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.fieldOf("key").forGetter(ConfigResourceCondition::key)
            ).apply(instance, ConfigResourceCondition::new)
    );

    @Override
    public boolean test(@NotNull IContext iContext) {
        return switch (key) {
            case "enable_biome_compass" -> CommonConfig.INSTANCE.enableBiomeCompass.get();
            case "enable_structure_compass" -> CommonConfig.INSTANCE.enableStructureCompass.get();
            default -> false;
        };
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
