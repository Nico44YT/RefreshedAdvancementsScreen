package nico.refreshed_advancements_screen.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;

public class RefreshedAdvancementsModMenuApiImpl implements ModMenuApi {
    public static final Function<Screen, Screen> CREATE = parent -> AutoConfigClient.getConfigScreen(RefreshedAdvancementsConfig.class, parent).get();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CREATE::apply;
    }
}
