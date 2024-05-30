package me.obsilabor.layercontrol.config

import kotlinx.serialization.encodeToString
import me.obsilabor.layercontrol.json
import me.obsilabor.layercontrol.minecraft
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.animal.Parrot
import java.io.File
import java.util.*

object ClothConfigManager {

    private fun saveConfigToFile() {
        configFile.writeText(json.encodeToString(config))
    }

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(minecraft.screen)
            .setTitle(Component.translatable("layercontrol.config"))
            .setSavingRunnable {
                saveConfigToFile()
            }
        val general = builder.getOrCreateCategory(Component.translatable("layercontrol.category.general"))
        val mojangLayers = builder.getOrCreateCategory(Component.nullToEmpty("Mojang Layers"))
        val customLayers = builder.getOrCreateCategory(Component.nullToEmpty("Custom Layers"))
        val entryBuilder = builder.entryBuilder()
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("layercontrol.option.renderOnOthers"), config?.renderOnOthers ?: true)
            .setSaveConsumer {
                config?.renderOnOthers = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.renderOnOthers)
            .build())
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("layercontrol.option.parrotVariantLeft"), config?.parrotVariantLeft ?: 0, 0, Parrot.Variant.entries.size - 1)
            .setSaveConsumer {
                config?.parrotVariantLeft = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.parrotVariantLeft)
            .build())
        general.addEntry(entryBuilder.startFloatField(Component.translatable("layercontrol.option.parrotScale"), config?.parrotScale ?: 0.5f)
            .setSaveConsumer {
                config?.parrotScale = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.parrotScale ?: 0.5f)
            .build())
        general.addEntry(entryBuilder.startIntSlider(Component.translatable("layercontrol.option.parrotVariantRight"), config?.parrotVariantRight ?: 0, 0, Parrot.Variant.entries.size - 1)
            .setSaveConsumer {
                config?.parrotVariantRight = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.parrotVariantRight)
            .build())
        general.addEntry(entryBuilder.startFloatField(Component.translatable("layercontrol.option.bigHeadSize"), config?.bigHeadScale ?: 2f)
            .setSaveConsumer {
                when {
                    it > 100f -> config?.bigHeadScale = 100f
                    it < 1f -> config?.bigHeadScale = 1f
                    else -> config?.bigHeadScale = it
                }
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.bigHeadScale ?: 2f)
            .build())
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("layercontrol.option.renderMyConfigOnOthers"), config?.renderMyConfigOnOthers ?: true)
            .setSaveConsumer {
                config?.renderMyConfigOnOthers = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.renderMyConfigOnOthers!!)
            .build())
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("layercontrol.option.shareWithOthers"), config?.shareWithOthers ?: true)
            .setSaveConsumer {
                config?.shareWithOthers = it
            }
            .setDefaultValue(LayerControlConfig.DEFAULT.shareWithOthers!!)
            .build())
        CustomizableRenderLayer.values().forEach { layer ->
            (if(layer.mojang) mojangLayers else customLayers).addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty(layer.name), config?.enabledLayers?.get(layer) ?: layer.mojang)
                .setSaveConsumer {
                    config?.enabledLayers?.set(layer, it)
                }
                .setDefaultValue(layer.mojang)
                .build())
        }
        return builder.build()
    }

    private val configFile = File(System.getProperty("user.dir") + "/config", "layercontrol.json")
    var config: LayerControlConfig? = null

    init {
        if(!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        if(!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText(json.encodeToString(LayerControlConfig.DEFAULT))
        }
        runCatching {
            config = json.decodeFromString(configFile.readText())
        }
    }

    var CONFIGS = mutableSetOf<SomehowMapsDontWork>()

    fun getConfigFor(uuid: UUID): LayerControlConfig {
        if (uuid == minecraft.player?.uuid) {
            return this.config ?: throw RuntimeException("Config is null")
        }

        val cfg = CONFIGS.firstOrNull { it.uuid == uuid }
        return cfg?.config
            ?: if (this.config?.renderMyConfigOnOthers == true) {
                this.config ?: throw RuntimeException("Config is null")
            } else {
                LayerControlConfig.DEFAULT
            }
    }

    fun clearConfigs() {
        CONFIGS.clear()
    }
}