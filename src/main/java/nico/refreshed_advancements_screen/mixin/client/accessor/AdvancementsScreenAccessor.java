package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("advancementHandler")
    ClientAdvancementManager refreshed_advancements$getAdvancementHandler();

    @Accessor("tabs")
    Map<Advancement, AdvancementTab> refreshed_advancements$getTabs();

    @Accessor("selectedTab")
    AdvancementTab refreshed_advancements$getSelectedTab();

    @Accessor("selectedTab")
    void refreshed_advancements$setSelectedTab(AdvancementTab tab);

    @Accessor("movingTab")
    boolean refreshed_advancements$isMovingTab();

    @Accessor("movingTab")
    void refreshed_advancements$setMovingTab(boolean bl);
}
