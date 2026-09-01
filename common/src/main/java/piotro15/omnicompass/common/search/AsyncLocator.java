package piotro15.omnicompass.common.search;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import piotro15.omnicompass.common.items.CompassItem;
import piotro15.omnicompass.common.items.compass.targets.BiomeTarget;
import piotro15.omnicompass.common.items.compass.targets.StructureTarget;
import piotro15.omnicompass.config.CommonConfig;

import java.util.concurrent.CompletableFuture;

public class AsyncLocator {
    public static void findStructure(ServerLevel level, Player player,
                                     ItemStack compassStack, StructureTarget structureTarget,
                                     BlockPos origin) {
        CompletableFuture.supplyAsync(() -> {
            Pair<BlockPos, Holder<Structure>> result =
                    level.getChunkSource().getGenerator()
                            .findNearestMapStructure(level, structureTarget.name(), origin, CommonConfig.INSTANCE.structureSearchRange.get(), false);

            return result != null ? result.getFirst() : null;
        }).thenAcceptAsync(foundPos -> {
            if (foundPos != null) {
                player.sendOverlayMessage(Component.translatable("omnicompass.compass.found"));
                CompassItem.setTarget(compassStack, structureTarget, level.dimension(), foundPos);
            } else {
                player.sendOverlayMessage(Component.translatable("omnicompass.compass.not_found"));
            }
        }, level.getServer());
    }

    public static void findBiome(ServerLevel level, Player player,
                                 ItemStack compassStack, BiomeTarget targetBiome,
                                 BlockPos origin) {
        CompletableFuture.supplyAsync(() -> {
            Pair<BlockPos, Holder<Biome>> pair = level.findClosestBiome3d(biome -> {
                if (targetBiome.name().size() == 1) {
                    return biome.is(targetBiome.name().get(0).unwrapKey().orElseThrow());
                } else {
                    return biome.is(targetBiome.name().unwrapKey().orElseThrow());
                }
            }, origin, CommonConfig.INSTANCE.biomeSearchRange.get(), CommonConfig.INSTANCE.horizontalResolution.get(), CommonConfig.INSTANCE.verticalResolution.get());

            return pair != null ? pair.getFirst() : null;
        }).thenAcceptAsync(foundPos -> {
            if (foundPos != null) {
                player.sendOverlayMessage(Component.translatable("omnicompass.compass.found"));
                CompassItem.setTarget(compassStack, targetBiome, level.dimension(), foundPos);
            } else {
                player.sendOverlayMessage(Component.translatable("omnicompass.compass.not_found"));
            }
        }, level.getServer());
    }
}