/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.player.StrafeEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Freeze;
import wtf.moonlight.features.modules.impl.movement.Speed;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.utils.player.MovementUtils;

@ModuleInfo(name = "Critical", category = ModuleCategory.Combat)
public class Critical extends Module {
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Jump", "AutoFreeze", "AutoSpeed"}, "Jump", this);
    private boolean attacking;
    public boolean stuckEnabled;

    @Override
    public void onEnable() {
        stuckEnabled = false;
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (mc.thePlayer.onGround && event.getTargetEntity() instanceof EntityLivingBase entity) {
            if(entity.hurtTime == 9)
                mc.thePlayer.onCriticalHit(entity);
            attacking = true;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        setTag(mode.get());
        switch (mode.get()) {
            case "AutoFreeze":
                if (getModule(KillAura.class).target != null && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                if (mc.thePlayer.fallDistance > 0) {
                    getModule(Freeze.class).setEnabled(true);
                    stuckEnabled = true;
                }
                if (getModule(KillAura.class).target == null && stuckEnabled) {
                    getModule(Freeze.class).setEnabled(false);
                    stuckEnabled = false;
                }
                break;
            case "AutoSpeed":
                if (getModule(KillAura.class).target != null) {
                    if (isDisabled(Speed.class)) {
                        getModule(Speed.class).setEnabled(true);
                    } else {
                        if (!MovementUtils.isMoving() && mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                        }
                    }
                } else {
                    if (isEnabled(Speed.class)) {
                        getModule(Speed.class).setEnabled(false);
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onStrafe(StrafeEvent event){
        if(mode.is("Jump") && attacking && mc.thePlayer.onGround){
            mc.thePlayer.jump();
            attacking = false;
        }
    }
}
