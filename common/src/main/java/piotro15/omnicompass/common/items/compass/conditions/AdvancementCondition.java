package piotro15.omnicompass.common.items.compass.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import piotro15.omnicompass.OmniCompass;

public record AdvancementCondition(
        Identifier name
) implements CompassTargetCondition {
    public static final Identifier id = Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "advancement");

    public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("name").forGetter(AdvancementCondition::name)
            ).apply(instance, AdvancementCondition::new)
    );

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public boolean isMet(ServerPlayer player) {
        var advancement = player.registryAccess().lookupOrThrow(Registries.ADVANCEMENT).get(name);

        return advancement.filter(advancementReference -> player.getAdvancements().getOrStartProgress(new AdvancementHolder(name, advancementReference.value())).isDone()).isPresent();
    }
}
