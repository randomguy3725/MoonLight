/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.features.values;

import lombok.Getter;
import wtf.moonlight.features.modules.Module;

import java.awt.*;
import java.util.function.BooleanSupplier;

@Getter
public abstract class Value {
    private final String name;
    @Getter
    public BooleanSupplier visible;
    public Color color = Color.WHITE;

    public Value(String name, Module module, BooleanSupplier visible) {
        this.name = name;
        this.visible = visible;
        if (module != null) {
            module.addValue(this);
        }
    }


    public Boolean canDisplay() {
        return this.visible.getAsBoolean();
    }
}