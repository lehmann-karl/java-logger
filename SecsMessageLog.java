import java.util.List;

public class SecsMessageLog extends LogEvent {

    record SecsMessageSignature(int stream, int function) {}

    public static final class SXFY extends LogToken {

        protected SXFY(Object value) {
            super(value);
        }
        
        @Override
        public String render() {
            SecsMessageSignature msgSignature = (SecsMessageSignature) value;
            return "S" + Integer.toString(msgSignature.stream)
                    + "F" + Integer.toString(msgSignature.function);
        }
    }

    private final SXFY sxfy;

    public SecsMessageLog(int stream, int function) {
        super(Logger.Level.INFO, "equipment", null);
        this.sxfy = LogToken.create(SXFY.class,  new SecsMessageSignature(stream, function));
    }

    @Override
    protected List<String> format() {
        List<String> message = super.format();

        String signatureRep = sxfy.render();
        message.add(signatureRep);

        return message;
    }  
}
