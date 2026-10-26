package com.tfport.entity;

import com.tfport.tfclass.TFClass;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Враждебный бот со случайным классом.
 * Скаут и тяжёлый дерутся в ближнем бою, солдат стреляет ракетами, медик лечит других ботов.
 */
public class TFBotEntity extends Monster {
    private static final EntityDataAccessor<Integer> DATA_CLASS =
            SynchedEntityData.defineId(TFBotEntity.class, EntityDataSerializers.INT);

    public TFBotEntity(EntityType<? extends TFBotEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLASS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RocketGoal(this));
        this.goalSelector.addGoal(2, new ClassMeleeGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public TFClass getTfClass() {
        return TFClass.byOrdinal(this.entityData.get(DATA_CLASS));
    }

    public void setTfClass(TFClass tfClass) {
        this.entityData.set(DATA_CLASS, tfClass.ordinal());
    }

    private void applyClassStats(TFClass c) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0 + c.bonusHealth);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25 * (1.0 + c.speedDelta));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(c.botMeleeDamage);
        this.setHealth(this.getMaxHealth());
        this.setItemSlot(EquipmentSlot.MAINHAND, c.primaryWeapon());
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        TFClass c = TFClass.byOrdinal(this.random.nextInt(TFClass.values().length));
        this.setTfClass(c);
        this.applyClassStats(c);
        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TFClass", this.entityData.get(DATA_CLASS));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_CLASS, tag.getInt("TFClass"));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Медик лечит союзных ботов раз в секунду.
        if (this.level() instanceof ServerLevel server
                && this.isAlive()
                && this.getTfClass() == TFClass.MEDIC
                && this.tickCount % 20 == 0) {
            for (TFBotEntity ally : server.getEntitiesOfClass(TFBotEntity.class,
                    this.getBoundingBox().inflate(8.0), a -> a != this && a.isAlive())) {
                ally.heal(2.0F);
                server.sendParticles(ParticleTypes.HEART, ally.getX(), ally.getEyeY() + 0.3, ally.getZ(),
                        1, 0.2, 0.1, 0.2, 0.0);
            }
            this.heal(0.5F);
        }
    }

    /** Ближний бой для всех, кроме солдата. */
    private static class ClassMeleeGoal extends MeleeAttackGoal {
        private final TFBotEntity bot;

        ClassMeleeGoal(TFBotEntity bot) {
            super(bot, 1.1, false);
            this.bot = bot;
        }

        @Override
        public boolean canUse() {
            return bot.getTfClass() != TFClass.SOLDIER && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return bot.getTfClass() != TFClass.SOLDIER && super.canContinueToUse();
        }
    }

    /** Солдат держит дистанцию и стреляет ракетами. */
    private static class RocketGoal extends Goal {
        private final TFBotEntity bot;
        private int cooldown;

        RocketGoal(TFBotEntity bot) {
            this.bot = bot;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = bot.getTarget();
            return bot.getTfClass() == TFClass.SOLDIER && target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            cooldown = 20;
        }

        @Override
        public void stop() {
            bot.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = bot.getTarget();
            if (target == null) {
                return;
            }
            bot.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distSqr = bot.distanceToSqr(target);
            boolean canSee = bot.getSensing().hasLineOfSight(target);

            if (distSqr > 14.0 * 14.0 || !canSee) {
                bot.getNavigation().moveTo(target, 1.0);
            } else {
                bot.getNavigation().stop();
            }

            if (--cooldown <= 0 && canSee && distSqr < 28.0 * 28.0) {
                RocketEntity rocket = new RocketEntity(bot.level(), bot);
                double dx = target.getX() - rocket.getX();
                double dy = target.getY(0.5) - rocket.getY();
                double dz = target.getZ() - rocket.getZ();
                rocket.shoot(dx, dy, dz, 1.3F, 4.0F);
                bot.level().addFreshEntity(rocket);
                bot.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F, 0.9F);
                cooldown = 50;
            }
        }
    }
}
