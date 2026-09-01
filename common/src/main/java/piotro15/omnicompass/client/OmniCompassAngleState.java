package piotro15.omnicompass.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import piotro15.omnicompass.common.registry.ModDataComponents;

public class OmniCompassAngleState extends NeedleDirectionHelper {
    public static final MapCodec<OmniCompassAngleState> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(OmniCompassAngleState::wobble)).apply(i, OmniCompassAngleState::new));
    private final NeedleDirectionHelper.Wobbler wobbler = this.newWobbler(0.8F);
    private final NeedleDirectionHelper.Wobbler noTargetWobbler = this.newWobbler(0.8F);
    private final RandomSource random = RandomSource.create();

    public OmniCompassAngleState(boolean wobble) {
        super(wobble);
    }

    protected float calculate(ItemStack itemStack, ClientLevel level, int seed, ItemOwner owner) {
        GlobalPos compassTargetPos = itemStack.get(ModDataComponents.TARGET_POSITION.get());
        long gameTime = level.getGameTime();
        return !isValidCompassTargetPos(owner, compassTargetPos) ? this.getRandomlySpinningRotation(seed, gameTime) : this.getRotationTowardsCompassTarget(owner, gameTime, compassTargetPos.pos());
    }

    private float getRandomlySpinningRotation(int seed, long gameTime) {
        if (this.noTargetWobbler.shouldUpdate(gameTime)) {
            this.noTargetWobbler.update(gameTime, this.random.nextFloat());
        }

        float targetRotation = this.noTargetWobbler.rotation() + (float)hash(seed) / (float)Integer.MAX_VALUE;
        return Mth.positiveModulo(targetRotation, 1.0F);
    }

    private float getRotationTowardsCompassTarget(ItemOwner owner, long gameTime, BlockPos compassTargetPos) {
        float angleToTarget = (float)getAngleFromEntityToPos(owner, compassTargetPos);
        float ownerYRotation = getWrappedVisualRotationY(owner);
        LivingEntity var9 = owner.asLivingEntity();
        float targetRotation;
        if (var9 instanceof Player player) {
            if (player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
                if (this.wobbler.shouldUpdate(gameTime)) {
                    this.wobbler.update(gameTime, 0.5F - (ownerYRotation - 0.25F));
                }

                targetRotation = angleToTarget + this.wobbler.rotation();
                return Mth.positiveModulo(targetRotation, 1.0F);
            }
        }

        targetRotation = 0.5F - (ownerYRotation - 0.25F - angleToTarget);
        return Mth.positiveModulo(targetRotation, 1.0F);
    }

    private static boolean isValidCompassTargetPos(ItemOwner owner, @Nullable GlobalPos positionToPointTo) {
        return positionToPointTo != null && positionToPointTo.dimension() == owner.level().dimension() && !(positionToPointTo.pos().distToCenterSqr(owner.position()) < (double)1.0E-5F);
    }

    private static double getAngleFromEntityToPos(ItemOwner owner, BlockPos position) {
        Vec3 target = Vec3.atCenterOf(position);
        Vec3 ownerPosition = owner.position();
        return Math.atan2(target.z() - ownerPosition.z(), target.x() - ownerPosition.x()) / (double)((float)Math.PI * 2F);
    }

    private static float getWrappedVisualRotationY(ItemOwner owner) {
        return Mth.positiveModulo(owner.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int input) {
        return input * 1327217883;
    }
}