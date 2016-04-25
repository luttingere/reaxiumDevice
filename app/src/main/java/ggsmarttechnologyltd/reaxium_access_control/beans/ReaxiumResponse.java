package ggsmarttechnologyltd.reaxium_access_control.beans;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Eduardo Luttinger on 21/10/2015.
 */
public class ReaxiumResponse<T> implements Serializable {

    private Integer code;
    private String message;
    private ArrayList<T> object;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<T> getObject() {
        return object;
    }

    public void setObject(ArrayList<T> object) {
        this.object = object;
    }
}
