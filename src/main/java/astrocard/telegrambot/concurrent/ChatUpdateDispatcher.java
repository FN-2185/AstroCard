package astrocard.telegrambot.concurrent;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class ChatUpdateDispatcher implements AutoCloseable {
    private final ThreadPoolExecutor[] workers;
    public ChatUpdateDispatcher(@Value("${bot.workers:8}") int stripes,
                                @Value("${bot.queue-capacity:128}") int capacity) {
        if (stripes < 1 || capacity < 1) throw new IllegalArgumentException("Positive worker and queue sizes required");
        workers = new ThreadPoolExecutor[stripes];
        AtomicInteger ids = new AtomicInteger();
        for (int i = 0; i < stripes; i++) {
            workers[i] = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(capacity), runnable -> new Thread(runnable, "bot-chat-" + ids.incrementAndGet()),
                    (task, executor) -> {
                        if (executor.isShutdown()) throw new RejectedExecutionException("Bot is stopping");
                        try {

                            executor.getQueue().put(task);
                            if (executor.isShutdown() && executor.remove(task)) {
                                throw new RejectedExecutionException("Bot is stopping");
                            }
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new RejectedExecutionException("Interrupted while queueing update", exception);
                        }
                    });
        }
    }

    public void dispatch(long chatId, Runnable task) {
        workers[Math.floorMod(Long.hashCode(chatId), workers.length)].execute(task);
    }

    @Override @PreDestroy
    public void close() {
        for (var worker : workers) worker.shutdown();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        boolean interrupted = false;
        for (var worker : workers) {
            try {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0 || !worker.awaitTermination(remaining, TimeUnit.NANOSECONDS)) worker.shutdownNow();
            } catch (InterruptedException exception) {
                interrupted = true;
                worker.shutdownNow();
            }
        }
        if (interrupted) Thread.currentThread().interrupt();
    }
}
