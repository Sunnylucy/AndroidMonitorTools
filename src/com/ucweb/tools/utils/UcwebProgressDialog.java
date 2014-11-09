package com.ucweb.tools.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucweb.tools.R;

public class UcwebProgressDialog extends Dialog{
	
//	private Context context = null;  
    private static UcwebProgressDialog customProgressDialog = null;
	
	public UcwebProgressDialog(Context context) {
		super(context);
//		this.context = context;
	}
	
	public UcwebProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static UcwebProgressDialog createDialog(Context context) {  
        customProgressDialog = new UcwebProgressDialog(context,  
                R.style.CustomProgressDialog);  
        customProgressDialog.setContentView(R.layout.my_progressdialog);  
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
        return customProgressDialog;  
    }  
  
    public void onWindowFocusChanged(boolean hasFocus) {  
        if (customProgressDialog == null) {  
            return;  
        }  
        ImageView imageView = (ImageView) customProgressDialog  
                .findViewById(R.id.loadingImageView);  
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView  
                .getBackground();  
        animationDrawable.start();  
    }  
  
    public UcwebProgressDialog setTitile(String strTitle) {  
        return customProgressDialog;  
    }  
  
    public UcwebProgressDialog setMessage(String strMessage) {  
        TextView tvMsg = (TextView) customProgressDialog  
                .findViewById(R.id.id_tv_loadingmsg);  
        if (tvMsg != null) {  
            tvMsg.setText(strMessage);  
        }  
        return customProgressDialog;  
    }
}
