import java.util.List;

public final class LoggerInstance {

    private String source;

    protected LoggerInstance(String source) {
        this.source = source != null ? source : Logger.config.defaultSource();
    }

    public void setSource(String source) {
        this.source = source != null ? source : Logger.config.defaultSource();
    }

    public void log(Object... values) {
        emit(Logger.Level.INFO, values);
    }

    public void log(Logger.Level level, Object... values) {
        emit(level, values);
    }

    public void log(String template, Object... args) {
        emit(Logger.Level.INFO, template, args);
    }

    public void log(Logger.Level level, String template, Object... args) {
        emit(level, template, args);
    }

    public void log(LogEvent log) {
        emit(Logger.Level.INFO, log);
    }

    public void log(Logger.Level level, LogEvent log) {
        emit(level, log);
    }

    public void debug(Object... values) {
        emit(Logger.Level.DEBUG, values);
    }

    public void debug(String template, Object... args) {
        emit(Logger.Level.DEBUG, template, args);
    }

    public void debug(LogEvent log) {
        emit(Logger.Level.DEBUG, log);
    }

    public void info(Object... values) {
        emit(Logger.Level.INFO, values);
    }

    public void info(String template, Object... args) {
        emit(Logger.Level.INFO, template, args);
    }

    public void info(LogEvent log) {
        emit(Logger.Level.INFO, log);
    }

    public void warn(Object... values) {
        emit(Logger.Level.WARN, values);
    }

    public void warn(String template, Object... args) {
        emit(Logger.Level.WARN, template, args);
    }

    public void warn(LogEvent log) {
        emit(Logger.Level.WARN, log);
    }

    public void error(Object... values) {
        emit(Logger.Level.ERROR, values);
    }

    public void error(String template, Object... args) {
        emit(Logger.Level.ERROR, template, args);
    }

    public void error(LogEvent log) {
        emit(Logger.Level.ERROR, log);
    }

    public void fatal(Object... values) {
        emit(Logger.Level.FATAL, values);
    }

    public void fatal(String template, Object... args) {
        emit(Logger.Level.FATAL, template, args);
    }

    public void fatal(LogEvent log) {
        emit(Logger.Level.FATAL, log);
    }

    private void emit(Logger.Level level, Object... values) {
        String[] valueReps = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            valueReps[i] = String.valueOf(values[i]);
        }

        emit(level, String.join(" ", valueReps));
    }

    private void emit(Logger.Level level, String template, Object... args) {   
        // Insert args for placeholders
        String message = formatTemplateString(template, args);     
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

    private String formatTemplateString(String template, Object... args) {
        // Return early if nothing to format
        if (template == null || args == null || args.length == 0) {
            return template;
        }

        // Setup for building the format string
        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < template.length()) {
            // Scan the template string for the next placeholder
            int placeholder = template.indexOf("{}", i);

            // No more placeholders or args -> append remaining text
            if (placeholder == -1 || argIndex >= args.length) {
                result.append(template.substring(i));
                break;
            }

            // Append text before {}
            result.append(template, i, placeholder);

            // Append argument
            result.append(args[argIndex] != null ? args[argIndex] : "null");

            argIndex++;

            // Skip to the index after {}
            i = placeholder + 2;
        }

        return result.toString();
    }
}
