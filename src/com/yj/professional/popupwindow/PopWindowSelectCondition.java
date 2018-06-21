package com.yj.professional.popupwindow;

import com.yj.professional.activity.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

/**
 * @author liaoyao
 * �Զ���popupwindow����  ���Ͻǵ���ѡ��鿴����
 */
public class PopWindowSelectCondition extends PopupWindow {
	private View contentView;  
	private Button btn_select_patient, btn_select_sample;
//	, btn_select_date;
    public PopWindowSelectCondition(final Activity context, int w, int h, View.OnClickListener mClickListener){  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        contentView = inflater.inflate(R.layout.popup_window_select_condition, null);  
        // ����SelectPicPopupWindow��View  
        this.setContentView(contentView);  
        // ����SelectPicPopupWindow��������Ŀ�  
        this.setWidth(w / 2 + 40);  
        // ����SelectPicPopupWindow��������ĸ�  
        this.setHeight(LayoutParams.WRAP_CONTENT);  
        // ����SelectPicPopupWindow��������ɵ��  
        this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // ˢ��״̬  
        this.update();  
        // ʵ����һ��ColorDrawable��ɫΪ��͸��  
        ColorDrawable dw = new ColorDrawable(0000000000);  
        // ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���  
        this.setBackgroundDrawable(dw);  
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
        // ����SelectPicPopupWindow�������嶯��Ч��  
        this.setAnimationStyle(R.style.AnimationPreview);  
        btn_select_patient = (Button)contentView.findViewById(R.id.btn_popup_select_patient);
        btn_select_sample = (Button)contentView.findViewById(R.id.btn_popup_select_sample);
//        btn_select_date = (Button)contentView.findViewById(R.id.btn_popup_select_date);
        btn_select_patient.setOnClickListener(mClickListener);
        btn_select_sample.setOnClickListener(mClickListener);
//        btn_select_date.setOnClickListener(mClickListener);
    }  
      
    /** 
     * ��ʾpopupWindow 
     *  
     * @param parent 
     */  
    public void showPopupWindow(View parent) {  
        if (!this.isShowing()) {  
            // ��������ʽ��ʾpopupwindow  
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);  
        } else {  
            this.dismiss();  
        }  
    }  
}  
