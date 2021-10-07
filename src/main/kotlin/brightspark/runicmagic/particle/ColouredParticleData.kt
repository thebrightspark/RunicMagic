package brightspark.runicmagic.particle

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
	val colour: Color
) : IParticleData {
	companion object {
		val DESERIALISER = object : IParticleData.IDeserializer<ColouredParticleData> {
			override fun deserialize(
				particleType: ParticleType<ColouredParticleData>,
				reader: StringReader
			): ColouredParticleData {
				reader.expect(' ')
				val r = reader.readInt()
				reader.expect(' ')
				val g = reader.readInt()
				reader.expect(' ')
				val b = reader.readInt()
				reader.expect(' ')
				val a = reader.readInt()
				return ColouredParticleData(particleType, Color(r, g, b, a))
			}

			override fun read(
				particleType: ParticleType<ColouredParticleData>,
				buffer: PacketBuffer
			): ColouredParticleData = ColouredParticleData(
				particleType,
				Color(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt())
			)
		}

		fun createCodec(particleType: ParticleType<ColouredParticleData>): Codec<ColouredParticleData> =
			RecordCodecBuilder.create { builder ->
				builder.group(
					Codec.INT.fieldOf("r").forGetter { it.colour.red },
					Codec.INT.fieldOf("g").forGetter { it.colour.green },
					Codec.INT.fieldOf("b").forGetter { it.colour.blue },
					Codec.INT.fieldOf("a").forGetter { it.colour.alpha }
				).apply(builder) { r, g, b, a -> ColouredParticleData(particleType, Color(r, g, b, a)) }
			}
	}

	override fun getType(): ParticleType<*> = particleType

	override fun write(buffer: PacketBuffer) {
		buffer.writeInt(colour.red).writeInt(colour.green).writeInt(colour.blue).writeInt(colour.alpha)
	}

	override fun getParameters(): String =
		"${ForgeRegistries.PARTICLE_TYPES.getKey(type)} ${colour.red} ${colour.green} ${colour.blue} ${colour.alpha}"
}
