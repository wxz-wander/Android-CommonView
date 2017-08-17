package wxz.android.commonviews.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import wxz.android.commonviews.R;

/**
 * Created by wxz11 on 2017/8/16.
 *开关控件
 */

public class SwitchButton extends FrameLayout {

    private Drawable openDrawable;
    private Drawable closeDrawable;
    private ImageView openView;
    private ImageView closeView;

    public SwitchButton(@NonNull Context context) {
        this(context, null);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(context, attrs);
    }

    private void obtainAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        openDrawable = typedArray.getDrawable(R.styleable.SwitchButton_switchOpenDrawable);
        closeDrawable = typedArray.getDrawable(R.styleable.SwitchButton_switchCloseDrawable);
        int switchStatus = typedArray.getInt(R.styleable.SwitchButton_switchStatus, 0);
        typedArray.recycle();
        openView = new ImageView(context);
        closeView = new ImageView(context);
        if (openDrawable != null) {
            openView.setImageDrawable(openDrawable);
        }
        if (closeDrawable != null) {
            closeView.setImageDrawable(closeDrawable);
        }
        addView(openView);
        addView(closeView);
        changeStatus(switchStatus);
    }

    public void setOpenDrawable(Drawable openDrawable) {
        this.openDrawable = openDrawable;
        openView.setImageDrawable(openDrawable);
    }

    public void setCloseDrawable(Drawable closeDrawable) {
        this.closeDrawable = closeDrawable;
        closeView.setImageDrawable(closeDrawable);
    }

    /**
     * @switchStatus 状态值
     */
    private void changeStatus(int switchStatus) {
        if (switchStatus == 1) {//开
            openView.setVisibility(VISIBLE);
            closeView.setVisibility(GONE);
        } else {
            openView.setVisibility(GONE);
            closeView.setVisibility(VISIBLE);
        }
    }

    public void openSwitch() {
        changeStatus(1);
    }

    public void closeSwitch() {
        changeStatus(0);
    }

    public boolean isWitchOpened() {
        return openView.getVisibility() == VISIBLE;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                if (openView.getVisibility() == GONE) {
                    openSwitch();
                } else {
                    closeSwitch();
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
