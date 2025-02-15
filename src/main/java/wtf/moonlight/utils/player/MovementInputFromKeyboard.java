package wtf.moonlight.utils.player;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import org.lwjgl.glfw.GLFW;
import org.lwjglx.input.KeyCodes;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.impl.player.MoveInputEvent;
import wtf.moonlight.utils.InstanceAccess;

public class MovementInputFromKeyboard extends MovementInput implements InstanceAccess {

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            ++this.moveForward;
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            --this.moveForward;
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            ++this.moveStrafe;
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            --this.moveStrafe;
        }

        this.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        this.sneak = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}

