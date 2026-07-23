import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LogTheme {

    public static final LogStyle DEFAULT_LOG_STYLE = LogStyle.builder().build();
    private static final Map<String, LogStyle> TOKEN_STYLES = new LinkedHashMap<>();

    public static void registerStyle(String tokenName, LogStyle style) {
        TOKEN_STYLES.put(LogToken.normalizeName(tokenName), style);
    }

    public static Optional<LogStyle> getStyle(String tokenName) {
        return Optional.ofNullable(TOKEN_STYLES.get(LogToken.normalizeName(tokenName)));
    }

    public static Optional<LogStyle> getStyle(LogToken token) {
        if (token == null) {
            return Optional.empty();
        }
        return getStyle(LogToken.resolveTokenName(token.getClass()));
    }
}
