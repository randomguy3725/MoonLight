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
package wtf.moonlight.gui.click.skeet.component.impl.sub.checkBox;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import wtf.moonlight.gui.click.skeet.component.Component;
import wtf.moonlight.gui.click.skeet.component.PredicateComponent;
import wtf.moonlight.gui.click.skeet.component.impl.sub.text.TextComponent;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CheckBooxyTextComponent extends Component implements PredicateComponent
{
    private final CheckBooxyComponent checkBox;
    private final TextComponent textComponent;

    public CheckBooxyTextComponent(final Component parent, final String text, final BooleanSupplier isChecked, final BooleanConsumer onChecked, final BooleanSupplier isVisible, final float x, final float y) {
        super(parent, x, y, 0.0f, 5.0f);
        this.checkBox = new CheckBooxyComponent(this, 0.0f, 0.0f, 5.0f, 5.0f) {
            @Override
            public boolean isChecked() {
                return isChecked.getAsBoolean();
            }

            @Override
            public void setChecked(final boolean checked) {
                onChecked.accept(checked);
            }

            @Override
            public boolean isVisible() {
                return isVisible.getAsBoolean();
            }
        };
        this.textComponent = new TextComponent(this, text, 8.0f, 1.0f);
        this.addChild(this.checkBox);
        this.addChild(this.textComponent);
    }

    public CheckBooxyTextComponent(final Component parent, final String text, final BooleanSupplier isChecked, final BooleanConsumer onChecked, final BooleanSupplier isVisible) {
        this(parent, text, isChecked, onChecked, isVisible, 0.0f, 0.0f);
    }

    public CheckBooxyTextComponent(final Component parent, final String text, final BooleanSupplier isChecked, final BooleanConsumer onChecked) {
        this(parent, text, isChecked, onChecked, () -> true);
    }
    
    @Override
    public float getWidth() {
        return 8.0f + this.textComponent.getWidth();
    }
    
    @Override
    public boolean isVisible() {
        return this.checkBox.isVisible();
    }
}
