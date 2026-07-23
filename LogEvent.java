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

    protected List<String> format() {
        List<String> tokens = new ArrayList<String>();
        
        if (Logger.config.showTimestamp()) {
            String timeRep = timestamp.render();
            String timestampStyled = LogTheme.getStyle(timestamp).get().apply(timeRep);
            tokens.add(timestampStyled);
        }

        if (Logger.config.showLevel()) {
            String levelRep = level.render();
            String levelStyled = LogTheme.getStyle(level).get().apply(levelRep);
            tokens.add(levelStyled);
        }

        if (Logger.config.showSource()) {
            String sourceRep = source.render();
            String sourceStyled = LogTheme.getStyle(source).get().apply(sourceRep);
            tokens.add(sourceStyled);
        }

        if (message.value != null) {
            String messageRep = message.render();
            String messageStyled = LogTheme.getStyle(message).get().apply(messageRep);
            tokens.add(messageStyled);
        }

        return tokens;
    }
}
