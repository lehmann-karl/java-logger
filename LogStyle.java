import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class LogStyle {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TokenStyle {
        LogStyle.AnsiColor color();
        boolean bold() default false;
        boolean italic() default false;
        boolean underline() default false;
    }

    public enum AnsiColor {
        BLACK("30"),
        RED("31"),
        DARK_RED("38;5;88"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        MAGENTA("35"),
        CYAN("36"),
        WHITE("37");

        private final String code;

        AnsiColor(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static String reset() {
            return "\u001B[0m";
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
            this.underline = underline;
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
        if (color != null && Logger.config.showColor()) {
            builder.append("\u001B[").append(color.code()).append("m");
        }

        builder.append(value);
        if (color != null || bold || italic || underline) {
            builder.append(AnsiColor.reset());
        }
        return builder.toString();
    }
}
