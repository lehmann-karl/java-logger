import java.util.List;
import java.util.Objects;

public final class LoggerInstance {

    private String source;

    public LoggerInstance(String source) {
        this.source = Objects.requireNonNullElse(source, Logger.config.defaultSource());
    }

    public void setSource(String source) {
        this.source = Objects.requireNonNullElse(source, Logger.config.defaultSource());
    }

    public void log(String message) {
        emit(Logger.Level.INFO, message);
    }

    public void log(LogEvent log) {
        emit(log);
    }

    public void debug(String message) {
        emit(Logger.Level.DEBUG, message);
    }

    public void info(String message) {
        emit(Logger.Level.INFO, message);
    }

    public void warn(String message) {
        emit(Logger.Level.WARN, message);
    }

    public void error(String message) {
        emit(Logger.Level.ERROR, message);
    }

    public void fatal(String message) {
        emit(Logger.Level.FATAL, message);
    }

    private void emit(Logger.Level level, String message) {
        List<String> tokens = new LogEvent(level, source, message).format();

        System.out.println(String.join(" ", tokens));
    }

    private void emit(LogEvent log) {
        List<String> tokens = log.format();
        System.out.println(String.join(" ", tokens));
    }
}
