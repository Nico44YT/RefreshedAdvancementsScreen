package nico.refreshed_advancements_screen.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    private GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private final Supplier<Screen> SUPP = () -> new RefreshedAdvancementsScreen(
            client.player.networkHandler.getAdvancementHandler()
    );

    @Unique
    private static boolean vanillaMode = false;

    @WrapOperation(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;createButton(Lnet/minecraft/text/Text;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/widget/ButtonWidget;", ordinal = 0))
    public ButtonWidget change(GameMenuScreen instance, Text text, Supplier<Screen> screenSupplier, Operation<ButtonWidget> original) {
        return original.call(instance, text, vanillaMode ? screenSupplier : SUPP);
    }
}
