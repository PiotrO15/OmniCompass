package piotro15.omnicompass.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.CompassTargetType;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.util.Platform;

import java.util.List;
import java.util.function.Consumer;

public class CompassItem extends Item {
    public CompassItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        Identifier compassType = stack.get(ModDataComponents.COMPASS_TYPE.get());
        if (compassType != null) {
            return Component.translatable(Util.makeDescriptionId("compass_type", compassType));
        }
        return super.getName(stack);
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (level.isClientSide()) {
            return InteractionResult.FAIL;
        }

        Identifier typeComponent = itemStack.get(ModDataComponents.COMPASS_TYPE.get());

        if (typeComponent != null) {
            Registry<CompassType> registry = level.registryAccess().lookupOrThrow(ModRegistries.COMPASS_TYPE);
            CompassType compassType = registry.getValue(typeComponent);

            if (compassType == null || compassType.entries().isEmpty()) {
                return InteractionResult.FAIL;
            }

            List<CompassTargetType> targets = compassType.getTargets((ServerLevel) level).stream()
                    .map(t -> new CompassTargetType(t.targetType(), t.entryId(), t.isTag()))
                    .toList();
            Platform.getInstance().sendToPlayer((ServerPlayer) player, new CompassScreenPacket(typeComponent, targets));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        CompassTargetType targetType = itemStack.get(ModDataComponents.TARGET_TYPE.get());
        if (targetType != null) {
            builder.accept(targetType.getTargetType().append(Component.literal(": ")).append(targetType.getTargetName()).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void setTarget(ItemStack itemStack, SingleTarget target, ResourceKey<Level> dimension, BlockPos blockPos) {
        itemStack.set(ModDataComponents.TARGET_TYPE.get(), new CompassTargetType(target.targetType(), target.entryId(), target.isTag()));
        itemStack.set(ModDataComponents.TARGET_POSITION.get(), new GlobalPos(dimension, blockPos));
    }
}
