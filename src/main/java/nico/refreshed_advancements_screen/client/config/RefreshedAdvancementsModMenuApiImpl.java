package nico.refreshed_advancements_screen.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class RefreshedAdvancementsModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RefreshedAdvancementsConfig.SCREEN_FACTORY::apply;
    }
}
