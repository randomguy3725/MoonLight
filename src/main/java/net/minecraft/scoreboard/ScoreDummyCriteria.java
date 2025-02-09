package net.minecraft.scoreboard;

import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria
{
    private final String dummyName;

    public ScoreDummyCriteria(String name)
    {
        this.dummyName = name;
        IScoreObjectiveCriteria.INSTANCES.put(name, this);
    }

    public String getName()
    {
        return this.dummyName;
    }

    public int setScore(List<EntityPlayer> p_96635_1_)
    {
        return 0;
    }

    public boolean isReadOnly()
    {
        return false;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
    }
}
