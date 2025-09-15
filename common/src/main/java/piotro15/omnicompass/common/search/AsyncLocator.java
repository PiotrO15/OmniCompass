package piotro15.omnicompass.common.search;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncLocator {
    public static void findStructure(ServerLevel level, Player player,
                                     ItemStack compassStack, HolderSet<Structure> targets,
                                     BlockPos origin, int maxRadius) {
        CompletableFuture.supplyAsync(() -> {
            Pair<BlockPos, Holder<Structure>> result =
                    level.getChunkSource().getGenerator()
                            .findNearestMapStructure(level, targets, origin, maxRadius, false);

            return result != null ? result.getFirst() : null;
        }).thenAcceptAsync(foundPos -> {
            if (foundPos != null) {
                player.displayClientMessage(Component.translatable("omnicompass.compass.found"), true);
                compassStack.set(DataComponents.LODESTONE_TRACKER,
                        new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), foundPos)), false));
            } else {
                player.displayClientMessage(Component.translatable("omnicompass.compass.not_found"), true);
            }
        }, level.getServer());
    }

    public static void findBiome(ServerLevel level, Player player,
                                 ItemStack compassStack, ResourceKey<Biome> targetBiome,
                                 BlockPos origin, int searchRadius) {
        CompletableFuture.supplyAsync(() -> {
            Pair<BlockPos, Holder<Biome>> pair = level.findClosestBiome3d(biome -> biome.is(targetBiome), origin, searchRadius, 32, 64);

            return pair != null ? pair.getFirst() : null;
        }).thenAcceptAsync(foundPos -> {
            if (foundPos != null) {
                player.displayClientMessage(Component.translatable("omnicompass.compass.found"), true);
                compassStack.set(DataComponents.LODESTONE_TRACKER,
                        new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), foundPos)), false));
            } else {
                player.displayClientMessage(Component.translatable("omnicompass.compass.not_found"), true);
            }
        }, level.getServer());
    }
}