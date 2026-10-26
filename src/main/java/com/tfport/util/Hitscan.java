package com.tfport.util;

import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Мгновенные выстрелы (хитскан) для дробовика, миниганa и луча медика. */
public final class Hitscan {
    private Hitscan() {}

    /** Находит живую цель по линии взгляда (не дальше range блоков). */
    @Nullable
    public static LivingEntity findLivingTarget(LivingEntity shooter, double range) {
        return trace(shooter, shooter.getLookAngle(), range);
    }

    @Nullable
    private static LivingEntity trace(LivingEntity shooter, Vec3 dir, double range) {
        Level level = shooter.level();
        Vec3 eye = shooter.getEyePosition();
        Vec3 end = eye.add(dir.scale(range));

        BlockHitResult block = level.clip(new ClipContext(
                eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        Vec3 realEnd = block.getType() != HitResult.Type.MISS ? block.getLocation() : end;

        AABB box = shooter.getBoundingBox().expandTowards(dir.scale(range)).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                shooter, eye, realEnd, box,
                e -> !e.isSpectator() && e.isPickable() && e instanceof LivingEntity,
                range * range);
        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    /**
     * Залп из нескольких пуль. Попадания по одной цели суммируются в один удар,
     * чтобы не упираться в кадры неуязвимости.
     */
    public static void volley(LivingEntity shooter, int pellets, double range, double spread, float damagePerPellet) {
        Level level = shooter.level();
        RandomSource random = shooter.getRandom();
        Map<LivingEntity, Integer> hits = new IdentityHashMap<>();

        for (int i = 0; i < pellets; i++) {
            Vec3 dir = shooter.getLookAngle().add(
                    (random.nextDouble() - 0.5) * 2.0 * spread,
                    (random.nextDouble() - 0.5) * 2.0 * spread,
                    (random.nextDouble() - 0.5) * 2.0 * spread).normalize();
            LivingEntity target = trace(shooter, dir, range);
            if (target != null) {
                hits.merge(target, 1, Integer::sum);
            }
        }

        DamageSource source = shooter instanceof Player p
                ? level.damageSources().playerAttack(p)
                : level.damageSources().mobAttack(shooter);

        hits.forEach((target, count) -> {
            target.invulnerableTime = 0;
            target.hurt(source, damagePerPellet * count);
        });
    }

    public static void playSound(LivingEntity source, SoundEvent sound, float volume, float pitch) {
        SoundSource category = source instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
        source.level().playSound(null, source.getX(), source.getY(), source.getZ(), sound, category, volume, pitch);
    }
}
