package nico.refreshed_advancements_screen.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import nico.refreshed_advancements_screen.client.config.RefreshedAdvancementsConfig;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;


//? >= 26.2 {
/*@Mixin(Gui.class)
*///?} else {
@Mixin(Minecraft.class)
//?}
public abstract class AdvancementScreenRedirectMixin {
    @WrapMethod(method = "setScreen")
    public void refreshed_advancements$wrapSetScreen(Screen screen, Operation<Void> original) {
        if (screen != null && screen.getClass() == AdvancementsScreen.class && RefreshedAdvancementsConfig.isEnabled()) {
            original.call(new RefreshedAdvancementsScreen(((AdvancementsScreenAccessor) screen).refreshed_advancements$getAdvancementHandler()));
            return;
        }

        original.call(screen);
    }
}