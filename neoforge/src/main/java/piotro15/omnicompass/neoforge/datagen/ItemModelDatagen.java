package piotro15.omnicompass.neoforge.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.client.CompassNeedleTintSource;
import piotro15.omnicompass.client.OmniCompassAngle;
import piotro15.omnicompass.client.OmniCompassAngleState;
import piotro15.omnicompass.common.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ItemModelDatagen extends ModelProvider {

    public ItemModelDatagen(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        itemModels.itemModelOutput.accept(
                ModItems.COMPASS.get(),
                ItemModelUtils.plainModel(Identifier.withDefaultNamespace("oak_sapling"))
        );

        List<RangeSelectItemModel.Entry> overrides = createNeedleModels(itemModels.modelOutput);

        itemModels.itemModelOutput.register(
                OmniCompass.id("compass"),
                new ClientItem(new CompositeModel.Unbaked(List.of(
                        ItemModelUtils.plainModel(OmniCompass.id("item/compass_base")),
                        ItemModelUtils.rangeSelect(new OmniCompassAngle(new OmniCompassAngleState(true)), 32.0F, overrides)
                ), Optional.empty()), ClientItem.Properties.DEFAULT)
        );

        itemModels.itemModelOutput.register(
                OmniCompass.id("biome_compass"),
                new ClientItem(new CompositeModel.Unbaked(List.of(
                        ItemModelUtils.plainModel(OmniCompass.id("item/biome_compass_base")),
                        ItemModelUtils.rangeSelect(new OmniCompassAngle(new OmniCompassAngleState(true)), 32.0F, overrides)
                ), Optional.empty()), ClientItem.Properties.DEFAULT)
        );

        itemModels.itemModelOutput.register(
                OmniCompass.id("structure_compass"),
                new ClientItem(new CompositeModel.Unbaked(List.of(
                        ItemModelUtils.plainModel(OmniCompass.id("item/structure_compass_base")),
                        ItemModelUtils.rangeSelect(new OmniCompassAngle(new OmniCompassAngleState(true)), 32.0F, overrides)
                ), Optional.empty()), ClientItem.Properties.DEFAULT)
        );
    }

    private List<RangeSelectItemModel.Entry> createNeedleModels(BiConsumer<Identifier, ModelInstance> modelOutput) {
        List<RangeSelectItemModel.Entry> overrides = new ArrayList<>();

        ItemModel.Unbaked base = ItemModelUtils.tintedModel(ModelTemplates.TWO_LAYERED_ITEM.create(OmniCompass.id("compass/needle_16"), TextureMapping.layered(new Material(OmniCompass.id("item/shade_16")), new Material(OmniCompass.id("item/needle_16"))), modelOutput), new CompassNeedleTintSource(true), new CompassNeedleTintSource(false));
        overrides.add(ItemModelUtils.override(base, 0.0F));

        for (int i = 1; i < 32; i++) {
            String textureIndex = String.format(Locale.ROOT, "%02d", Mth.positiveModulo(i - 16, 32));

            Identifier id = OmniCompass.id("compass/needle_" + textureIndex);
            ItemModel.Unbaked overrideModel = ItemModelUtils.tintedModel(ModelTemplates.TWO_LAYERED_ITEM.create(id, TextureMapping.layered(new Material(OmniCompass.id("item/shade_" + textureIndex)), new Material(OmniCompass.id("item/needle_" + textureIndex))), modelOutput), new CompassNeedleTintSource(true), new CompassNeedleTintSource(false));

            overrides.add(ItemModelUtils.override(overrideModel, i - 0.5F));
        }

        overrides.add(ItemModelUtils.override(base, 31.5F));
        return overrides;
    }
}
