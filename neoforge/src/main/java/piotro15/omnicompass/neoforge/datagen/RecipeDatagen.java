package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeDatagen extends RecipeProvider {

    protected RecipeDatagen(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        ItemStackTemplate biomeCompass = new ItemStackTemplate(ModItems.COMPASS.get(), DataComponentPatch.builder().set(ModDataComponents.COMPASS_TYPE.get(), Identifier.fromNamespaceAndPath("minecraft", "biome")).build());
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS, biomeCompass)
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', ItemTags.PLANKS)
                .define('C', Items.COMPASS)
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output, String.valueOf(Identifier.fromNamespaceAndPath("omnicompass", "biome_compass")));

        ItemStackTemplate structureCompass = new ItemStackTemplate(ModItems.COMPASS.get(), DataComponentPatch.builder().set(ModDataComponents.COMPASS_TYPE.get(), Identifier.fromNamespaceAndPath("minecraft", "structure")).build());
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS, structureCompass)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .define('B', Items.BRICK)
                .define('C', Items.COMPASS)
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output, String.valueOf(Identifier.fromNamespaceAndPath("omnicompass", "structure_compass")));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new RecipeDatagen(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return "OmniCompass Recipes";
        }
    }
}
