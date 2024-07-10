package net.mcbrawls.entities

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import net.mcbrawls.entities.entity.DisplayedBlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BrawlsAPIEntities {
    val DISPLAYED_BLOCK = register(
        "displayed_block",
        EntityType.Builder.create(::DisplayedBlockEntity, SpawnGroup.MISC)
            .dimensions(0.0f, 0.0f)
            .maxTrackingRange(2)
            .trackingTickInterval(10)
    )

    fun <E : Entity> register(id: String, builder: EntityType.Builder<E>): EntityType<E> {
        val identifier = Identifier.of("brawls", id)
        val type = builder.build(identifier.toString())
        PolymerEntityUtils.registerType(type)
        return Registry.register(Registries.ENTITY_TYPE, identifier, type)
    }
}
