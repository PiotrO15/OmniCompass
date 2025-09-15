package piotro15.omnicompass.common.items;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.items.compass.CompassType;
import piotro15.omnicompass.common.network.CompassScreenPacket;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModRegistries;
import piotro15.omnicompass.util.Platform;

public class CompassItem extends Item {
    public CompassItem() {
        super(new Properties());
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        ResourceLocation compassType = stack.get(ModDataComponents.COMPASS_TYPE.get());
        if (compassType != null) {
            return Component.translatable(Util.makeDescriptionId("compass_type", compassType));
        }
        return super.getName(stack);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (level.isClientSide) {
            return InteractionResultHolder.fail(itemStack);
        }

        ResourceLocation typeComponent = itemStack.get(ModDataComponents.COMPASS_TYPE.get());

        if (typeComponent != null) {
            Registry<CompassType> registry = level.registryAccess().registryOrThrow(ModRegistries.COMPASS_TYPE);
            CompassType compassType = registry.get(typeComponent);

            if (compassType == null || compassType.entries().isEmpty()) {
                return InteractionResultHolder.fail(itemStack);
            }

            Platform.getInstance().sendToPlayer((ServerPlayer) player, new CompassScreenPacket(typeComponent, compassType.getTargets((ServerLevel) level).stream().filter(target -> target.isUnlocked((ServerPlayer) player)).toList()));
            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }
}
