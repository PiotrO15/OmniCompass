package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelDatagen extends ItemModelProvider {
    public ItemModelDatagen(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (int i = 0; i < 32; i++) {
            getBuilder("omnicompass:compass/needle_" + (i < 10 ? "0" + i : i))
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", "omnicompass:item/needle_layer_0")
                    .texture("layer1", "omnicompass:item/shade_" + (i < 10 ? "0" + i : i))
                    .texture("layer2", "omnicompass:item/needle_" + (i < 10 ? "0" + i : i));
        }
    }
}
