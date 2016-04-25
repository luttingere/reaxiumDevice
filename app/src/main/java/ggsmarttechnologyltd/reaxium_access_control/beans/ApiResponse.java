package ggsmarttechnologyltd.reaxium_access_control.beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eduardo Luttinger on 19/04/2016.
 */
public class ApiResponse<T> implements Serializable {

    @SerializedName("ReaxiumResponse")
    private ReaxiumResponse<T> reaxiumResponse;


    public ReaxiumResponse<T> getReaxiumResponse() {
        return reaxiumResponse;
    }

    public void setReaxiumResponse(ReaxiumResponse<T> reaxiumResponse) {
        this.reaxiumResponse = reaxiumResponse;
    }
}
