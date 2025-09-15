package piotro15.omnicompass.common.items.compass.targets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface SingleTarget extends CompassTarget {
    Component displayName();

    void find(Player player, ResourceLocation compassId, ResourceLocation entryType, ResourceLocation entryId);

    boolean isUnlocked(ServerPlayer player);
}
