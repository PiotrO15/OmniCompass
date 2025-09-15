package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeDatagen extends RecipeProvider {
    public RecipeDatagen(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(arg, completableFuture);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ItemStack biomeCompass = new ItemStack(ModItems.COMPASS);
        biomeCompass.applyComponents(DataComponentMap.builder().set(ModDataComponents.COMPASS_TYPE, ResourceLocation.fromNamespaceAndPath("minecraft", "biome")).build());
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, biomeCompass)
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', ItemTags.PLANKS)
                .define('C', Items.COMPASS)
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output, ResourceLocation.fromNamespaceAndPath("omnicompass", "biome_compass"));

        ItemStack structureCompass = new ItemStack(ModItems.COMPASS);
        structureCompass.applyComponents(DataComponentMap.builder().set(ModDataComponents.COMPASS_TYPE, ResourceLocation.fromNamespaceAndPath("minecraft", "structure")).build());
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, structureCompass)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .define('B', Items.BRICK)
                .define('C', Items.COMPASS)
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output, ResourceLocation.fromNamespaceAndPath("omnicompass", "structure_compass"));
    }
}
