import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogEvent {

    private final LogToken.TIMESTAMP timestamp;
    private final LogToken.LEVEL level;
    private final LogToken.SOURCE source;
    private final LogToken.MESSAGE message;

    protected LogEvent(Logger.Level level, String source, String message) {
        this.timestamp = LogToken.create(LogToken.TIMESTAMP.class, LocalDateTime.now());
        this.level = LogToken.create(LogToken.LEVEL.class, level);
        this.source = LogToken.create(LogToken.SOURCE.class, source);
        this.message = LogToken.create(LogToken.MESSAGE.class, message);
    }

    protected List<LogToken> format() {
        List<LogToken> tokens = new ArrayList<LogToken>();
        
        if (Logger.config.showTimestamp()) {
            tokens.add(timestamp);
        }

        if (Logger.config.showLevel()) {
            tokens.add(level);
        }

        if (Logger.config.showSource()) {
            tokens.add(source);
        }

        if (message.value != null) {
            tokens.add(message);
        }

        return tokens;
    }
}
