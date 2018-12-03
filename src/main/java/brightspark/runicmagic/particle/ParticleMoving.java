package brightspark.runicmagic.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ParticleMoving extends ParticleStatic
{
    private float[] colour2 = null;

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

    public ParticleMoving setSecondColour(Color colour2)
    {
        this.colour2 = colour2.getRGBColorComponents(null);
        return this;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        move(motionX, motionY, motionZ);

        if(colour2 != null)
        {
            //Change the colour towards colour2
            int ageLeft = particleMaxAge - particleAge;
            float red = (getRedColorF() - colour2[0]) / ageLeft;
            float green = (getGreenColorF() - colour2[1]) / ageLeft;
            float blue = (getBlueColorF() - colour2[2]) / ageLeft;
            setRBGColorF(getRedColorF() + red, getGreenColorF() + green, getBlueColorF() + blue);
        }
    }
}
