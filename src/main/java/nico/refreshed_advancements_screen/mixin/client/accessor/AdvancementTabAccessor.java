package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("root")
    AdvancementWidget refreshed_advancements$getRootWidget();

    @Accessor("scrollX")
    double refreshed_advancements$getOriginX();

    @Accessor("scrollY")
    double refreshed_advancements$getOriginY();

    @Accessor("scrollX")
    void refreshed_advancements$setOriginX(double originX);

    @Accessor("scrollY")
    void refreshed_advancements$setOriginY(double originY);

    @Accessor("centered")
    boolean refreshed_advancements$isInitialized();

    @Accessor("centered")
    void refreshed_advancements$setInitialized(boolean initialized);

    @Accessor("maxX")
    int refreshed_advancements$getMaxPanX();

    @Accessor("maxY")
    int refreshed_advancements$getMaxPanY();

    @Accessor("minX")
    int refreshed_advancements$getMinPanX();

    @Accessor("minY")
    int refreshed_advancements$getMinPanY();

    @Accessor("widgets")
    Map<Advancement, AdvancementWidget> refreshed_advancements$getWidgets();

    @Accessor("fade")
    float refreshed_advancements$getAlpha();

    @Accessor("fade")
    void refreshed_advancements$setAlpha(float alpha);

    @Accessor("icon")
    ItemStack refreshed_advancements$getIcon();
}
