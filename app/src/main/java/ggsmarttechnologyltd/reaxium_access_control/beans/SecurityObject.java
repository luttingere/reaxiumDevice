package ggsmarttechnologyltd.reaxium_access_control.beans;

/**
 * Created by Eduardo Luttinger on 09/05/2016.
 */
public class SecurityObject extends AppBean {

    private Long cardId;
    private Integer userId;
    private int errorCode = 0;
    private String errorMessage;


    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
