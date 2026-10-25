package nico.refreshed_advancements_screen.mixin.client.accessor;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor("renderables")
    List<Renderable> refreshed_advancements$getDrawables();
}
