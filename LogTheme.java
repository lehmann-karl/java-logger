import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LogTheme {

    public static final LogStyle DEFAULT_LOG_STYLE = LogStyle.builder().build();
    private static final Map<String, LogStyle> TOKEN_STYLES = new LinkedHashMap<>();

    public static void registerStyle(String tokenID, LogStyle style) {
        TOKEN_STYLES.put(LogToken.normalizeName(tokenID), style);
    }

    public static void registerDefaultStyle(String tokenID, LogStyle style) {
        TOKEN_STYLES.putIfAbsent(
            LogToken.normalizeName(tokenID),
            style
        );
    }

    public static Optional<LogStyle> getStyle(String tokenID) {
        return Optional.ofNullable(TOKEN_STYLES.get(LogToken.normalizeName(tokenID)));
    }

    public static Optional<LogStyle> getStyle(LogToken token) {
        if (token == null) {
            return Optional.empty();
        }
        Optional<LogStyle> style = getStyle(LogToken.resolveTokenID(token));
        if (style.isPresent()) {
            return style;
        }

        Class<? extends LogToken> tokenClass = token.getClass();
        return getStyle(LogToken.resolveTokenID(tokenClass));
    }
}
