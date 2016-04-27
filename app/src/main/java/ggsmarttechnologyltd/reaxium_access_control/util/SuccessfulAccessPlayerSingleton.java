package ggsmarttechnologyltd.reaxium_access_control.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import ggsmarttechnologyltd.reaxium_access_control.R;

/**
 * Created by Eduardo Luttinger on 07/12/2015.
 */
public class SuccessfulAccessPlayerSingleton {

    private MediaPlayer mediaPlayer;

    private static SuccessfulAccessPlayerSingleton mediaPlayerSingleton;

    private Context mContext;

    private SuccessfulAccessPlayerSingleton(Context context){
        mContext = context;
        mediaPlayer = MediaPlayer.create(context, R.raw.success_sound);
        mediaPlayer.setLooping(Boolean.FALSE);
        mediaPlayer.setVolume(100,100);
    }
    public static SuccessfulAccessPlayerSingleton getInstance(Context context){
        if(mediaPlayerSingleton == null){
            mediaPlayerSingleton = new SuccessfulAccessPlayerSingleton(context);
        }
        return mediaPlayerSingleton;
    }

    public void initRingTone(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void stopRingTone(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer = MediaPlayer.create(mContext, R.raw.success_sound);
                mediaPlayer.setLooping(Boolean.TRUE);
                mediaPlayer.prepare();
                mediaPlayer.setVolume(100,100);
            }catch (Exception e){
                Log.e("MD_SINGLETON","",e);
            }
        }
    }


}
