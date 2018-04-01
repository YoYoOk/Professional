package com.yj.professional.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * @author liaoyao
 * ʵ���󻬵�list�б�
 */
public class SideslipListView extends ListView {

	private static final String TAG = "SideslipListView";

    private int mScreenWidth;//��Ļ�Ŀ��
    private boolean isDeleteShow;//ɾ������Ƿ���ʾ
    private ViewGroup mPointChild;//��ָ����λ�õ�item���
    private int mDeleteWidth;//ɾ������Ŀ��
    private LinearLayout.LayoutParams mItemLayoutParams;//��ָ����ʱ���ڵ�item�Ĳ��ֲ���
    private int mDownX;//��ָ���ΰ��µ�X����
    private int mDownY;//��ָ���ΰ��µ�Y����

    private int mPointPosition;//��ָ����λ�����ڵ�itemλ��
    private boolean isAllowItemClick;//�Ƿ�����item���

    public SideslipListView(Context context) {
        super(context);
        init(context);
    }

    public SideslipListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SideslipListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // ��ȡ��Ļ���
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        Log.i(TAG, "***********mScreenWidth: " + mScreenWidth);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//�¼�����
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                isAllowItemClick = true;

                //�໬ɾ��
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                mPointPosition = pointToPosition(mDownX, mDownY);
                Log.i(TAG, "*******pointToPosition(mDownX, mDownY): " + mPointPosition);
                if (mPointPosition != -1) {
                    if (isDeleteShow) {
                        ViewGroup tmpViewGroup = (ViewGroup) getChildAt(mPointPosition - getFirstVisiblePosition());
                        if (!mPointChild.equals(tmpViewGroup)) {
                            turnNormal();
                        }
                    }
                    //��ȡ��ǰ��item
                    mPointChild = (ViewGroup) getChildAt(mPointPosition - getFirstVisiblePosition());

                    mDeleteWidth = mPointChild.getChildAt(1).getLayoutParams().width;
                    mItemLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();

                    Log.i(TAG, "*********mItemLayoutParams.height: " + mItemLayoutParams.height +
                            ", mDeleteWidth: " + mDeleteWidth);
                    mItemLayoutParams.width = mScreenWidth;
                    mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int nowX = (int) ev.getX();
                int nowY = (int) ev.getY();
                int diffX = nowX - mDownX;
                Log.i(TAG, "******dp2px(4): " + dp2px(8) + ", dp2px(8): " + dp2px(8) +
                        ", density: " + getContext().getResources().getDisplayMetrics().density);
                if (Math.abs(diffX) > dp2px(4) || Math.abs(nowY - mDownY) > dp2px(4)) {
                    return true;//�����Ӳ������е���Ŀؼ�ʱ������Ч
                }
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {//�¼���Ӧ
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                performActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                performActionUp(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void performActionDown(MotionEvent ev) {
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();
        if (isDeleteShow) {
            ViewGroup tmpViewGroup = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY) - getFirstVisiblePosition());
            Log.i(TAG, "*********mPointChild.equals(tmpViewGroup): " + mPointChild.equals(tmpViewGroup));
            if (!mPointChild.equals(tmpViewGroup)) {
                turnNormal();
            }
        }
        //��ȡ��ǰ��item
        mPointChild = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY) - getFirstVisiblePosition());

        mDeleteWidth = mPointChild.getChildAt(1).getLayoutParams().width;//��ȡɾ������Ŀ��
        Log.i(TAG, "**********pointToPosition(x,y): " + pointToPosition(mDownX, mDownY)
                + ", getFirstVisiblePosition() = " + getFirstVisiblePosition()
                + ", mDeleteWidth = " + mDeleteWidth);
        mItemLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();

        mItemLayoutParams.width = mScreenWidth;
        mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
    }

    private boolean performActionMove(MotionEvent ev) {
        int nowX = (int) ev.getX();
        int nowY = (int) ev.getY();
        int diffX = nowX - mDownX;
        if (Math.abs(diffX) > Math.abs(nowY - mDownY) && Math.abs(nowY - mDownY) < 20) {
            if (!isDeleteShow && nowX < mDownX) {//ɾ����ťδ��ʾʱ����
                if (-diffX >= mDeleteWidth) {//��������������ɾ������Ŀ��ʱ����ƫ�Ƶ������
                    diffX = -mDeleteWidth;
                }
                mItemLayoutParams.leftMargin = diffX;
                mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                isAllowItemClick = false;
            } else if (isDeleteShow && nowX > mDownX) {//ɾ����ť��ʾʱ���һ�
                if (diffX >= mDeleteWidth) {
                    diffX = mDeleteWidth;
                }
                mItemLayoutParams.leftMargin = diffX - mDeleteWidth;
                mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                isAllowItemClick = false;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void performActionUp(MotionEvent ev) {
        //������󻬳��������صĶ���֮һ��ȫ����ʾ
        if (-mItemLayoutParams.leftMargin >= mDeleteWidth / 2) {
            mItemLayoutParams.leftMargin = -mDeleteWidth;
            isDeleteShow = true;
            mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
        } else {
            turnNormal();
        }
    }

    /**
     * ת��Ϊ�����������
     */
    public void turnNormal() {
        mItemLayoutParams.leftMargin = 0;
        mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
        isDeleteShow = false;
    }

    /**
     * �Ƿ�����Item���
     *
     * @return
     */
    public boolean isAllowItemClick() {
        return isAllowItemClick;
    }

}
