package net.mcbrawls.entities

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MCBrawlsEntities : ModInitializer {
    private val logger = LoggerFactory.getLogger("brawls-entities")

	override fun onInitialize() {
		BrawlsAPIEntities
	}
}
