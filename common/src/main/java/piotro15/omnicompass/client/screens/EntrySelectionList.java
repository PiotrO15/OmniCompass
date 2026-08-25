package piotro15.omnicompass.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.registry.CompassTargetType;
import piotro15.omnicompass.util.Platform;

public class EntrySelectionList extends AbstractSelectionList<EntrySelectionList.Entry> {
    private final ResourceLocation compassType;

    public EntrySelectionList(Minecraft minecraft, int i, int j, int k, int l, ResourceLocation compassType) {
        super(minecraft, i, j, k, l);
        this.compassType = compassType;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {

    }

    public void addEntry(CompassTargetType entry) {
        super.addEntry(new Entry(entry));
    }

    public void clearEntries() {
        super.clearEntries();
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final CompassTargetType entry;

        Entry(CompassTargetType entry) {
            this.entry = entry;
        }

        @Override
        public @NotNull Component getNarration() {
            return entry.getTargetName();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Platform.getInstance().sendToServer(new CompassSelectEntryPacket(compassType, entry.type(), entry.id()));
            Minecraft.getInstance().setScreen(null);
            return true;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int y, int x, int entryHeight, int m, int n, int o, boolean hovered, float f) {
            Minecraft mc = Minecraft.getInstance();

            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                    entry.type().getNamespace(),
                    "textures/entry/" + entry.type().getPath() + ".png"
            );

            int textureX = EntrySelectionList.this.width / 2 + mc.font.width(entry.getTargetName()) / 2 + 4;
            int textureY = (y + m / 2) - 6;

            guiGraphics.blit(texture, textureX, textureY, 0, 0, 10, 10, 10, 10);


            guiGraphics.drawCenteredString(
                    mc.font,
                    entry.getTargetName(),
                    EntrySelectionList.this.width / 2,
                    (y + m / 2) - 9 / 2,
                    hovered ? 0xFFFFA0 : 0xFFFFFF
            );
        }
    }
}
