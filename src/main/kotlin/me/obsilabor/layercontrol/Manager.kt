package me.obsilabor.layercontrol

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.obsilabor.layercontrol.config.ClothConfigManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen

fun init() {
    ClothConfigManager
    ClientTickEvents.END_CLIENT_TICK.register {
        if ((it.screen ?: return@register) is TitleScreen || (it.screen ?: return@register) is JoinMultiplayerScreen) {
            ClothConfigManager.clearConfigs()
        }
    }
}

val minecraft by lazy { Minecraft.getInstance() }

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    prettyPrint = true
    encodeDefaults = true
    prettyPrintIndent = "  "
}