public class LogStyle {

    public enum AnsiColor {
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        MAGENTA(35),
        CYAN(36),
        WHITE(37),
        RESET(0);

        private final int code;

        AnsiColor(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }

    public static final class Builder {

        private AnsiColor color = null;
        private boolean bold = false;
        private boolean italic = false;
        private boolean underline = false;

        public Builder color(AnsiColor color) {
            this.color = color;
            return this;
        }

        public Builder bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public Builder italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public Builder underline(boolean underline) {
            this.bold = underline;
            return this;
        }

        public LogStyle build() {
            return new LogStyle(this);
        }
    }

    private final AnsiColor color;
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;

    private LogStyle(Builder builder) {
        this.color = builder.color;
        this.bold = builder.bold;
        this.italic = builder.italic;
        this.underline = builder.underline;
    }

    public static Builder builder() {
        return new Builder();
    }

    public AnsiColor color() {
        return color;
    }

    public boolean bold() {
        return bold;
    }

    public boolean italic() {
        return italic;
    }

    public boolean underline() {
        return underline;
    }

    public String apply(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        if (bold) {
            builder.append("\u001B[1m");
        }
        if (italic) {
            builder.append("\u001B[3m");
        }
        if (underline) {
            builder.append("\u001B[4m");
        }
        if (color != null) {
            builder.append("\u001B[").append(color.code()).append("m");
        }

        builder.append(value);
        if (color != null || bold || italic || underline) {
            builder.append("\u001B[0m");
        }
        return builder.toString();
    }
}
