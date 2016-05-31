package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class UserAccessData extends AppBean {

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("biometric_code")
    private String biometricCode;

    @SerializedName("rfid_code")
    private String rfidCode;

    @SerializedName("user_login_name")
    private String userLoginName;

    @SerializedName("user_password")
    private String userPassword;

    @SerializedName("document_id")
    private String documentId;

    @SerializedName("user")
    private User user;

    @SerializedName("access_type")
    private AccessType accessType;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBiometricCode() {
        return biometricCode;
    }

    public void setBiometricCode(String biometricCode) {
        this.biometricCode = biometricCode;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getUserLoginName() {
        return userLoginName;
    }

    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
