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
    }
}
