package com.risewide.bdebugapp.smsmms.pdu;

/**
 * @author seungtae.hwang (birdea@sk.com)
 * @since 2019. 2. 15.
 */
public class InvalidHeaderValueException  extends MmsException {
    private static final long serialVersionUID = -2053384496042052262L;
    /**
     * Constructs an InvalidHeaderValueException with no detailed message.
     */
    public InvalidHeaderValueException() {
        super();
    }
    /**
     * Constructs an InvalidHeaderValueException with the specified detailed message.
     *
     * @param message the detailed message.
     */
    public InvalidHeaderValueException(String message) {
        super(message);
    }
}
