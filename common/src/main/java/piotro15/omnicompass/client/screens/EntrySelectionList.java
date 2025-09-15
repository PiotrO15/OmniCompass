package piotro15.omnicompass.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.items.compass.targets.SingleTarget;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
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

    public void addEntry(SingleTarget entry) {
        super.addEntry(new Entry(entry));
    }

    public void clearEntries() {
        super.clearEntries();
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final SingleTarget entry;

        Entry(SingleTarget entry) {
            this.entry = entry;
        }

        @Override
        public @NotNull Component getNarration() {
            return entry.displayName();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Platform.getInstance().sendToServer(new CompassSelectEntryPacket(compassType, entry.targetType(), entry.entryId()));
            Minecraft.getInstance().setScreen(null);
            return true;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int y, int x, int entryHeight, int m, int n, int o, boolean hovered, float f) {
            Minecraft mc = Minecraft.getInstance();

            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                    entry.targetType().getNamespace(),
                    "textures/entry/" + entry.targetType().getPath() + ".png"
            );

            int textureX = EntrySelectionList.this.width / 2 + mc.font.width(entry.displayName()) / 2 + 4; // po lewej stronie tekstu
            int textureY = (y + m / 2) - 6;

            guiGraphics.blit(texture, textureX, textureY, 0, 0, 10, 10, 10, 10);


            guiGraphics.drawCenteredString(
                    mc.font,
                    entry.displayName(),
                    EntrySelectionList.this.width / 2,
                    (y + m / 2) - 9 / 2,
                    hovered ? 0xFFFFA0 : 0xFFFFFF
            );
        }
    }
}
