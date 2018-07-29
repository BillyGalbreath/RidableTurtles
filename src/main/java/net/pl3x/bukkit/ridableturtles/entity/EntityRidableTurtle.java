package net.pl3x.bukkit.ridableturtles.entity;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.EntityTurtle;
import net.minecraft.server.v1_13_R1.EnumMoveType;
import net.minecraft.server.v1_13_R1.MathHelper;
import net.minecraft.server.v1_13_R1.MobEffects;
import net.minecraft.server.v1_13_R1.World;
import net.pl3x.bukkit.ridableturtles.configuration.Config;

public class EntityRidableTurtle extends EntityTurtle {
    public EntityRidableTurtle(World world) {
        super(world);
        persistent = true; // we want persistence
    }

    @Override
    protected boolean isTypeNotPersistent() {
        return false; // we definitely want persistence
    }

    // travel(strafe, vertical, forward)
    @Override
    public void a(float f, float f1, float f2) {
        EntityPlayer rider = getRider();
        if (rider != null) {
            // rotation
            setYawPitch(lastYaw = yaw = rider.yaw, pitch = rider.pitch * 0.75F);
            aS = aQ = yaw;

            // controls
            float forward = rider.bj;
            float strafe = rider.bh;
            float vertical = forward == 0 ? 0 : -(rider.pitch / 90);

            // move
            customMove(strafe, vertical, forward);
            return;
        }
        super.a(f, f1, f2);
    }

    public EntityPlayer getRider() {
        if (passengers != null && !passengers.isEmpty()) {
            Entity entity = passengers.get(0); // only care about first rider
            if (entity instanceof EntityPlayer) {
                return (EntityPlayer) entity;
            }
        }
        return null; // aww, lonely turtle is lonely
    }

    private void customMove(float strafe, float vertical, float forward) {
        // in water
        if (isInWater()) {
            double oldY = locY;
            a(strafe, vertical, forward, 0.02F * Config.SPEED_WATER); // moveRelative
            move(EnumMoveType.PLAYER, motX, motY, motZ);
            motX *= isSprinting() ? 0.9F : cJ();
            motY *= 0.8D;
            motZ *= isSprinting() ? 0.9F : cJ();

            if (!isNoGravity() && !isSprinting()) {
                if (motY <= 0.0D && Math.abs(motY - 0.005D) >= 0.003D && Math.abs(motY - 0.08D / 16.0D) < 0.003D) {
                    motY = -0.003D;
                } else {
                    motY -= 0.08D / 16.0D;
                }
            }

            if (positionChanged && c(motX, motY + 0.6D - locY + oldY, motZ)) {
                motY = 0.3D;
            }
        }

        // in lava
        else if (ax()) {
            double oldY = locY;
            a(strafe, vertical, forward, 0.02F * Config.SPEED_WATER); // moveRelative
            move(EnumMoveType.PLAYER, motX, motY, motZ);
            motX *= 0.5D;
            motY *= 0.5D;
            motZ *= 0.5D;
            if (!isNoGravity()) {
                motY -= 0.08D / 4.0D;
            }
            if (positionChanged && c(motX, motY + 0.6D - locY + oldY, motZ)) {
                motY = 0.3D;
            }
        }

        // not in lava or water
        else {
            o((forward != 0 || strafe != 0 ? 0.01F : 0F));

            BlockPosition.b posUnder = BlockPosition.b.d(locX, getBoundingBox().b - 1.0D, locZ);
            Throwable throwable = null;

            try {
                float blockFriction = onGround ? world.getType(posUnder).getBlock().n() * 0.91F : 0.91F;
                float friction = onGround ? cK() * (0.16277137F / (blockFriction * blockFriction * blockFriction)) : aU;
                a(strafe, vertical, forward, friction * Config.SPEED_LAND); // moveRelative
                blockFriction = onGround ? world.getType(posUnder.e(locX, getBoundingBox().b - 1.0D, locZ)).getBlock().n() * 0.91F : 0.91F;
                if (z_()) { // isOnLadder
                    motX = MathHelper.a(motX, -0.15D, 0.15D);
                    motZ = MathHelper.a(motZ, -0.15D, 0.15D);
                    fallDistance = 0.0F;
                    if (motY < -0.15D) {
                        motY = -0.15D;
                    }
                }
                move(EnumMoveType.PLAYER, motX, motY, motZ);
                if (positionChanged && z_()) { // isOnLadder
                    motY = 0.2D;
                }

                if (hasEffect(MobEffects.LEVITATION)) {
                    motY += (0.05D * (double) (getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - motY) * 0.2D;
                    fallDistance = 0.0F;
                } else {
                    posUnder.e(locX, 0.0D, locZ);
                    if (!isNoGravity()) {
                        motY -= 0.08D;
                    }
                }

                motY *= 0.98D;
                motX *= (double) blockFriction;
                motZ *= (double) blockFriction;
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (posUnder != null) {
                    if (throwable != null) {
                        try {
                            posUnder.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        posUnder.close();
                    }
                }
            }
        }

        aI = aJ; // prevLimbSwingAmount = limbSwingAmount;
        double d0 = locX - lastX;
        double d2 = locZ - lastZ;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 4.0F;
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }
        aJ += (f3 - aJ) * 0.4F; // limbSwingAmount
        aK += aJ; // limbSwing += limbSwingAmount
    }
}
