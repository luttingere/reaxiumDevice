package ggsmarttechnologyltd.reaxium_access_control.beans;

/**
 * Created by Eduardo Luttinger on 27/04/2016.
 */
public class AccessControl extends AppBean {

    private Long userId;
    private String userAccessType;
    private String accessType;
    private Long accessDate;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAccessType() {
        return userAccessType;
    }

    public void setUserAccessType(String userAccessType) {
        this.userAccessType = userAccessType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public Long getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Long accessDate) {
        this.accessDate = accessDate;
    }
}
