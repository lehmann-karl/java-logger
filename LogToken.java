import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LogToken {

    @LogStyle.TokenStyle(color = LogStyle.AnsiColor.BLUE)
    public static final class TIMESTAMP extends LogToken {

        protected TIMESTAMP(Object value) { super(value); }
        
        @Override
        public String render() {
            String timestamp = ((LocalDateTime) value).format(Logger.config.timestampFormatter());
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

        public LEVEL(Object value) { super(value); }
        
        @Override
        public String render() { return "[" + value + "]"; }
    }
    
    @LogStyle.TokenStyle(color = LogStyle.AnsiColor.CYAN)
    public static final class SOURCE extends LogToken {

        protected SOURCE(Object value) { super(value); }
        
        @Override
        public String render() { return "[" + value + "]"; }
    }

    public static final class MESSAGE extends LogToken {

        protected MESSAGE(Object value) { super(value); }
        
        @Override
        public String render() { return value.toString(); }
    }

    private static final Set<Class<? extends LogToken>> INITIALIZED = ConcurrentHashMap.newKeySet();

    protected Object value;

    protected LogToken(Object value) {
        this.value = value;
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
        Objects.requireNonNull(tokenClass, "tokenClass must not be null");
        registerAnnotationStyle(tokenClass);
        try {
            Constructor<T> constructor = tokenClass.getDeclaredConstructor(Object.class);
            constructor.setAccessible(true);
            return constructor.newInstance(value);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalArgumentException("Token class must expose a protected or public constructor accepting (Object)", ex);
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String render() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return render();
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
