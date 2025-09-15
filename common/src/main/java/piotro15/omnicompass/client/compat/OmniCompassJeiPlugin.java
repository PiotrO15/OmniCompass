package piotro15.omnicompass.client.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import piotro15.omnicompass.OmniCompass;
import piotro15.omnicompass.common.registry.ModDataComponents;
import piotro15.omnicompass.common.registry.ModItems;

@JeiPlugin
public class OmniCompassJeiPlugin implements IModPlugin {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OmniCompass.MOD_ID, "jei_compat");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.COMPASS.get(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
                return ingredient.get(ModDataComponents.COMPASS_TYPE.get());
            }

            @Override
            public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
                ResourceLocation blendId = ingredient.get(ModDataComponents.COMPASS_TYPE.get());

                if (blendId != null) {
                    return blendId.toString();
                }
                return "";
            }
        });
    }
}