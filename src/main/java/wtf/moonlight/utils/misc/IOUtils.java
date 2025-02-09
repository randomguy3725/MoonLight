/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [MukjepScarlet]
 */
package wtf.moonlight.utils.misc;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtils {
    private IOUtils() {}

    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {}
        }
    }
}
