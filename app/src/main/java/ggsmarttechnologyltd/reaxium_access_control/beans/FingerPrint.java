package ggsmarttechnologyltd.reaxium_access_control.beans;

import android.graphics.Bitmap;

/**
 * Created by Eduardo Luttinger on 20/04/2016.
 */
public class FingerPrint extends AppBean {

    private byte[] fingerPrintFeature;

    private Bitmap fingerPrintImage;


    public byte[] getFingerPrintFeature() {
        return fingerPrintFeature;
    }

    public void setFingerPrintFeature(byte[] fingerPrintFeature) {
        this.fingerPrintFeature = fingerPrintFeature;
    }

    public Bitmap getFingerPrintImage() {
        return fingerPrintImage;
    }

    public void setFingerPrintImage(Bitmap fingerPrintImage) {
        this.fingerPrintImage = fingerPrintImage;
    }
}
