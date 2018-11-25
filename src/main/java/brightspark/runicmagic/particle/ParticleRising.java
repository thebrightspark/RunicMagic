package brightspark.runicmagic.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleRising extends ParticleStatic
{
    public ParticleRising(World worldIn, Vec3d position, Color colour)
    {
        super(worldIn, position, colour, 0);
        motionY = 0.05F;
        setMaxAge(60);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        move(motionX, motionY, motionZ);
        motionX *= 0.95F;
        motionY *= 0.95F;
        motionZ *= 0.95F;
        setAlphaF(1F - ((float) particleAge / (float) particleMaxAge));
    }
}
