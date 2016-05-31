package ggsmarttechnologyltd.reaxium_access_control.admin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import ggsmarttechnologyltd.reaxium_access_control.R;
import ggsmarttechnologyltd.reaxium_access_control.admin.listeners.OnValidateDocumentCodeListener;
import ggsmarttechnologyltd.reaxium_access_control.beans.User;
import ggsmarttechnologyltd.reaxium_access_control.util.GGUtil;
import ggsmarttechnologyltd.reaxium_access_control.util.MySingletonUtil;

/**
 * Created by Eduardo Luttinger on 26/04/2016.
 */
public class DocumentCodeAccessDialog extends Dialog {


    /**
     * contexto del sistema
     */
    private Context context;

    private OnValidateDocumentCodeListener validateController;
    private EditText documentCode;
    private RelativeLayout closeDialogButton;
    private RelativeLayout validateAccessButton;



    public DocumentCodeAccessDialog(Context context) {
        super(context);
        this.context = context;
    }

    public DocumentCodeAccessDialog(Context context, int themeResId, OnValidateDocumentCodeListener onValidateDocumentCodeListener) {
        super(context, themeResId);
        this.context = context;
        this.validateController = onValidateDocumentCodeListener;
    }

    protected DocumentCodeAccessDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_number_access_dialog);
        setViews();
        setEvents();
    }

    private void setViews() {
        documentCode = (EditText) findViewById(R.id.studen_id_input);
        closeDialogButton = (RelativeLayout) findViewById(R.id.close_container);
        validateAccessButton = (RelativeLayout) findViewById(R.id.validateDocumentCodeButton);

    }

    private void setEvents(){
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        validateAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!documentCode.getText().toString().equals("")){
                    validateController.validateDocument(documentCode.getText().toString());
                }else{
                    GGUtil.showAToast(context,"Invalid document number");
                }
                dismiss();
            }
        });
    }



}
