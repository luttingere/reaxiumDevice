package ggsmarttechnologyltd.reaxium_access_control.admin.errormessages;

/**
 * Created by Eduardo Luttinger on 13/05/2016.
 */
public class RFIDErrorMessage {
    /**
     * Success
     */
    public static final int RESULT_OK = 0;
    /**
     * Failed
     */
    public static final int RESULT_FAIL = -1000;
    public static final String RESULT_FAIL_MESSAGE = "Failure read";
    /**
     * Device connection error
     */
    public static final int WRONG_CONNECTION = -1001;
    public static final String WRONG_CONNECTION_MESSAGE = "wrong connection";
    /**
     * Device busy
     */
    public static final int DEVICE_BUSY = -1002;
    public static final String DEVICE_BUSY_MESSAGE = "device is busy";
    /**
     * Device is not opened or can not open
     */
    public static final int DEVICE_NOT_OPEN = -1003;
    public static final String DEVICE_NOT_OPEN_MESSAGE = "device not open";
    /**
     * Timeout
     */
    public static final int TIMEOUT = -1004;
    public static final String TIMEOUT_MESSAGE = "read time out";
    /**
     * Permission  rejected
     */
    public static final int NO_PERMISSION = -1005;
    public static final String NO_PERMISSION_MESSAGE = "no permission";
    /**
     * Input parameter is invalid
     */
    public static final int WRONG_PARAMETER = -1006;
    public static final String WRONG_PARAMETER_MESSAGE = "wrong parameter";
    /**
     * Decode error
     */
    public static final int DECODE_ERROR = -1007;
    public static final String DECODE_ERROR_MESSAGE = "decode error";
    /**
     * Device initialize failed
     */
    public static final int INIT_FAIL = -1008;
    public static final String INIT_FAIL_MESSAGE = "rfid initialization error";
    /**
     * Unknown error
     */
    public static final int UNKNOWN_ERROR = -1009;
    public static final String UNKNOWN_ERROR_MESSAGE = "rfid unknown error";
    /**
     * Not supported operation
     */
    public static final int NOT_SUPPORT = -1010;
    public static final String NOT_SUPPORT_MESSAGE = "not supported";
    /**
     * Not enough memory
     */
    public static final int NOT_ENOUGH_MEMORY = -1011;
    public static final String NOT_ENOUGH_MEMORY_MESSAGE = "not enough memory";
    /**
     * Device not found
     */
    public static final int DEVICE_NOT_FOUND = -1012;
    public static final String DEVICE_NOT_FOUND_MESSAGE = "device not found";
    /**
     * Device repeat open
     */
    public static final int DEVICE_REOPEN = -1013;
    public static final String DEVICE_REOPEN_MESSAGE = "device reopened";
    /**
     * No IC card found
     */
    public static final int NO_CARD = -3000;
    public static final String NO_CARD_MESSAGE = "In order to activate a card, please place it on the RFID reader and press the button Configure Card";
    /**
     * Card is not support
     */
    public static final int INVALID_CARD = -3001;
    public static final String INVALID_CARD_MESSAGE = "invalid rfid card";
    /**
     * Input card key is not correct
     */
    public static final int CARD_VALIDATE_FAIL = -3003;
    public static final String CARD_VALIDATE_FAIL_MESSAGE = "card validation fail";


    public static String getErrorMessage(int errorCode) {

        String errorMessage = "";
        switch (errorCode) {
            case RESULT_FAIL:
                errorMessage = RESULT_FAIL_MESSAGE;
                break;
            case WRONG_CONNECTION:
                errorMessage = WRONG_CONNECTION_MESSAGE;
                break;
            case DEVICE_BUSY:
                errorMessage = DEVICE_BUSY_MESSAGE;
                break;
            case DEVICE_NOT_OPEN:
                errorMessage = DEVICE_NOT_OPEN_MESSAGE;
                break;
            case TIMEOUT:
                errorMessage = TIMEOUT_MESSAGE;
                break;
            case NO_PERMISSION:
                errorMessage = NO_PERMISSION_MESSAGE;
                break;
            case WRONG_PARAMETER:
                errorMessage = WRONG_PARAMETER_MESSAGE;
                break;
            case DECODE_ERROR:
                errorMessage = DECODE_ERROR_MESSAGE;
                break;
            case INIT_FAIL:
                errorMessage = INIT_FAIL_MESSAGE;
                break;
            case UNKNOWN_ERROR:
                errorMessage = UNKNOWN_ERROR_MESSAGE;
                break;
            case NOT_SUPPORT:
                errorMessage = NOT_SUPPORT_MESSAGE;
                break;
            case NOT_ENOUGH_MEMORY:
                errorMessage = NOT_ENOUGH_MEMORY_MESSAGE;
                break;
            case DEVICE_NOT_FOUND:
                errorMessage = DEVICE_NOT_FOUND_MESSAGE;
                break;
            case DEVICE_REOPEN:
                errorMessage = DEVICE_REOPEN_MESSAGE;
                break;
            case NO_CARD:
                errorMessage = NO_CARD_MESSAGE;
                break;
            case INVALID_CARD:
                errorMessage = INVALID_CARD_MESSAGE;
                break;
            case CARD_VALIDATE_FAIL:
                errorMessage = CARD_VALIDATE_FAIL_MESSAGE;
                break;
            default:
                errorMessage = ""+errorCode;
                break;
        }
        return errorMessage;
    }

}
