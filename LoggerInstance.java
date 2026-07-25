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

    public void log(Logger.Level level, String message) {
        emit(level, message);
    }

    public void log(LogEvent log) {
        emit(Logger.Level.INFO, log);
    }

    public void log(Logger.Level level, LogEvent log) {
        emit(level, log);
    }

    public void debug(String message) {
        emit(Logger.Level.DEBUG, message);
    }

    public void debug(LogEvent log) {
        emit(Logger.Level.DEBUG, log);
    }

    public void info(String message) {
        emit(Logger.Level.INFO, message);
    }

    public void info(LogEvent log) {
        emit(Logger.Level.INFO, log);
    }

    public void warn(String message) {
        emit(Logger.Level.WARN, message);
    }

    public void warn(LogEvent log) {
        emit(Logger.Level.WARN, log);
    }

    public void error(String message) {
        emit(Logger.Level.ERROR, message);
    }

    public void error(LogEvent log) {
        emit(Logger.Level.ERROR, log);
    }

    public void fatal(String message) {
        emit(Logger.Level.FATAL, message);
    }

    public void fatal(LogEvent log) {
        emit(Logger.Level.FATAL, log);
    }

    private void emit(Logger.Level level, String message) {        
        emit(level, new LogEvent(message));
    }

    private void emit(Logger.Level level, LogEvent log) {
        log.setLevel(level);
        log.setSource(source);
        List<LogToken> tokens = log.format();
        StringBuilder output = new StringBuilder();

        for (LogToken token : tokens) {
            String tokenRep = token.toString();
            String styledToken = LogTheme.getStyle(token).get().apply(tokenRep);
            output.append(styledToken);
        }

        Logger.submit(output.toString());
    }
}
