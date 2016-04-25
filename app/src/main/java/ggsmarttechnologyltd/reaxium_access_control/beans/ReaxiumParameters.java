package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class ReaxiumParameters implements Serializable {

    public ReaxiumParameters() {}

    @SerializedName("Users")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
