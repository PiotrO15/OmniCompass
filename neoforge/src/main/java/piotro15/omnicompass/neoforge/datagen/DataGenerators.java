package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import piotro15.omnicompass.OmniCompass;

@EventBusSubscriber(modid = OmniCompass.MOD_ID)
public class DataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

//        event.createProvider(RecipeDatagen.Runner::new);
        gen.addProvider(true, new LanguageDatagen(packOutput, OmniCompass.MOD_ID, "en_us"));
        gen.addProvider(true, new ItemModelDatagen(packOutput, OmniCompass.MOD_ID));

//        gen.addProvider(
//                true,
//                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
//                        output,
//                        lookupProvider,
//                        new RegistrySetBuilder()
//                                .add(ModRegistries.COMPASS_TYPE, CompassTypeDatagen::registerCompassTypes),
//                        Set.of(OmniCompass.MOD_ID, "minecraft")
//                )
//        );
    }
}
