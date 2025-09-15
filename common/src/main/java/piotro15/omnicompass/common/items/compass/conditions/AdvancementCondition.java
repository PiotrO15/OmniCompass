package piotro15.omnicompass.common.items.compass.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import piotro15.omnicompass.OmniCompass;

public record AdvancementCondition(
        ResourceLocation name
) implements CompassTargetCondition {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "advancement");

    public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(AdvancementCondition::name)
            ).apply(instance, AdvancementCondition::new)
    );

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public boolean isMet(ServerPlayer player) {
        return player.getAdvancements().getOrStartProgress(player.server.getAdvancements().get(name)).isDone();
    }
}
