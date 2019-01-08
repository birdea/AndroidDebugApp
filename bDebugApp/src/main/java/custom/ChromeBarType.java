package custom;


/**
 * Created by taskin on 08/11/2017.
 */

public enum ChromeBarType {
    CBTNone,
    CBTListening,
    CBTSpeaking,
    CBTReady,
    CBTError,
    CBTThinking,
    CBTThinkingEndDUX,
    CBTThinkingEndVUX,
    CBTThinkingEndERROR,
    ;

    public static String[] getTypes() {
        String[] array = new String[values().length];
        int i=0;
        for (ChromeBarType type : values()) {
            array[i++] = type.name();
        }
        return array;
    }
}
