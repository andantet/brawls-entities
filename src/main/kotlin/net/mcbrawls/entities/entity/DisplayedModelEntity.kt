package net.mcbrawls.entities.entity

import eu.pb4.polymer.core.api.entity.PolymerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier
import net.minecraft.util.math.AffineTransformation
import net.minecraft.world.World
import org.joml.Vector3f
import xyz.nucleoid.packettweaker.PacketContext

open class DisplayedModelEntity(type: EntityType<*>, world: World) : ItemDisplayEntity(type, world), PolymerEntity {
    var model: Identifier? = null
        set(value) {
            field = value
            updateItem()
        }

    var scale: Float = 1.0f
        set(value) {
            field = value
            updateTransformation()
        }

    init {
        updateItem()
        updateTransformation()
    }

    private fun updateItem() {
        setItemStack(
            ItemStack(Items.WHITE_DYE).apply {
                model?.also { set(DataComponentTypes.ITEM_MODEL, it) }
            }
        )
    }

    private fun updateTransformation() {
        setTransformation(
            AffineTransformation(null, null, Vector3f(scale, scale, scale), null)
        )
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        model?.also { nbt.putString(MODEL_KEY, it.toString()) }
        nbt.putFloat(SCALE_KEY, scale)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        if (nbt.contains(MODEL_KEY, NbtElement.STRING_TYPE.toInt())) {
            model = Identifier.tryParse(nbt.getString(MODEL_KEY))
        }

        if (nbt.contains(SCALE_KEY, NbtElement.FLOAT_TYPE.toInt())) {
            scale = nbt.getFloat(SCALE_KEY)
        }
    }

    override fun getPolymerEntityType(context: PacketContext): EntityType<*> {
        return EntityType.ITEM_DISPLAY
    }

    companion object {
        private const val MODEL_KEY = "model"
        private const val SCALE_KEY = "scale"
    }
}
