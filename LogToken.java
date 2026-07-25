import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LogToken {

    @LogStyle.TokenStyle(color = LogStyle.AnsiColor.BLUE)
    public static final class TIMESTAMP extends LogToken {

        private TIMESTAMP(Object value, String sep) { super(value, sep); }
        
        @Override
        protected String render() {
            String timestamp = ((LocalDateTime) getValue()).format(Logger.config.timestampFormatter());
            return "[" + timestamp + "]";
        }
    }
    
    @LogStyle.TokenStyle(color = LogStyle.AnsiColor.WHITE, bold = true)
    public static final class LEVEL extends LogToken {

        static {
            // DEBUG-Style
            LogStyle debugStyle = LogStyle.builder()
                .color(LogStyle.AnsiColor.GREEN)
                .build();
            LogTheme.registerDefaultStyle(
                resolveTokenID(LEVEL.class, Logger.Level.DEBUG), debugStyle);

            // WARN-Style
            LogStyle warningStyle = LogStyle.builder()
                .color(LogStyle.AnsiColor.YELLOW)
                .build();
            LogTheme.registerDefaultStyle(
                resolveTokenID(LEVEL.class, Logger.Level.WARN), warningStyle);

            // ERROR-Style
            LogStyle errorStyle = LogStyle.builder()
                .color(LogStyle.AnsiColor.RED)
                .build();
            LogTheme.registerDefaultStyle(
                resolveTokenID(LEVEL.class, Logger.Level.ERROR), errorStyle);

            // FATAL-Style
            LogStyle fatalStyle = LogStyle.builder()
                .color(LogStyle.AnsiColor.DARK_RED)
                .build();
            LogTheme.registerDefaultStyle(
                resolveTokenID(LEVEL.class, Logger.Level.FATAL), fatalStyle);
        }

        private LEVEL(Object value, String sep) { super(value, sep); }
        
        @Override
        protected String render() { return "[" + getValue() + "]"; }
    }
    
    @LogStyle.TokenStyle(color = LogStyle.AnsiColor.CYAN)
    public static final class SOURCE extends LogToken {

        private SOURCE(Object value, String sep) { super(value, sep); }
        
        @Override
        protected String render() { return "[" + getValue() + "]"; }
    }

    public static final class MESSAGE extends LogToken {

        private MESSAGE(Object value, String sep) { super(value, sep); }
        
        @Override
        protected String render() { return getValue().toString(); }
    }

    private static final Set<Class<? extends LogToken>> INITIALIZED = ConcurrentHashMap.newKeySet();
    private static final Set<Class<? extends LogToken>> BUILTIN_TOKEN_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        TIMESTAMP.class,
        LEVEL.class,
        SOURCE.class,
        MESSAGE.class
    )));
    private static final ThreadLocal<LogEvent> ACTIVE_EVENT = new ThreadLocal<>();

    private Object value;
    private String separator;

    protected LogToken(Object value, String sep) {
        this.value = value;
        this.separator = sep;
    }

    private static void registerAnnotationStyle(Class<? extends LogToken> tokenClass) {
        if (!INITIALIZED.add(tokenClass)) {
            return;
        }

        LogStyle.TokenStyle annotation = tokenClass.getAnnotation(LogStyle.TokenStyle.class);
        LogStyle style;

        if (annotation == null) {
            style = LogTheme.DEFAULT_LOG_STYLE;
        } else {
            style = LogStyle.builder()
            .color(annotation.color())
            .bold(annotation.bold())
            .italic(annotation.italic())
            .underline(annotation.underline())
            .build();
        }

        LogTheme.registerDefaultStyle(resolveTokenID(tokenClass), style);
    }

    public static <T extends LogToken> void setStyle(Class<T> tokenClass, LogStyle style) {
        String name = resolveTokenID(tokenClass);
        LogTheme.registerStyle(name, style);
    }

    public static <T extends LogToken> void setStyle(Class<T> tokenClass, Object value, LogStyle style) {
        String name = resolveTokenID(tokenClass, value);
        LogTheme.registerStyle(name, style);
    }

    public static <T extends LogToken> T create(Class<T> tokenClass, Object value) {
        return create(tokenClass, value, " ");
    }

    public static <T extends LogToken> T create(Class<T> tokenClass, Object value, String sep) {
        Objects.requireNonNull(tokenClass, "tokenClass must not be null");
        assertTokenCreatePermission(tokenClass);
        registerAnnotationStyle(tokenClass);
        try {
            Constructor<T> constructor = tokenClass.getDeclaredConstructor(Object.class, String.class);
            constructor.setAccessible(true);
            T token = constructor.newInstance(value, sep);
            LogEvent event = ACTIVE_EVENT.get();
            if (event != null) {
                if (token instanceof MESSAGE) {
                    event.addPendingMessageToken(token);
                } else {
                    event.addToken(token);
                }
            }
            return token;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException("Token class must expose a protected or public constructor accepting (Object)", ex);
        }
    }

    private static void assertTokenCreatePermission(Class<? extends LogToken> tokenClass) {
        LogEvent event = ACTIVE_EVENT.get();
        if (event == null) {
            throw new IllegalAccessError("LogTokens must be created during LogEvent construction");
        }
        if (isBuiltInTokenType(tokenClass) && event.containsToken(tokenClass)) {
            throw new IllegalAccessError("Built-in token types are restricted to the logger itsself");
        }
    }

    private static boolean isBuiltInTokenType(Class<? extends LogToken> tokenClass) {
        return BUILTIN_TOKEN_TYPES.contains(tokenClass);
    }

    static void beginCollection(LogEvent event) {
        ACTIVE_EVENT.set(event);
    }

    static void endCollection() {
        ACTIVE_EVENT.remove();
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    protected String render() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return render() + LogStyle.AnsiColor.reset() + separator;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LogToken)) {
            return false;
        }

        LogToken that = (LogToken) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static String normalizeName(String name) {
        return (name != null ? name : "TOKEN").trim().toUpperCase(Locale.ROOT);
    }

    public static String resolveTokenID(Class<? extends LogToken> token, Object value) {
        String configuredName = token.getSimpleName() + "(" +  String.valueOf(value) + ")";
        return normalizeName(configuredName);
    }

    public static String resolveTokenID(Class<? extends LogToken> token) {
        return normalizeName(token.getSimpleName());
    }

    public static String resolveTokenID(LogToken token) {
        return resolveTokenID(token.getClass(), token.value);
    }
}
