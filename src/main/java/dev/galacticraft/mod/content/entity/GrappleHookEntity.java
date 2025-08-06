/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.entity;



/*
public class GrappleHookEntity extends Projectile {
    private Player player;
    private boolean attached = false;

    public GrappleHookEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public GrappleHookEntity(Level level, Player player) {
        super(GCEntityTypes.GRAPPLE_HOOK, level);
        this.setOwner(player);
        this.player = player;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        if (!this.attached) {
            super.tick();
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            Vec3 vec3 = this.getDeltaMovement();
            double d = this.getX() + vec3.x;
            double e = this.getY() + vec3.y;
            double f = this.getZ() + vec3.z;
            this.updateRotation();
            if (this.level().noCollision(this, this.getBoundingBox().move(vec3).inflate(1.0))) {
                this.setPos(d, e, f);
            } else {
                this.discard();
            }
        } else {
            if (this.player == null || this.player.isRemoved() || this.player.distanceToSqr(this) > 400) {
                this.discard();
                return;
            }
            Vec3 pullVector = this.position().subtract(this.player.position()).normalize().scale(0.8);
            this.player.setDeltaMovement(pullVector);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.attached = true;
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            this.discard();
        }
    }
}
*/
