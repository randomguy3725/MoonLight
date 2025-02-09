package net.minecraft.world.storage;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

import java.util.List;

public interface ISaveFormat
{
    String getName();

    ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);

    List<SaveFormatComparator> getSaveList() throws AnvilConverterException;

    void flushCache();

    WorldInfo getWorldInfo(String saveName);

    boolean isNewLevelIdAcceptable(String saveName);

    boolean deleteWorldDirectory(String saveName);

    void renameWorld(String dirName, String newName);

    boolean isConvertible(String saveName);

    boolean isOldMapFormat(String saveName);

    boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

    boolean canLoadWorld(String saveName);
}
