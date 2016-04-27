package ggsmarttechnologyltd.reaxium_access_control.beans;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class User extends AppBean implements Comparable{

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

    @SerializedName("email")
    private String email;

    @SerializedName("status")
    private Status status;

    @SerializedName("user_type")
    private UserType userType;

    @SerializedName("busines")
    private Business business;

    private String accessTime;


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
        if(secondName == null){
            secondName = "";
        }
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
        if(secondLastName == null){
            secondLastName = "";
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessTime() {
        if(accessTime == null){
            accessTime = "";
        }
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    @Override
    public int hashCode() {
        return getUserId().intValue();
    }

    @Override
    public boolean equals(Object another) {
        Boolean equals = Boolean.FALSE;
        if(another instanceof User){
            Log.i("REAXIUM_DEVICE","Probando usuarioId: "+((User) another).getUserId());
            Log.i("REAXIUM_DEVICE","Contra  usuarioId: " + getUserId());
            if(((User) another).getUserId().intValue() == getUserId().intValue()){
                equals = Boolean.TRUE;
            }
        }
        return equals;
    }

    @Override
    public int compareTo(Object another) {
        int comparation = 0;
        if(another instanceof User){
            if(another != null){
                if(((User) another).getUserId().intValue() > getUserId().intValue()){
                    comparation = 1;
                }else if(((User) another).getUserId().intValue() < getUserId().intValue()){
                    comparation = -1;
                }else{
                    comparation = 0;
                }
            }
        }
        return comparation;
    }
}
