package nico.refreshed_advancements_screen.mixin.client;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import nico.refreshed_advancements_screen.client.screen.RefreshedAdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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

    @ModifyArg(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 1), index = 5)
    public ButtonWidget.PressAction redirectAdvancementsScreen(ButtonWidget.PressAction onPress) {
        if (!vanillaMode) return (widget) -> {
            if(client != null) client.openScreen(SUPP.get());
        };
        return onPress;
    }

}
