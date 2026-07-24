import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class LoggerConfig {

    public static final class Builder {

        private boolean showLevel = true;
        private boolean showTimestamp = true;
        private boolean showSource = false;
        private String defaultSource = "application";
        private DateTimeFormatter timestampFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");

        public Builder showLevel(boolean show) {
            this.showLevel = show;
            return this;
        }

        public Builder showTimestamp(boolean show) {
            this.showTimestamp = show;
            return this;
        }

        public Builder showSource(boolean show) {
            this.showSource = show;
            return this;
        }

        public Builder defaultSource(String source) {
            this.defaultSource = source != null ? source : "application";
            return this;
        }

        public Builder timestampFormat(String pattern) {
            this.timestampFormatter = DateTimeFormatter.ofPattern(pattern);
            return this;
        }

        public Builder timestampFormatter(DateTimeFormatter formatter) {
            this.timestampFormatter = Objects.requireNonNull(formatter);
            return this;
        }

        public LoggerConfig build() {
            return new LoggerConfig(this);
        }
    }

    private final boolean showLevel;
    private final boolean showTimestamp;
    private final boolean showSource;
    private final String defaultSource;
    private final DateTimeFormatter timestampFormatter;

    private LoggerConfig(Builder builder) {
        this.showLevel = builder.showLevel;
        this.showTimestamp = builder.showTimestamp;
        this.showSource = builder.showSource;
        this.defaultSource = builder.defaultSource;
        this.timestampFormatter = builder.timestampFormatter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean showLevel() {
        return showLevel;
    }

    public boolean showTimestamp() {
        return showTimestamp;
    }

    public boolean showSource() {
        return showSource;
    }

    public String defaultSource() {
        return defaultSource;
    }

    public DateTimeFormatter timestampFormatter() {
        return timestampFormatter;
    }
}