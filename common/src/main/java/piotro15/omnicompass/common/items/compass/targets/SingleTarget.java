package piotro15.omnicompass.common.items.compass.targets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface SingleTarget extends CompassTarget {
    Component displayName();

    void find(Player player, Identifier compassId, Identifier entryType, Identifier entryId);

    boolean isTag();

    boolean isUnlocked(ServerPlayer player);
}
