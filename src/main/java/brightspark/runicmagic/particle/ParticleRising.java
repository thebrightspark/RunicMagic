package brightspark.runicmagic.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleRising extends ParticleMoving
{
    public ParticleRising(World worldIn, Vec3d position, Color colour)
    {
        super(worldIn, position, colour, 0);
        motionY = (worldIn.rand.nextFloat() * 0.02F) + 0.04F;
        setMaxAge(80);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        motionX *= 0.98F;
        motionY *= 0.98F;
        motionZ *= 0.98F;
        setAlphaF(1F - ((float) particleAge / (float) particleMaxAge));
    }
}
