package net.mcbrawls.entities.entity

import dev.andante.audience.player.StandalonePlayerReference
import eu.pb4.polymer.core.api.entity.PolymerEntity
import net.mcbrawls.entities.entity.PlayerAttachedTextDisplayEntity.TextSupplier
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.AffineTransformation
import net.minecraft.world.World
import org.joml.Vector3f

open class PlayerAttachedTextDisplayEntity(
    /**
     * The player to be attached to.
     */
    private var player: StandalonePlayerReference?,

    /**
     * Provides the text component for this text display.
     */
    private var textSupplier: TextSupplier,

    /**
     * The offset from the top of the player's hitbox to display the entity.
     */
    private var positionOffset: Vector3f = DEFAULT_POSITION_OFFSET,

    type: EntityType<*>,
    world: World
) : TextDisplayEntity(type, world), PolymerEntity {
    open val serverPlayer: ServerPlayerEntity? get() = player?.player

    constructor(type: EntityType<*>, world: World) : this(null, TextSupplier { player, _ -> Text.literal(player.playerName) }, DEFAULT_POSITION_OFFSET, type, world)

    init {
        setBillboardMode(BillboardMode.CENTER)

        setTransformation(
            AffineTransformation(positionOffset, null, null, null)
        )
    }

    override fun tick() {
        super.tick()

        if (isRemoved) {
            return
        }

        val player = serverPlayer
        if (player == null) {
            discard()
        } else {
            // verify same world
            if (player.world != world) {
                teleport(player.serverWorld, player.x, player.y, player.z, emptySet(), 0.0f, 0.0f)
                return
            }

            // verify riding
            if (vehicle != player) {
                startRiding(player, true)
            }

            try {
                val text = textSupplier.getText(player, age)
                setText(text)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    override fun copyFrom(original: Entity) {
        super.copyFrom(original)

        if (original is PlayerAttachedTextDisplayEntity) {
            player = original.player
            textSupplier = original.textSupplier
            positionOffset = original.positionOffset
        }
    }

    override fun getPolymerEntityType(viewer: ServerPlayerEntity): EntityType<*> {
        return EntityType.TEXT_DISPLAY
    }

    fun interface TextSupplier {
        fun getText(player: ServerPlayerEntity, age: Int): Text
    }

    companion object {
        val DEFAULT_POSITION_OFFSET = Vector3f(0.0f, 0.0f, 0.0f)
    }
}
