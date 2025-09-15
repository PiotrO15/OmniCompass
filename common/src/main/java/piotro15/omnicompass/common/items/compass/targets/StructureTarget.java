package piotro15.omnicompass.common.items.compass.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.items.CompassItem;
import piotro15.omnicompass.common.items.compass.CompassTargetConditionRegistry;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.conditions.CompassTargetCondition;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.common.search.AsyncLocator;

import java.util.List;

public record StructureTarget(
        ResourceLocation name,
        List<CompassTargetCondition> conditions
) implements SingleTarget {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "structure");

    public static final MapCodec<StructureTarget> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(StructureTarget::name),
                    CompassTargetConditionRegistry.CODEC.listOf().fieldOf("conditions").forGetter(StructureTarget::conditions)
            ).apply(instance, StructureTarget::new)
    );

    @Override
    public ResourceLocation targetType() {
        return id;
    }

    @Override
    public ResourceLocation entryId() {
        return name;
    }

    @Override
    public Component displayName() {
        Language language = Language.getInstance();
        String key = "structure." + name.getNamespace() + "." + name.getPath();

        if (language.has(key)) {
            return Component.translatable(name.toString());
        } else {
            String[] words = name.getPath().split("_");
            for (int i = 0; i < words.length; i++) {
                if (!words[i].isEmpty()) {
                    words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
                }
            }
            return Component.literal(String.join(" ", words));
        }
    }

    @Override
    public void find(Player player, ResourceLocation compassId, ResourceLocation entryType, ResourceLocation entryId) {
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());

        if (!(stack.getItem() instanceof CompassItem)) {
            return;
        }

        Registry<CompassType> compassTypeRegistry = player.level().registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
        CompassType compassType = compassTypeRegistry.get(compassId);

        if (compassType == null) {
            return;
        }

        Level level = player.level();

        if (level.isClientSide()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        Registry<Structure> registry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Holder<Structure> structureHolder = registry.getHolder(entryId).orElse(null);
        if (structureHolder == null) return;

        HolderSet<Structure> holderSet = HolderSet.direct(structureHolder);
        BlockPos origin = player.getOnPos();

        AsyncLocator.findStructure(serverLevel, player, stack, holderSet, origin, 100);
    }

    @Override
    public boolean isUnlocked(ServerPlayer player) {
        return conditions.stream().allMatch(condition -> condition.isMet(player));
    }
}
