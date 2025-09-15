package piotro15.omnicompass.common.items.compass.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import piotro15.omnicompass.OmniCompass;

import java.util.List;
import java.util.Set;

public record AllOfTarget(
        ResourceLocation entryType
) implements MultiTarget {
    public static final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "all_of");

    public static final MapCodec<AllOfTarget> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("entryType").forGetter(AllOfTarget::entryType)
            ).apply(instance, AllOfTarget::new)
    );

    @Override
    public ResourceLocation targetType() {
        return id;
    }

    @Override
    public ResourceLocation entryId() {
        return null;
    }

    @Override
    public List<SingleTarget> processTargets(ServerLevel level) {
        if (entryType.equals(Registries.BIOME.location())) {
            Set<ResourceLocation> biomes = level.registryAccess().registryOrThrow(Registries.BIOME).keySet();
            return biomes.stream().map(biome -> (SingleTarget) new BiomeTarget(biome, List.of())).toList();
        } else if (entryType.equals(Registries.STRUCTURE.location())) {
            Set<ResourceLocation> biomes = level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet();
            return biomes.stream().map(structure -> (SingleTarget) new StructureTarget(structure, List.of())).toList();
        }
        return List.of();
    }
}
