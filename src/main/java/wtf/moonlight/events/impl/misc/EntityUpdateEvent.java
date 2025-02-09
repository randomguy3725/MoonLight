package wtf.moonlight.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import wtf.moonlight.events.impl.Event;

@AllArgsConstructor
@Getter
public class EntityUpdateEvent implements Event {
    public final EntityLivingBase entity;
}
