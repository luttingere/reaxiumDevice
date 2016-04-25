package ggsmarttechnologyltd.reaxium_access_control.beans;

/**
 * Created by Eduardo Luttinger on 21/04/2016.
 */
public class BiometricData extends AppBean {

    private Long userId;
    private String biometricCode;


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
}
