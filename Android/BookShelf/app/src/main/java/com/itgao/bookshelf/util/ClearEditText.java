package com.itgao.bookshelf.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;


import com.itgao.bookshelf.R;

import static android.view.View.OnFocusChangeListener;

/**
 * Created by Administrator on 2016/9/28.
 */
public class ClearEditText extends EditText implements OnFocusChangeListener,TextWatcher {

    // 删除按钮
    private Drawable mClearDrawable;
    // 控件是否有焦点
    private boolean isFocus;


    public ClearEditText(Context context) {
        this(context,null);
    }
    public ClearEditText(Context context, AttributeSet attrs){
        this(context,attrs,android.R.attr.editTextStyle);
    }
    public ClearEditText(Context context, AttributeSet attrs, int deftStyle){
        super(context,attrs,deftStyle);

    }

    public void init(){
        // 获取edittext的DrawableRight，如果没有就使用默认的
        mClearDrawable = getCompoundDrawables()[2];
        if(mClearDrawable == null){
            mClearDrawable = getResources().getDrawable(R.drawable.btn_ink_page_control_up);

        }
        mClearDrawable.setBounds(0,0,mClearDrawable.getIntrinsicWidth(),mClearDrawable.getIntrinsicHeight());
        setmClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);

    }

    /**
     * 获取清除控件的点击位置
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(getCompoundDrawables()[2]!=null){
                boolean touchable = event.getX() > (getWidth()-getTotalPaddingRight()) && (event.getX() <(getWidth()-getPaddingRight()));
                if(touchable){
                    this.setText("");

                }
            }

        }
        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化时 判断控件里的长度来设置清楚控件的隐藏和显示
     * @param view
     * @param b
     */
    @Override
    public void onFocusChange(View view, boolean b) {
        this.isFocus = b;
        if(b) {
            setmClearIconVisible(getText().length()>0);
        }else{
            setmClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(isFocus){
            setmClearIconVisible(getText().length()>0);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * 设置清楚图标的隐藏和显示 setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setmClearIconVisible(boolean visible){
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }
    /**
     * 设置抖动动画
     */
    public void setShakeAnimation(){
        this.setAnimation(shakeAnimation(5));
    }
    public static Animation shakeAnimation(int count){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(count));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
