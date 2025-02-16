package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.util.Vec3;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.events.impl.render.Render3DEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayDeque;

@ModuleInfo(name = "Breadcrumbs", category = ModuleCategory.Visual)
public final class Breadcrumbs extends Module {

    private final ArrayDeque<Vec3> path = new ArrayDeque<>();

    private final BoolValue timeoutBool = new BoolValue("Timeout", true, this);
    private final SliderValue timeout = new SliderValue("Time", 15, 1, 150, 0.1f, this);

    @Override
    public void onEnable() {
        path.clear();
    }

    @EventTarget
    public void onPreMotion(MotionEvent e) {
        if (e.isPre()) {
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }

            if (timeoutBool.get()) {
                while (path.size() > (int) timeout.get()) {
                    path.removeFirst();
                }
            }
        }
    }

    @EventTarget
    public void onRender3DEvent(Render3DEvent e) {
        RenderUtils.renderBreadCrumbs(path);
    }
}