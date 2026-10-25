package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("advancements")
    ClientAdvancements refreshed_advancements$getAdvancementHandler();

    @Accessor("tabs")
    Map<Advancement, AdvancementTab> refreshed_advancements$getTabs();

    @Accessor("selectedTab")
    AdvancementTab refreshed_advancements$getSelectedTab();

    @Accessor("selectedTab")
    void refreshed_advancements$setSelectedTab(AdvancementTab tab);

    @Accessor("isScrolling")
    boolean refreshed_advancements$isMovingTab();

    @Accessor("isScrolling")
    void refreshed_advancements$setMovingTab(boolean bl);
}