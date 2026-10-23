package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementTab.class)
public interface AdvancementTabAccessor {
    @Accessor("rootWidget")
    AdvancementWidget refreshed_advancements$getRootWidget();

    @Accessor("originX")
    double refreshed_advancements$getOriginX();

    @Accessor("originY")
    double refreshed_advancements$getOriginY();

    @Accessor("originX")
    void refreshed_advancements$setOriginX(double originX);

    @Accessor("originY")
    void refreshed_advancements$setOriginY(double originY);

    @Accessor("initialized")
    boolean refreshed_advancements$isInitialized();

    @Accessor("initialized")
    void refreshed_advancements$setInitialized(boolean initialized);

    @Accessor("maxPanX")
    int refreshed_advancements$getMaxPanX();

    @Accessor("maxPanY")
    int refreshed_advancements$getMaxPanY();

    @Accessor("minPanX")
    int refreshed_advancements$getMinPanX();

    @Accessor("minPanY")
    int refreshed_advancements$getMinPanY();

    @Accessor("widgets")
    Map<Advancement, AdvancementWidget> refreshed_advancements$getWidgets();

    @Accessor("alpha")
    float refreshed_advancements$getAlpha();

    @Accessor("alpha")
    void refreshed_advancements$setAlpha(float alpha);

    @Accessor("display")
    AdvancementDisplay refreshed_advancements$getDisplay();
}
