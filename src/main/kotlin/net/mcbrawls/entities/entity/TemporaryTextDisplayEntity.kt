package net.mcbrawls.entities.entity

import net.mcbrawls.entities.entity.TemporaryTextDisplayEntity.TextSupplier
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity
import net.minecraft.text.Text
import net.minecraft.world.World

class TemporaryTextDisplayEntity(
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
) : TextDisplayEntity(type, world) {
    constructor(
        /**
         * The text to display on this entity.
         */
        text: Text,

        maxAge: Int = DEFAULT_MAX_AGE,

        type: EntityType<*>,
        world: World
    ) : this(TextSupplier { text }, maxAge, type, world)

    constructor(type: EntityType<*>, world: World) : this(Text.literal("Temp"), DEFAULT_MAX_AGE, type, world)

    override fun tick() {
        super.tick()

        if (age > maxAge) {
            discard()
        } else {
            val text = textSupplier.getText(age)
            setText(text)
        }
    }

    fun interface TextSupplier {
        fun getText(age: Int): Text
    }

    companion object {
        const val DEFAULT_MAX_AGE: Int = 20
    }
}
