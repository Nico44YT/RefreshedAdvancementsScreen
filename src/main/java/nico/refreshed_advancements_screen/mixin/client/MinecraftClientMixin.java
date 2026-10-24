package nico.refreshed_advancements_screen.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import nico.refreshed_advancements_screen.mixin.client.accessor.AdvancementsScreenAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @WrapMethod(method = "setScreen")
    public void refreshed_advancements$wrapSetScreen(Screen screen, Operation<Void> original) {
        if (screen != null && screen.getClass() == AdvancementsScreen.class) {
            original.call(new RefreshedAdvancementsScreen(((AdvancementsScreenAccessor) screen).refreshed_advancements$getAdvancementHandler()));
            return;
        }

        original.call(screen);
    }
}
