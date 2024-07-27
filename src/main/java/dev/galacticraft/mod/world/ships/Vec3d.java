package dev.galacticraft.mod.world.ships;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;
import org.joml.Runtime;

import java.io.Externalizable;
import java.text.NumberFormat;

public class Vec3d implements Cloneable {
    public double x;
    public double y;
    public double z;

    public static final Vec3d ZERO = new Vec3d(0);

    public Vec3d()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3d(double d)
    {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    public Vec3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Vec3 vec)
    {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vec3d(BlockPos vec)
    {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }

    public Vec3d lerp(Vec3d other, float t) {
        return new Vec3d(
                Math.lerp(this.x, other.x, t),
                Math.lerp(this.y, other.y, t),
                Math.lerp(this.z, other.z, t)
        );
    }

    public Vec3 toVec3()
    {
        return new Vec3(this.x, this.y, this.z);
    }

    public Vec3d add(Vec3d other)
    {
        return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3d add(double x, double y, double z)
    {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d add(double d)
    {
        return new Vec3d(this.x + d, this.y + d, this.z + d);
    }

    public Vec3d subtract(Vec3d other)
    {
        return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3d subtract(double x, double y, double z)
    {
        return new Vec3d(this.x - x, this.y - y, this.z - z);
    }

    public Vec3d subtract(double d)
    {
        return new Vec3d(this.x - d, this.y - d, this.z - d);
    }

    public Vec3d multiply(Vec3d other)
    {
        return new Vec3d(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vec3d multiply(double x, double y, double z)
    {
        return new Vec3d(this.x * x, this.y * y, this.z * z);
    }

    public Vec3d multiply(double d)
    {
        return new Vec3d(this.x * d, this.y * d, this.z * d);
    }

    public Vec3d scale(float f)
    {
        return new Vec3d(this.x * f, this.y * f, this.z * f);
    }

    public Vec3d scale(double d)
    {
        return new Vec3d(this.x * d, this.y * d, this.z * d);
    }

    public double length()
    {
        return Math.sqrt(this.lengthSquared());
    }

    public double distanceTo(Vec3d other)
    {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx * dy * dy * dz * dz);
    }

    public Vec3d normalize() {
        double length = this.length();
        if (length == 0) {
            return new Vec3d(0, 0, 0);
        }
        return new Vec3d(this.x / length, this.y / length, this.z / length);
    }

    public Vec3d crossProduct(Vec3d other) {
        return new Vec3d(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public static Vec3d applyRotation(Vec3d vec, Quaternion rotation) {
        Quaternion pointQuat = new Quaternion((float) vec.x, (float) vec.y, (float) vec.z, 0);
        Quaternion rotatedQuat = rotation.copy().multiply(pointQuat).multiply(rotation.conjugate());
        return new Vec3d(rotatedQuat.x, rotatedQuat.y, rotatedQuat.z);
    }


    public double dot(Vec3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vec3d zero() {
        return new Vec3d();
    }

    public String toString() {
        return "(x: " + this.x + " y: " + this.y + " z: " + this.z + ")";
    }
    @Override
    public Vec3d clone() {
        try {
            return (Vec3d) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Vec3d fromQuaternion(Quaternion quaternion) {
        Vec3d euler = new Vec3d();

        double sinr_cosp = 2 * (quaternion.w * quaternion.x + quaternion.y * quaternion.z);
        double cosr_cosp = 1 - 2 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y);
        euler.x = Math.atan2(sinr_cosp, cosr_cosp);

        double sinp = 2 * (quaternion.w * quaternion.y - quaternion.z * quaternion.x);
        if (Math.abs(sinp) >= 1) {
            euler.y = copySign(Math.PI / 2, sinp);
        } else {
            euler.y = Math.asin(sinp);
        }

        double siny_cosp = 2 * (quaternion.w * quaternion.z + quaternion.x * quaternion.y);
        double cosy_cosp = 1 - 2 * (quaternion.y * quaternion.y + quaternion.z * quaternion.z);
        euler.z = Math.atan2(siny_cosp, cosy_cosp);

        return euler;
    }

    public Vec3d fromQuaternion(double w, double x, double y, double z) {
        Vec3d euler = new Vec3d();

        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        euler.x = Math.atan2(sinr_cosp, cosr_cosp);

        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1) {
            euler.y = copySign(Math.PI / 2, sinp);
        } else {
            euler.y = Math.asin(sinp);
        }

        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        euler.z = Math.atan2(siny_cosp, cosy_cosp);

        return euler;
    }



    public static double copySign(double magnitude, double sign) {
        return sign < 0 ? -Math.abs(magnitude) : Math.abs(magnitude);
    }


    public BlockPos toBlockPos() {
        return new BlockPos((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z));
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vec3d ceil() {
        return new Vec3d(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
    }

    public Vec3d cross(Vec3d vec) {
        return new Vec3d(
                this.y * vec.z - this.z * vec.y,
                this.z * vec.x - this.x * vec.z,
                this.x * vec.y - this.y * vec.x
        );
    }

    public Vec3d add(BlockPos pos) {
        return new Vec3d(this.x + pos.getX(), this.y + pos.getY(), this.z + pos.getZ());
    }
}
