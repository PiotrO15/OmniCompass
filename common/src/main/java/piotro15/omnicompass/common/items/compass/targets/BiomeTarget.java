package piotro15.omnicompass.common.items.compass.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.items.CompassItem;
import piotro15.omnicompass.common.items.compass.CompassTargetConditionRegistry;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.conditions.CompassTargetCondition;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.common.search.AsyncLocator;

import java.util.List;

public record BiomeTarget(
    HolderSet<Biome> name,
    List<CompassTargetCondition> conditions
) implements SingleTarget {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "biome");

    public static final MapCodec<BiomeTarget> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("name").forGetter(BiomeTarget::name),
                    CompassTargetConditionRegistry.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(BiomeTarget::conditions)
            ).apply(instance, BiomeTarget::new)
    );

    @Override
    public ResourceLocation targetType() {
        return id;
    }

    @Override
    public ResourceLocation entryId() {
        return name.unwrap().map(
                TagKey::location,
                list -> list.getFirst().unwrapKey().orElseThrow().location()
        );
    }

    @Override
    public Component displayName() {
        return name.unwrap().map(
                tag -> Component.translatable("tag.biome." + tag.location().toLanguageKey()),
                list -> Component.translatable("biome." + list.getFirst().unwrapKey().orElseThrow().location().toLanguageKey())
        );
    }

    @Override
    public void find(Player player, ResourceLocation compassId, ResourceLocation entryType, ResourceLocation entryId) {
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());

        if (!(stack.getItem() instanceof CompassItem)) {
            return;
        }

        Registry<CompassType> registry = player.level().registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
        CompassType compassType = registry.get(compassId);

        if (compassType == null) {
            return;
        }

        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        SingleTarget singleTarget = compassType.getTargets(serverLevel).stream().filter(entry -> entry.entryId().equals(entryId) && entry.targetType().equals(entryType)).findFirst().orElseThrow();

        if (singleTarget instanceof BiomeTarget biomeTarget) {
            AsyncLocator.findBiome(serverLevel, player, stack, biomeTarget, player.getOnPos());
        }
    }

    @Override
    public boolean isUnlocked(ServerPlayer player) {
        return conditions.stream().allMatch(condition -> condition.isMet(player));
    }
}
