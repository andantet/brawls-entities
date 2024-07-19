package net.mcbrawls.entities.entity

import eu.pb4.polymer.core.api.entity.PolymerEntity
import net.mcbrawls.entities.entity.TemporaryTextDisplayEntity.TextSupplier
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

open class TemporaryTextDisplayEntity(
    /**
     * Provides the text component for this text display.
     */
    private val textSupplier: TextSupplier,

    /**
     * The maximum age (duration) of this entity.
     */
    private val maxAge: Int = DEFAULT_MAX_AGE,

    type: EntityType<*>,
    world: World
) : TextDisplayEntity(type, world), PolymerEntity {
    constructor(type: EntityType<*>, world: World) : this(TextSupplier { entity, _ -> entity.displayName ?: Text.literal(entity.nameForScoreboard) }, DEFAULT_MAX_AGE, type, world)

    private var initialPosition: Vec3d? = null

    init {
        setBillboardMode(BillboardMode.CENTER)
    }

    override fun tick() {
        super.tick()

        if (age > maxAge) {
            discard()
        } else {
            val initialPosition: Vec3d = initialPosition ?: let {
                initialPosition = pos
                pos
            }

            val percentage = age / maxAge.toFloat()
            val yOffset = MAX_Y_OFFSET * percentage
            setPosition(initialPosition.add(0.0, yOffset, 0.0))

            val text = textSupplier.getText(this, age)
            setText(text)
        }
    }

    override fun getPolymerEntityType(player: ServerPlayerEntity): EntityType<*> {
        return EntityType.TEXT_DISPLAY
    }

    fun interface TextSupplier {
        fun getText(entity: TemporaryTextDisplayEntity, age: Int): Text
    }

    companion object {
        const val DEFAULT_MAX_AGE: Int = 20
        const val MAX_Y_OFFSET: Double = 0.25
    }
}
