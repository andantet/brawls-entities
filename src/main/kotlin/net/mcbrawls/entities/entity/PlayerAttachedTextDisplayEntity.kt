package net.mcbrawls.entities.entity

import dev.andante.audience.player.StandalonePlayerReference
import eu.pb4.polymer.core.api.entity.PolymerEntity
import net.mcbrawls.entities.entity.PlayerAttachedTextDisplayEntity.TextSupplier
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

open class PlayerAttachedTextDisplayEntity(
    /**
     * The player to be attached to.
     */
    private val player: StandalonePlayerReference?,

    /**
     * Provides the text component for this text display.
     */
    private val textSupplier: TextSupplier,

    /**
     * The offset from the top of the player's hitbox to display the entity.
     */
    private val positionOffset: Vec3d = DEFAULT_POSITION_OFFSET,

    type: EntityType<*>,
    world: World
) : TextDisplayEntity(type, world), PolymerEntity {
    open val serverPlayer: ServerPlayerEntity? get() = player?.player

    constructor(type: EntityType<*>, world: World) : this(null, TextSupplier { player, _ -> Text.literal(player.playerName) }, DEFAULT_POSITION_OFFSET, type, world)

    init {
        setBillboardMode(BillboardMode.CENTER)
    }

    override fun tick() {
        super.tick()

        val player = serverPlayer
        if (player == null) {
            discard()
        } else {
            val position = player.pos.add(0.0, player.height.toDouble(), 0.0).add(positionOffset)
            setPosition(position)

            val text = textSupplier.getText(player, age)
            setText(text)
        }
    }

    override fun getPolymerEntityType(player: ServerPlayerEntity): EntityType<*> {
        return EntityType.TEXT_DISPLAY
    }

    fun interface TextSupplier {
        fun getText(player: ServerPlayerEntity, age: Int): Text
    }

    companion object {
        val DEFAULT_POSITION_OFFSET = Vec3d(0.0, 0.5, 0.0)
    }
}
