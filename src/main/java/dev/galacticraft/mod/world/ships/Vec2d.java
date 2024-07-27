package dev.galacticraft.mod.world.ships;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Vec2d {
    public double x;
    public double y;

    public static final Vec2d ZERO = new Vec2d(0);

    public Vec2d()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vec2d(double d)
    {
        this.x = d;
        this.y = d;
    }

    public Vec2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2 vec)
    {
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vec2 toVec2()
    {
        return new Vec2((float) this.x, (float) this.y);
    }
}
