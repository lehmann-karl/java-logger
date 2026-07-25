import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class Logger {

    public enum Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }

    public static volatile LoggerConfig config = LoggerConfig.builder().build();

    // Centralized single log consumer thread
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
    });

    // Centralized Queue to serialize all log messages
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    // Inserted into queue to signal logger shutdown
    private static final String SHUTDOWN = "__SHUTDOWN__";

    // Assign static method to process logs across multiple logging sources
    static {
        executor.submit(Logger::processLogs);
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::shutdown));
    }

    private static final Map<String, LoggerInstance> REGISTRY = new ConcurrentHashMap<>();
    private static final LoggerInstance DEFAULT_LOGGER = new LoggerInstance("application");

    private Logger() {
    }

    public static void submit(String output) {
        // Submit final output to the logging queue
        try {
            if (!queue.offer(output)) {
                System.err.printf("[LOGGER] %s[ERROR]%s Log queue full: Dropping new logs\n",
                LogStyle.AnsiColor.RED, LogStyle.AnsiColor.reset());
            }
        } catch (NullPointerException e) {
            System.err.printf("[LOGGER] %s[ERROR]%s Provided output is null\n",
                LogStyle.AnsiColor.RED, LogStyle.AnsiColor.reset());
        }
        
    }

    // processLogs takes incoming log messages out of the queue and prints them to stdout
    private static void processLogs() {
        final List<String> batch = new ArrayList<>(1024);
        final StringBuilder sb = new StringBuilder(16 * 1024);

        boolean shutdown = false;

        try {
            while (!shutdown) {
                // Wait for incoming message
                batch.add(queue.take());
                // String msg = queue.take();

                // Drain everything currently available
                queue.drainTo(batch);

                sb.setLength(0);

                for (String msg : batch) {
                    // Stop logging if shutdown marker is inserted
                    if (SHUTDOWN.equals(msg)) {
                        shutdown = true;
                        break;
                    }

                    sb.append(msg).append(System.lineSeparator());
                }

                if (sb.length() > 0) {
                    String output = sb.toString();
                    System.out.print(output);
                }
                
                batch.clear();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * <p>Process remaining logs and shutdown logging gracefully.</p>
     */
    public static void shutdown() {
        try {
            // Enqueue shutdown marker to signal end of logging
            queue.put(SHUTDOWN);
            // Tell the ExecutorService to shut down
            executor.shutdown();

            // Wait for remaining logs in the queue to get printed
            if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static LoggerInstance create(String source) {
        return REGISTRY.computeIfAbsent(source, LoggerInstance::new);
    }

    public static LoggerInstance get(String source) {
        return REGISTRY.computeIfAbsent(source, LoggerInstance::new);
    }

    public static LoggerInstance getDefault() {
        return DEFAULT_LOGGER;
    }

    public static void configure(LoggerConfig newConfig) {
        config = Objects.requireNonNull(newConfig);
        DEFAULT_LOGGER.setSource(config.defaultSource());
    }

    public static void setDefaultSource(String source) {
        DEFAULT_LOGGER.setSource(source);
    }

    public static void log(String message) {
        DEFAULT_LOGGER.log(message);
    }

    public static void log(Level level, String message) {
        DEFAULT_LOGGER.log(level, message);
    }

    public static void log(LogEvent log) {
        DEFAULT_LOGGER.log(log);
    }

    public static void log(Level level, LogEvent log) {
        DEFAULT_LOGGER.log(level, log);
    }

    public static void debug(String message) {
        DEFAULT_LOGGER.debug(message);
    }

    public static void debug(LogEvent log) {
        DEFAULT_LOGGER.debug(log);
    }

    public static void info(String message) {
        DEFAULT_LOGGER.info(message);
    }

    public static void info(LogEvent log) {
        DEFAULT_LOGGER.info(log);
    }

    public static void warn(String message) {
        DEFAULT_LOGGER.warn(message);
    }

    public static void warn(LogEvent log) {
        DEFAULT_LOGGER.warn(log);
    }

    public static void error(String message) {
        DEFAULT_LOGGER.error(message);
    }

    public static void error(LogEvent log) {
        DEFAULT_LOGGER.error(log);
    }

    public static void fatal(String message) {
        DEFAULT_LOGGER.fatal(message);
    }

    public static void fatal(LogEvent log) {
        DEFAULT_LOGGER.fatal(log);
    }
}