package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LanguageDatagen extends LanguageProvider {
    public LanguageDatagen(PackOutput packOutput, String modId, String locale) {
        super(packOutput, modId, locale);
    }

    @Override
    protected void addTranslations() {
        add("item.omnicompass.compass", "Unknown Compass");

        add("compass_type.minecraft.biome", "Biome Compass");
        add("compass_type.minecraft.structure", "Structure Compass");

        add("gui.omnicompass.search_hint", "Search...");
        add("gui.omnicompass.title", "Select a Target");

        add("omnicompass.compass.scanning", "Scanning for targets...");
        add("omnicompass.compass.found", "Target found!");
        add("omnicompass.compass.not_found", "No valid targets found");

        add("omnicompass.configuration.structure_search_range", "Structure Search Range");
        add("omnicompass.configuration.structure_search_range.tooltip", "Maximum distance (in structure spacing, varies by structure) to search for structures. Higher values may take cause the search to take longer.");
        add("omnicompass.configuration.biome_search_range", "Biome Search Range");
        add("omnicompass.configuration.biome_search_range.tooltip", "Maximum distance (in blocks) to search for biomes. Higher values may take cause the search to take longer.");
        add("omnicompass.configuration.horizontal_resolution", "Horizontal Resolution");
        add("omnicompass.configuration.horizontal_resolution.tooltip", "The horizontal resolution (in blocks) to use when searching for biomes. Lower values may cause the search to take longer, but may be more accurate.");
        add("omnicompass.configuration.vertical_resolution", "Vertical Resolution");
        add("omnicompass.configuration.vertical_resolution.tooltip", "The vertical resolution (in blocks) to use when searching for biomes. Lower values may cause the search to take longer, but may be more accurate.");
    }
}
