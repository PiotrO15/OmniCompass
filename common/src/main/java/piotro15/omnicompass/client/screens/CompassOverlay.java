package piotro15.omnicompass.client.screens;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import piotro15.omnicompass.common.registry.CompassTargetType;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;
import piotro15.omnicompass.config.ClientConfig;
import piotro15.omnicompass.config.CommonConfig;

import java.util.ArrayList;
import java.util.List;

public class CompassOverlay implements LayeredDraw.Layer {
    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 10;
    private static final int COLOR = 0xFFFFFF;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack itemStack = player.getOffhandItem();
        if (!itemStack.is(ModItems.COMPASS.get())) {
            itemStack = player.getMainHandItem();
            if (!itemStack.is(ModItems.COMPASS.get())) return;
        }

        CompassTargetType targetType = itemStack.get(ModDataComponents.TARGET_TYPE.get());
        GlobalPos target = itemStack.get(ModDataComponents.TARGET_POSITION.get());
        if (targetType == null || target == null) return;

        int distance = (int) Math.sqrt(target.pos().distToCenterSqr(player.position()));
        boolean showPosition = CommonConfig.INSTANCE.showPosition.get() && ClientConfig.INSTANCE.showPosition.get();

        switch (ClientConfig.INSTANCE.overlayStyle.get()) {
            case COLUMN -> renderColumn(guiGraphics, targetType, target, distance, showPosition);
            case BAR -> renderBar(guiGraphics, targetType, target, distance, showPosition);
            case ACTION_BAR -> renderActionBar(player, targetType, target, distance, showPosition);
            case NONE -> {}
        }
    }

    private void renderBar(GuiGraphics guiGraphics, CompassTargetType targetType, GlobalPos target, int distance, boolean showPosition) {
        MutableComponent line = targetType.getTargetType()
                .append(": ")
                .append(targetType.getTargetName())
                .append(" - ")
                .append(Component.translatable("omnicompass.target.distance.blocks", distance));

        if (showPosition) {
            line = line.append(" (").append(target.pos().toShortString()).append(")");
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int x = screenWidth / 2;
        int y = screenHeight - 85;

        guiGraphics.drawCenteredString(Minecraft.getInstance().font, line, x, y, COLOR);
    }

    private void renderActionBar(Player player, CompassTargetType targetType, GlobalPos target, int distance, boolean showPosition) {
        MutableComponent line = targetType.getTargetType()
                .append(": ")
                .append(targetType.getTargetName())
                .append(" - ")
                .append(Component.translatable("omnicompass.target.distance.blocks", distance));

        if (showPosition) {
            line = line.append(" (").append(target.pos().toShortString()).append(")");
        }

        player.displayClientMessage(line, true);
    }

    private void renderColumn(GuiGraphics guiGraphics, CompassTargetType targetType, GlobalPos target, int distance, boolean showPosition) {
        List<Component> lines = new ArrayList<>();
        lines.add(targetType.getTargetType());
        lines.add(targetType.getTargetName());
        int sections = 2;

        if (showPosition) {
            lines.add(Component.translatable("omnicompass.target.position"));
            lines.add(Component.literal(target.pos().toShortString()));
            sections++;
        }

        lines.add(Component.translatable("omnicompass.target.distance"));
        lines.add(Component.translatable("omnicompass.target.distance.blocks", distance));

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int blockHeight = lines.size() * (LINE_HEIGHT + sections);
        int y = switch (ClientConfig.INSTANCE.verticalAlignment.get()) {
            case TOP -> PADDING;
            case CENTER -> (screenHeight - blockHeight) / 2;
            case BOTTOM -> screenHeight - PADDING - blockHeight;
        };

        int lineNumber = 0;
        for (Component line : lines) {
            int x = switch (ClientConfig.INSTANCE.horizontalAlignment.get()) {
                case LEFT -> PADDING;
                case RIGHT -> screenWidth - PADDING - Minecraft.getInstance().font.width(line);
            };

            guiGraphics.drawString(Minecraft.getInstance().font, line, x, y, COLOR);
            y += LINE_HEIGHT + (++lineNumber % 2 == 0 ? LINE_HEIGHT : 0);
        }
    }
}
