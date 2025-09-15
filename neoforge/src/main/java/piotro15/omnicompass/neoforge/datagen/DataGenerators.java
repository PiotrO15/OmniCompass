package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.registry.ModRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = OmniCompass.MOD_ID)
public class DataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(true, new RecipeDatagen(packOutput, lookupProvider));
        gen.addProvider(true, new LanguageDatagen(packOutput, OmniCompass.MOD_ID, "en_us"));
        gen.addProvider(true, new ItemModelDatagen(packOutput, OmniCompass.MOD_ID, event.getExistingFileHelper()));

        gen.addProvider(
                true,
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        lookupProvider,
                        new RegistrySetBuilder()
                                .add(ModRegistries.COMPASS_TYPE, CompassTypeDatagen::registerCompassTypes),
                        Set.of(OmniCompass.MOD_ID, "minecraft")
                )
        );
    }
}
