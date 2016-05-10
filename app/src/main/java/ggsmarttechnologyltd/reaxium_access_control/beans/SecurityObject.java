package ggsmarttechnologyltd.reaxium_access_control.beans;

/**
 * Created by Eduardo Luttinger on 09/05/2016.
 */
public class SecurityObject extends AppBean {

    private Long cardId;
    private Integer userId;


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
}
