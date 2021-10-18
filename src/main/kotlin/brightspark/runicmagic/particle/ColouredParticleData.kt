package brightspark.runicmagic.particle

import brightspark.runicmagic.util.readColor
import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.PacketBuffer
import net.minecraft.particles.IParticleData
import net.minecraft.particles.ParticleType
import net.minecraftforge.registries.ForgeRegistries
import java.awt.Color

class ColouredParticleData(
	private val particleType: ParticleType<out IParticleData>,
	val colour1: Color,
	val colour2: Color? = null,
	val age: Int = 0,
	val fadeOut: Boolean = false
) : IParticleData {
	companion object {
		val DESERIALISER = object : IParticleData.IDeserializer<ColouredParticleData> {
			override fun deserialize(
				particleType: ParticleType<ColouredParticleData>,
				reader: StringReader
			): ColouredParticleData {
				reader.expect(' ')
				val color1 = reader.readColor()!!
				reader.expect(' ')
				val color2 = reader.readColor()
				reader.expect(' ')
				val age = reader.readInt()
				reader.expect(' ')
				val fadeOut = reader.readBoolean()
				return ColouredParticleData(particleType, color1, color2, age, fadeOut)
			}

			override fun read(
				particleType: ParticleType<ColouredParticleData>,
				buffer: PacketBuffer
			): ColouredParticleData = ColouredParticleData(
				particleType,
				buffer.readColor()!!,
				buffer.readColor(),
				buffer.readInt(),
				buffer.readBoolean()
			)
		}

		fun createCodec(particleType: ParticleType<ColouredParticleData>): Codec<ColouredParticleData> =
			RecordCodecBuilder.create { builder ->
				builder.group(
					Codec.INT.fieldOf("r1").forGetter { it.colour1.red },
					Codec.INT.fieldOf("g1").forGetter { it.colour1.green },
					Codec.INT.fieldOf("b1").forGetter { it.colour1.blue },
					Codec.INT.fieldOf("a1").forGetter { it.colour1.alpha },
					Codec.INT.fieldOf("r2").forGetter { it.colour2?.red ?: -1 },
					Codec.INT.fieldOf("g2").forGetter { it.colour2?.green ?: -1 },
					Codec.INT.fieldOf("b2").forGetter { it.colour2?.blue ?: -1 },
					Codec.INT.fieldOf("a2").forGetter { it.colour2?.alpha ?: -1 },
					Codec.INT.fieldOf("age").forGetter { it.age },
					Codec.BOOL.fieldOf("fadeOut").forGetter { it.fadeOut }
				).apply(builder) { r1, g1, b1, a1, r2, g2, b2, a2, age, fadeOut ->
					ColouredParticleData(particleType, Color(r1, g1, b1, a1), Color(r2, g2, b2, a2), age, fadeOut)
				}
			}
	}

	override fun getType(): ParticleType<*> = particleType

	override fun write(buffer: PacketBuffer) {
		buffer.writeInt(colour1.red).writeInt(colour1.green).writeInt(colour1.blue).writeInt(colour1.alpha)
			.writeInt(colour2?.red ?: -1).writeInt(colour2?.green ?: -1).writeInt(colour2?.blue ?: -1)
			.writeInt(colour2?.alpha ?: -1)
			.writeInt(age).writeBoolean(fadeOut)
	}

	override fun getParameters(): String =
		"${ForgeRegistries.PARTICLE_TYPES.getKey(type)} ${colour1.red} ${colour1.green} ${colour1.blue} ${colour1.alpha} ${colour2?.red ?: -1} ${colour2?.green ?: -1} ${colour2?.blue ?: -1} ${colour2?.alpha ?: -1} $age $fadeOut"
}
