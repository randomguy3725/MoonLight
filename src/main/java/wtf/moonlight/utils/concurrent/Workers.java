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
package wtf.moonlight.utils.concurrent;

import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.moonlight.Moonlight;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Workers {

    private Workers() {}

    private static int getAvailableBackgroundThreads() {
        int maxCount = 255;
        try {
            maxCount = Integer.parseInt(System.getProperty("max.bg.threads"));
        } catch (NumberFormatException ignored) {}

        return MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 1, 1, maxCount);
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        Moonlight.LOGGER.error("Uncaught Exception in thread {}", t.getName(), e);
    }

    private static ExecutorService createWorker(String name) {
        var id = new AtomicInteger(1);
        return new ForkJoinPool(getAvailableBackgroundThreads(), pool -> {
            var threadName = "Worker-" + name + "-" + id.getAndIncrement();

            var forkJoinWorkerThread = new ForkJoinWorkerThread(pool) {
                protected void onTermination(@Nullable Throwable throwable) {
                    if (throwable != null) {
                        Moonlight.LOGGER.warn("{} died", threadName, throwable);
                    } else {
                        Moonlight.LOGGER.debug("{} stopped", threadName);
                    }
                }
            };

            forkJoinWorkerThread.setName(threadName);

            return forkJoinWorkerThread;
        }, Workers::uncaughtExceptionHandler, true);
    }

    private static ExecutorService createIoWorker(String name) {
        var id = new AtomicInteger(1);
        return Executors.newCachedThreadPool(runnable -> {
            var thread = new Thread(runnable);
            thread.setName(name + "-" + id.getAndIncrement());
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(Workers::uncaughtExceptionHandler);
            return thread;
        });
    }

    @NotNull
    public static final ExecutorService Default = createWorker("Default");

    @NotNull
    public static final ExecutorService IO = createIoWorker("IO");

}
