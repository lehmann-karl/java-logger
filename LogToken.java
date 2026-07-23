import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

public abstract class LogToken {

    public static final class TIMESTAMP extends LogToken {

        protected TIMESTAMP(Object value) { super(value); }
        
        @Override
        public String render() {
            String timestamp = ((LocalDateTime) value).format(Logger.config.timestampFormatter());
            return "[" + timestamp + "]";
        }
    }
    
    public static final class LEVEL extends LogToken {

        protected LEVEL(Object value) { super(value); }
        
        @Override
        public String render() { return "[" + value + "]"; }
    }
    
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

    protected Object value;

    protected LogToken(Object value) {
        this.value = value;
        String name = resolveTokenName(this.getClass());

        if (LogTheme.getStyle(name).isEmpty()) {
            LogTheme.registerStyle(name, LogTheme.DEFAULT_LOG_STYLE);
        }
    }

    public static <T extends LogToken> void setStyle(Class<T> tokenClass, LogStyle style) {
        String name = resolveTokenName(tokenClass);
        LogTheme.registerStyle(name, style);
    }

    public static <T extends LogToken> T create(Class<T> tokenClass, Object value) {
        Objects.requireNonNull(tokenClass, "tokenClass must not be null");
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
        return Objects.requireNonNullElse(name, "TOKEN").trim().toUpperCase(Locale.ROOT);
    }

    public static String resolveTokenName(Class<? extends LogToken> tokenClass) {
        String configuredName = tokenClass.getSimpleName();
        return normalizeName(configuredName);
    }
}
