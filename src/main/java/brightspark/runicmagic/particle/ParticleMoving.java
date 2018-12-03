package brightspark.runicmagic.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleMoving extends ParticleStatic
{
    public ParticleMoving(World worldIn, Vec3d position, Color colour, int particleIconIndex)
    {
        super(worldIn, position, colour, particleIconIndex);
    }

    public ParticleMoving(World worldIn, Vec3d position, Color colour, TextureAtlasSprite texture)
    {
        super(worldIn, position, colour, texture);
    }

    public ParticleMoving setMotion(Vec3d motion)
    {
        return setMotion(motion.x, motion.y, motion.z);
    }

    public ParticleMoving setMotion(double motionX, double motionY, double motionZ)
    {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        return this;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        move(motionX, motionY, motionZ);
    }
}
