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
package wtf.moonlight.features.values.impl;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.values.Value;

import java.util.function.BooleanSupplier;

@Getter
@Setter
public class TextValue extends Value {
    private String text;
    private boolean onlyNumber;

    public TextValue(String name, String text, Module module, BooleanSupplier visible) {
        super(name, module, visible);
        this.text = text;
        this.onlyNumber = false;
    }

    public TextValue(String name, String text, Module module) {
        super(name, module, () -> true);
        this.text = text;
    }

    public TextValue(String name, String text, boolean onlyNumber, Module module, BooleanSupplier visible) {
        super(name, module, visible);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public TextValue(String name, String text, boolean onlyNumber, Module module) {
        super(name, module, () -> true);
        this.text = text;
        this.onlyNumber = onlyNumber;
    }

    public String get() {
        return text;
    }
}
