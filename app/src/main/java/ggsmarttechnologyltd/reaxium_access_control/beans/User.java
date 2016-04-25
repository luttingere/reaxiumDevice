package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class User extends AppBean {

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("document_id")
    private String documentId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("second_name")
    private String secondName;

    @SerializedName("first_last_name")
    private String firstLastName;

    @SerializedName("second_last_name")
    private String secondLastName;

    @SerializedName("fingerprint")
    private String fingerprint;

    @SerializedName("user_photo")
    private String photo;

    @SerializedName("birthdate")
    private String birthDate;

    @SerializedName("status")
    private Status status;

    @SerializedName("user_type")
    private UserType userType;

    @SerializedName("busines")
    private Business business;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
}
