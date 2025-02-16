package wtf.moonlight.features.modules.impl.misc;

import org.lwjgl.glfw.GLFW;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "RawMouseInput",category = ModuleCategory.Misc)
public class RawMouseInput extends Module {

    @Override
    public void onEnable(){
        if (Mouse.isCreated()) {
            if (GLFW.glfwRawMouseMotionSupported()) {
                GLFW.glfwSetInputMode(Display.getWindow(), GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_FALSE);
            }
        }
    }

    @Override
    public void onDisable(){
        if (Mouse.isCreated()) {
            if (GLFW.glfwRawMouseMotionSupported()) {
                GLFW.glfwSetInputMode(Display.getWindow(), GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
            }
        }
    }
}
