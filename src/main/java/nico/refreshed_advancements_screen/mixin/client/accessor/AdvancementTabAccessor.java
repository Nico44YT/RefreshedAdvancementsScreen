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
    @Accessor("field_2696")
    AdvancementWidget refreshed_advancements$getRootWidget();

    @Accessor("field_2690")
    double refreshed_advancements$getOriginX();

    @Accessor("field_2689")
    double refreshed_advancements$getOriginY();

    @Accessor("field_2690")
    void refreshed_advancements$setOriginX(double originX);

    @Accessor("field_2689")
    void refreshed_advancements$setOriginY(double originY);

    @Accessor("field_2683")
    boolean refreshed_advancements$isInitialized();

    @Accessor("field_2683")
    void refreshed_advancements$setInitialized(boolean initialized);

    @Accessor("field_2692")
    int refreshed_advancements$getMaxPanX();

    @Accessor("field_2691")
    int refreshed_advancements$getMaxPanY();

    @Accessor("field_2694")
    int refreshed_advancements$getMinPanX();

    @Accessor("field_2693")
    int refreshed_advancements$getMinPanY();

    @Accessor("widgets")
    Map<Advancement, AdvancementWidget> refreshed_advancements$getWidgets();

    @Accessor("field_2688")
    float refreshed_advancements$getAlpha();

    @Accessor("field_2688")
    void refreshed_advancements$setAlpha(float alpha);

    @Accessor("field_2695")
    AdvancementDisplay refreshed_advancements$getDisplay();
}
