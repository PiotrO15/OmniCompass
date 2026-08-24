package piotro15.omnicompass.client.screens;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import piotro15.omnicompass.common.registry.CompassTargetType;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;

public class CompassOverlay implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.is(ModItems.COMPASS)) {
                CompassTargetType targetType = itemStack.get(ModDataComponents.TARGET_TYPE.get());
                GlobalPos target = itemStack.get(ModDataComponents.TARGET_POSITION.get());
                if (targetType == null || target == null) return;

                guiGraphics.drawString(Minecraft.getInstance().font, targetType.getTargetType(), 10, 10, 0xFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, targetType.getTargetName(), 10, 20, 0xFFFFFF);

                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("omnicompass.target.position"), 10, 40, 0xFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, target.pos().toShortString(), 10, 50, 0xFFFFFF);

                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("omnicompass.target.distance"), 10, 70, 0xFFFFFF);
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("omnicompass.target.distance.blocks", (int) Math.sqrt(target.pos().distToCenterSqr(player.position()))), 10, 80, 0xFFFFFF);
            }
        }
    }
}
