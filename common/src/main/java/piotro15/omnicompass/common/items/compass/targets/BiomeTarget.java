package piotro15.omnicompass.common.items.compass.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    Holder<Biome> name,
    List<CompassTargetCondition> conditions
) implements SingleTarget {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "biome");

    public static final MapCodec<BiomeTarget> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    RegistryFixedCodec.create(Registries.BIOME).fieldOf("name").forGetter(BiomeTarget::name),
//                    ResourceLocation.CODEC.fieldOf("name").forGetter(BiomeTarget::name),
                    CompassTargetConditionRegistry.CODEC.listOf().fieldOf("conditions").forGetter(BiomeTarget::conditions)
            ).apply(instance, BiomeTarget::new)
    );

    @Override
    public ResourceLocation targetType() {
        return id;
    }

    @Override
    public ResourceLocation entryId() {
        return name.unwrapKey().get().location();
    }

    @Override
    public Component displayName() {
        return Component.translatable("biome." + name.unwrapKey().get().location().getNamespace() + "." + name.unwrapKey().get().location().getPath());
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

        AsyncLocator.findBiome(serverLevel, player, stack, ResourceKey.create(Registries.BIOME, singleTarget.entryId()), player.getOnPos());
    }

    @Override
    public boolean isUnlocked(ServerPlayer player) {
        return conditions.stream().allMatch(condition -> condition.isMet(player));
    }
}
