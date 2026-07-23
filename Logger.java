import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class Logger {

    public enum Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }

    public static volatile LoggerConfig config = LoggerConfig.builder().build();
    private static final Map<String, LoggerInstance> REGISTRY = new ConcurrentHashMap<>();
    private static final LoggerInstance DEFAULT_LOGGER = new LoggerInstance("application");

    private Logger() {
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

    public static void log(LogEvent log) {
        DEFAULT_LOGGER.log(log);
    }

    public static void debug(String message) {
        DEFAULT_LOGGER.debug(message);
    }

    public static void info(String message) {
        DEFAULT_LOGGER.info(message);
    }

    public static void warn(String message) {
        DEFAULT_LOGGER.warn(message);
    }

    public static void error(String message) {
        DEFAULT_LOGGER.error(message);
    }

    public static void fatal(String message) {
        DEFAULT_LOGGER.fatal(message);
    }

    public static void main(String[] args) {
        Logger.configure(LoggerConfig.builder()
            .showLevel(false)
            .showTimestamp(false)
            .showSource(true)
            .defaultSource("SYSTEM")
            .build()
        );

        LogStyle sourceStyle = LogStyle.builder()
            .color(LogStyle.AnsiColor.RED)
            .italic(true)
            .bold(true)
            .build();

        LogToken.setStyle(LogToken.SOURCE.class, sourceStyle);

        // Logger.log("Application has started.");

        // LoggerInstance appLogger = Logger.get("application");
        // LoggerInstance dbLogger = Logger.get("database");

        // appLogger.info("Application is ready.");
        // dbLogger.info("Connection established.");

        Logger.log("Hello, World!");
        Logger.log(new SecsMessageLog(1, 1));
    }
}