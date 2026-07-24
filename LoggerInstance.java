import java.util.ArrayList;
import java.util.List;

public final class LoggerInstance {

    private String source;

    protected LoggerInstance(String source) {
        this.source = source != null ? source : Logger.config.defaultSource();
    }

    public void setSource(String source) {
        this.source = source != null ? source : Logger.config.defaultSource();
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
        emit(new LogEvent(level, source, message));
    }

    private void emit(LogEvent log) {
        List<LogToken> tokens = log.format();
        List<String> output = new ArrayList<>();

        for (LogToken token : tokens) {
            String tokenRep = token.render();
            String styledToken = LogTheme.getStyle(token).get().apply(tokenRep);
            output.add(styledToken);
        }

        Logger.submit(String.join(" ", output));
    }
}
