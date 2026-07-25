import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogEvent {

    private final List<LogToken> tokens = new ArrayList<>();
    private LogToken pendingMessageToken;

    private LogToken.LEVEL level;
    private LogToken.SOURCE source;

    protected LogEvent(String message) {
        LogToken.beginCollection(this);
        LogToken.create(LogToken.TIMESTAMP.class, LocalDateTime.now());
        this.level = LogToken.create(LogToken.LEVEL.class, Logger.Level.INFO);
        this.source = LogToken.create(LogToken.SOURCE.class, Logger.config.defaultSource());
        LogToken.create(LogToken.MESSAGE.class, message);
    }

    protected List<LogToken> format() {
        LogToken.endCollection();
        finalizeTokens();

        List<LogToken> formattedTokens = new ArrayList<>();

        for (LogToken token : tokens) {
            if (shouldIncludeToken(token)) {
                formattedTokens.add(token);
            }
        }

        return formattedTokens;
    }

    void setLevel(Logger.Level level) {
        this.level.setValue(level);
    }

    void setSource(String source) {
        this.source.setValue(source);
    }

    void addToken(LogToken token) {
        tokens.add(token);
    }

    void addPendingMessageToken(LogToken token) {
        pendingMessageToken = token;
    }

    boolean containsToken(Class<? extends LogToken> tokenClass) {
        boolean alreadyInTokens = tokens.stream().anyMatch(token -> token.getClass() == tokenClass);
        return alreadyInTokens || (pendingMessageToken != null && pendingMessageToken.getClass() == tokenClass);
    }

    private void finalizeTokens() {
        if (pendingMessageToken != null && !tokens.contains(pendingMessageToken)) {
            tokens.add(pendingMessageToken);
        }
        pendingMessageToken = null;
    }

    private boolean shouldIncludeToken(LogToken token) {
        if (token.getValue() == null) {
            return false;
        }

        if (token instanceof LogToken.TIMESTAMP) {
            return Logger.config.showTimestamp();
        }

        if (token instanceof LogToken.LEVEL) {
            return Logger.config.showLevel();
        }

        if (token instanceof LogToken.SOURCE) {
            return Logger.config.showSource();
        }

        return true;
    }
}
