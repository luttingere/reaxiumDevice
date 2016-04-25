package ggsmarttechnologyltd.reaxium_access_control.utilityViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Eduardop Luttinger on 15/04/2016.
 */
public class NormalCustomTextView extends TextView {

    Typeface font;

    public NormalCustomTextView(Context context) {
        super(context);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/helveticaneue-condensedbold.ttf");
        this.setTypeface(font);
    }

    public NormalCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/helveticaneue-condensedbold.ttf");
        this.setTypeface(font);
    }

    public NormalCustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/helveticaneue-condensedbold.ttf");
        this.setTypeface(font);
    }

}
