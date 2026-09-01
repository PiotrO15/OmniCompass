package piotro15.omnicompass.client.screens;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import piotro15.omnicompass.common.registry.CompassTargetType;

import java.util.List;
import java.util.Locale;

public class CompassScreen extends Screen {
    private final List<CompassTargetType> unlocked;
    private EntrySelectionList list;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    public final Identifier compassType;

    public CompassScreen(Identifier compassType, List<CompassTargetType> unlocked) {
        super(Component.translatable("gui.omnicompass.title"));
        this.unlocked = unlocked;
        this.compassType = compassType;
    }

    @Override
    protected void init() {
        this.addTitle();
        this.list = new EntrySelectionList(this.minecraft, this.width, this.height - 60, 30, 20, this.compassType);
        for (CompassTargetType entry : unlocked) {
            this.list.addEntry(entry);
        }
        this.addRenderableWidget(this.list);
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    protected void addTitle() {
        this.layout.addTitleHeader(this.title, this.font);
    }

    protected void addFooter() {
        EditBox searchBox = new EditBox(this.font, 0, 0, 200, 20, Component.literal("Search"));
        searchBox.setResponder(this::updateList);
        searchBox.setHint(Component.translatable("gui.omnicompass.search_hint"));
        this.layout.addToFooter(searchBox);
    }

    private void updateList(String filter) {
        this.list.clearEntries();
        String lowerFilter = filter.toLowerCase(Locale.ROOT);
        for (CompassTargetType entry : unlocked) {
            if (entry.getTargetName().getString().toLowerCase(Locale.ROOT).contains(lowerFilter)) {
                this.list.addEntry(entry);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
