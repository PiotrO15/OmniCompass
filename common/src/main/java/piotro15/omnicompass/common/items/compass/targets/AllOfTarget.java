package piotro15.omnicompass.common.items.compass.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.config.CommonConfig;

import java.util.ArrayList;
import java.util.List;

public record AllOfTarget(
        Identifier entryType
) implements MultiTarget {
    public static final Identifier id = Identifier.fromNamespaceAndPath(OmniCompass.MOD_ID, "all_of");

    public static final MapCodec<AllOfTarget> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("entryType").forGetter(AllOfTarget::entryType)
            ).apply(instance, AllOfTarget::new)
    );

    @Override
    public Identifier targetType() {
        return id;
    }

    @Override
    public Identifier entryId() {
        return null;
    }

    @Override
    public List<SingleTarget> processTargets(ServerLevel level) {
        if (entryType.equals(Registries.BIOME.identifier())) {
            Registry<Biome> biomeRegistry = level.registryAccess().lookupOrThrow(Registries.BIOME);
            List<SingleTarget> targets = new ArrayList<>();
            biomeRegistry.asHolderIdMap().iterator().forEachRemaining(entry -> {
                if (!CommonConfig.INSTANCE.biomeBlacklist.get().contains(entry.unwrapKey().get().identifier().toString())) {
                    targets.add(new BiomeTarget(HolderSet.direct(entry), List.of()));
                }
            });

            return targets;
        } else if (entryType.equals(Registries.STRUCTURE.identifier())) {
            Registry<Structure> structureRegistry =  level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
            List<SingleTarget> targets = new ArrayList<>();
            structureRegistry.asHolderIdMap().iterator().forEachRemaining(entry -> {
                if (!CommonConfig.INSTANCE.structureBlacklist.get().contains(entry.unwrapKey().get().identifier().toString())) {
                    targets.add(new StructureTarget(HolderSet.direct(entry), List.of()));
                }
            });

            return targets;
        }
        return List.of();
    }
}
