package piotro15.omnicompass.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import piotro15.omnicompass.common.network.CompassSelectEntryPacket;
import piotro15.omnicompass.common.registry.CompassTargetType;
import piotro15.omnicompass.util.Platform;

public class EntrySelectionList extends ObjectSelectionList<EntrySelectionList.Entry> {
    private final Identifier compassType;

    public EntrySelectionList(Minecraft minecraft, int i, int j, int k, int l, Identifier compassType) {
        super(minecraft, i, j, k, l);
        this.compassType = compassType;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public void addEntry(CompassTargetType entry) {
        super.addEntry(new Entry(entry));
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
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            Platform.getInstance().sendToServer(new CompassSelectEntryPacket(compassType, entry.type(), entry.id()));
            Minecraft.getInstance().setScreen(null);
            return true;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor guiGraphics, int x, int y, boolean hovered, float v) {
            Minecraft mc = Minecraft.getInstance();

            Identifier texture = Identifier.fromNamespaceAndPath(
                    entry.type().getNamespace(),
                    "textures/entry/" + entry.type().getPath() + ".png"
            );

            int textureX = EntrySelectionList.this.width / 2 + mc.font.width(entry.getTargetName()) / 2 + 4;
            int textureY = getContentYMiddle() - 6;

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED ,texture, textureX, textureY, 0, 0, 10, 10, 10, 10);


            guiGraphics.centeredText(
                    mc.font,
                    entry.getTargetName(),
                    EntrySelectionList.this.width / 2,
                    getContentYMiddle() - 9 / 2,
                    hovered ? 0xFFFFFFA0 : 0xFFFFFFFF
            );
        }
    }
}
