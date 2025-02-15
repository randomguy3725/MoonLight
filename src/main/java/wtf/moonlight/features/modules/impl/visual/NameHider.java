package wtf.moonlight.features.modules.impl.visual;

import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.TextValue;


@ModuleInfo(name = "NameHider",category = ModuleCategory.Visual)
public class NameHider extends Module {
    public final TextValue name = new TextValue("Name","Randomguy",this);
    public String getFakeName(String s) {
        if (mc.thePlayer != null) {
            s = s.replace(mc.thePlayer.getName(), name.get());
        }
        return s;
    }
}
