package dev.galacticraft.mod.world.ships;

import org.joml.Options;
import org.joml.Quaternionf;
import org.joml.Runtime;

import java.text.NumberFormat;

public class Quaternion {
    public float w, x, y, z;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(double x, double y, double z, double w) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.w = (float) w;
    }

    public Quaternion multiply(double scalar) {
        return new Quaternion(
                this.x * scalar,
                this.y * scalar,
                this.z * scalar,
                this.w * scalar
        );
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
                this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y,
                this.w * other.y - this.x * other.z + this.y * other.w + this.z * other.x,
                this.w * other.z + this.x * other.y - this.y * other.x + this.z * other.w,
                this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z
        );
    }

    public Quaternion normalize() {
        float length = (float) Math.sqrt(w * w + x * x + y * y + z * z);
        return new Quaternion(x / length, y / length, w / length, z / length);
    }

    public Quaternion add(Quaternion other) {
        return new Quaternion(
                this.x + other.x,
                this.y + other.y,
                this.z + other.z,
                this.w + other.w
        );
    }

    public Quaternion rotate(Vec3d vec) {
        return this.multiply(Quaternion.fromEuler(vec));
    }

    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat formatter) {
        return "(" + Runtime.format((double)this.x, formatter) + " " + Runtime.format((double)this.y, formatter) + " " + Runtime.format((double)this.z, formatter) + " " + Runtime.format((double)this.w, formatter) + ")";
    }

    public static Quaternion fromEuler(float x, float y, float z) {
        float halfRoll = (float) Math.toRadians(x) / 2;
        float halfPitch = (float) Math.toRadians(y) / 2;
        float halfYaw = (float) Math.toRadians(z) / 2;

        float sinRoll = (float) Math.sin(halfRoll);
        float cosRoll = (float) Math.cos(halfRoll);
        float sinPitch = (float) Math.sin(halfPitch);
        float cosPitch = (float) Math.cos(halfPitch);
        float sinYaw = (float) Math.sin(halfYaw);
        float cosYaw = (float) Math.cos(halfYaw);

        float w = cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw;
        float xNew = sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw;
        float yNew = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
        float zNew = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;

        return new Quaternion(xNew, yNew, zNew, w).normalize();
    }
    public static Quaternion fromEuler(Vec3d vec) {
        float halfRoll = (float) Math.toRadians(vec.x) / 2;
        float halfPitch = (float) Math.toRadians(vec.y) / 2;
        float halfYaw = (float) Math.toRadians(vec.z) / 2;

        float sinRoll = (float) Math.sin(halfRoll);
        float cosRoll = (float) Math.cos(halfRoll);
        float sinPitch = (float) Math.sin(halfPitch);
        float cosPitch = (float) Math.cos(halfPitch);
        float sinYaw = (float) Math.sin(halfYaw);
        float cosYaw = (float) Math.cos(halfYaw);

        float w = cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw;
        float x = sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw;
        float y = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
        float z = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;

        return new Quaternion(x, y, z, w).normalize();
    }

    public Quaternionf f() {
        return new Quaternionf(this.x, this.y, this.z, this.w);
    }

    public Quaternion copy() {
        return new Quaternion(this.x, this.y, this.z, this.w);
    }

    public Quaternion conjugate() {
        return new Quaternion(-this.x, -this.y, -this.z, this.w);
    }
}
