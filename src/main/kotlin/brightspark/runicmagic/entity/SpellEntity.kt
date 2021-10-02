package brightspark.runicmagic.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.world.World

class SpellEntity(entityTypeIn: EntityType<*>, worldIn: World) : Entity(entityTypeIn, worldIn) {
	override fun registerData() {
		TODO("Not yet implemented")
	}

	override fun readAdditional(compound: CompoundNBT) {
		TODO("Not yet implemented")
	}

	override fun writeAdditional(compound: CompoundNBT) {
		TODO("Not yet implemented")
	}

	override fun createSpawnPacket(): IPacket<*> {
		TODO("Not yet implemented")
	}
}
