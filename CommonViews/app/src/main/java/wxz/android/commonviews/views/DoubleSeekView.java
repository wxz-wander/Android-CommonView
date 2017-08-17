package wxz.android.commonviews.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import wxz.android.commonviews.R;


/**
 * Created by wxz11 on 2017/8/16.
 * 双向拖动选择控件
 */

public class DoubleSeekView extends View {

    private int seekBgColor;
    private int seekPbColor;
    private Drawable ballDrawable;
    private int seekSolidColor;
    private int seekStrokeColor;
    private int seekTextColor;
    private int seekTextSize;
    //是否可以左右都可以滑动
    private boolean isTwoDrag = true;
    private int currentBallMovingType;
    private Paint seekTextPaint;
    private Paint seekBgPaint;
    private Paint seekBallPaint;
    private Paint seekPbPaint;
    private Paint seekBallEndPaint;
    private Paint seekBallStrokePaint;
    private Bitmap ballBitmap;
    private int viewHeight;
    private int viewWidth;
    private int seekBallRadio;
    private static final float SEEK_BG_SCALE = 1.1F / 2;
    private static final float SEEK_TEXT_SCALE = 1.F / 3.5F;
    private static final int DEF_HEIGHT = 70;
    //private static final int DEF_PADDING = 40;
    private static final int BG_HEIGHT = 5;
    private static final int SEEK_STROKE_SIZE = 1;
    private int seekBallY;
    private int seekTextY;
    private double leftPosition;
    private int leftSeekBallX;
    private int rightSeekBallX;
    private double rightPosition;
    private RectF seekBGRectF;
    private RectF seekPbRectF;
    private int downX;
    private double leftPositonValue;
    private double rightPositonValue;

    public DoubleSeekView(Context context) {
        this(context, null);
    }

    public DoubleSeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        obtainAttrs(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        currentBallMovingType = BallPositionType.LEFT;
        seekTextPaint = createPaint(seekTextColor, seekTextSize, Paint.Style.FILL, 0);
        seekBgPaint = createPaint(seekBgColor, 0, Paint.Style.FILL, 0);
        seekBallPaint = createPaint(seekSolidColor, 0, Paint.Style.FILL, 0);
        seekPbPaint = createPaint(seekPbColor, 0, Paint.Style.FILL, 0);
        seekBallEndPaint = createPaint(seekPbColor, 0, Paint.Style.FILL, 0);
        seekBallStrokePaint = createPaint(seekStrokeColor, 0, Paint.Style.FILL, 0);
        seekBallStrokePaint.setShadowLayer(5, 2, 2, seekStrokeColor);
        ballBitmap = drawableToBitmap(ballDrawable);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        //获取drawable的宽高
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        //创建等大的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        //创建等大的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        //把drawble的内容画到画布上
        drawable.draw(canvas);
        return bitmap;
    }

    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int strokeWidth) {
        Paint paint = new Paint();
        paint.setStyle(style);
        paint.setAntiAlias(true);
        paint.setColor(paintColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    private void obtainAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleSeekView, defStyleAttr, R.style.def_seekview);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.DoubleSeekView_seek_bg_color:
                    seekBgColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.DoubleSeekView_seek_pb_color:
                    seekPbColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.DoubleSeekView_seek_ball_image:
                    ballDrawable = typedArray.getDrawable(attr);
                    break;
                case R.styleable.DoubleSeekView_seek_ball_solid_color:
                    seekSolidColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.DoubleSeekView_seek_ball_stroke_color:
                    seekStrokeColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.DoubleSeekView_seek_text_color:
                    seekTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.DoubleSeekView_seek_text_size:
                    seekTextSize = typedArray.getDimensionPixelSize(attr, 12);
                    break;
                case R.styleable.DoubleSeekView_seek_bar_isTwoDrag:
                    isTwoDrag = typedArray.getBoolean(attr, true);
                    break;
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(ballBitmap.getHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;

        if (ballBitmap != null) {
            seekBallRadio = ballBitmap.getWidth() / 2;
        } else {
            seekBallRadio = 30;
        }
        seekBallY = (int) (viewHeight * SEEK_BG_SCALE + BG_HEIGHT / 2.F);
        seekTextY = (int) (viewHeight * SEEK_TEXT_SCALE);
        if (leftPosition != 0) {
            leftSeekBallX = (int) (leftPosition * ((double) (viewWidth - seekBallRadio - seekBallRadio)) + seekBallRadio);
        } else {
            leftSeekBallX = seekBallRadio;
        }

        if (rightPosition != 0) {
            rightSeekBallX = (int) (rightPosition * ((double) (viewWidth - seekBallRadio - seekBallRadio)) + seekBallRadio);
        } else {
            rightSeekBallX = viewWidth - seekBallRadio;
        }

        seekBGRectF = new RectF(seekBallRadio, seekBallRadio - 2, viewWidth - seekBallRadio, seekBallRadio + 2);
        seekPbRectF = new RectF(leftSeekBallX, seekBallRadio - 2, rightSeekBallX, seekBallRadio + 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSeekBG(canvas);
        drawSeekPB(canvas);
        drawLeftCircle(canvas);
        drawRightCircle(canvas);
    }

    private void drawRightCircle(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        if (ballDrawable != null) {
            canvas.save();
            canvas.translate(rightSeekBallX - seekBallRadio, 0);
            canvas.drawBitmap(ballBitmap, 0, 0, null);
            canvas.restore();
        } else {
            canvas.drawCircle(rightSeekBallX, seekBallRadio, seekBallRadio, seekBallStrokePaint);
            canvas.drawCircle(rightSeekBallX, seekBallRadio, seekBallRadio, seekBallPaint);
        }
    }

    private void drawLeftCircle(Canvas canvas) {
        if (isTwoDrag) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            if (ballDrawable != null) {
                canvas.save();
                canvas.translate(leftSeekBallX - seekBallRadio, 0);
                canvas.drawBitmap(ballBitmap, 0, 0, null);
                canvas.restore();
            } else {
                canvas.drawCircle(leftSeekBallX, seekBallRadio, seekBallRadio, seekBallStrokePaint);
                canvas.drawCircle(leftSeekBallX, seekBallRadio, seekBallRadio, seekBallPaint);
            }
        }
    }

    private void drawSeekPB(Canvas canvas) {
        canvas.save();
        canvas.drawRect(seekPbRectF, seekPbPaint);
        canvas.restore();
    }

    private void drawSeekBG(Canvas canvas) {
        canvas.save();
        canvas.drawRect(seekBGRectF, seekBgPaint);
        canvas.restore();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DoubleSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPosition(int leftPositonValue, int rightPositonValue) {
        if (leftPositonValue != -1) {
            this.leftPositonValue = leftPositonValue;
        }
        if (rightPositonValue != -1) {
            this.rightPositonValue = rightPositonValue;
        }
    }

    public int getUnitWidth(int count) {
        return ((viewWidth - 2 - 2 * seekBallRadio) / count);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                currentBallMovingType = getMoveLeftOrRightBall(downX);
                if (currentBallMovingType == BallPositionType.RIGHT) {
                    rightSeekBallX = downX;
                } else {
                    leftSeekBallX = downX;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                if (leftSeekBallX == rightSeekBallX) {
                    if (moveX - downX > 0) {
                        currentBallMovingType = BallPositionType.RIGHT;
                        rightSeekBallX = moveX;
                    } else {
                        currentBallMovingType = BallPositionType.LEFT;
                        leftSeekBallX = moveX;
                    }
                } else {
                    if (currentBallMovingType == BallPositionType.LEFT) {
                        leftSeekBallX = leftSeekBallX - rightSeekBallX >= 0 ? rightSeekBallX : moveX;
                    } else {
                        rightSeekBallX = rightSeekBallX - leftSeekBallX < 0 ? leftSeekBallX : moveX;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onSeekDragFinished(leftPositonValue, rightPositonValue);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }

        if (currentBallMovingType == BallPositionType.LEFT) {
            if (leftSeekBallX <= seekBallRadio) {
                leftSeekBallX = seekBallRadio;
            } else if (leftSeekBallX >= viewWidth - seekBallRadio) {
                leftSeekBallX = viewWidth - seekBallRadio;
            }
        } else {
            if (rightSeekBallX <= seekBallRadio) {
                rightSeekBallX = seekBallRadio;
            } else if (rightSeekBallX >= viewWidth - seekBallRadio) {
                rightSeekBallX = viewWidth - seekBallRadio;
            }
        }

        leftPositonValue = ((double) (leftSeekBallX - seekBallRadio)) / ((double) (viewWidth - seekBallRadio * 2));
        rightPositonValue = ((double) (rightSeekBallX - seekBallRadio)) / ((double) (viewWidth - seekBallRadio * 2));
        if (listener != null) {
            listener.onSeekDraging(leftPositonValue, rightPositonValue);
        }
        seekPbRectF = new RectF(leftSeekBallX, seekBallRadio - 2, rightSeekBallX, seekBallRadio + 2);
        invalidate();
        return true;
    }

    private int getMoveLeftOrRightBall(int downX) {
        if (isTwoDrag) {
            return Math.abs(leftSeekBallX - downX) - Math.abs(rightSeekBallX - downX) >= 0 ? BallPositionType.RIGHT : BallPositionType.LEFT;
        } else {
            return BallPositionType.RIGHT;
        }
    }

    public SeekDragListener getListener() {
        return listener;
    }

    public void setListener(SeekDragListener listener) {
        this.listener = listener;
    }

    private class BallPositionType {
        public static final int LEFT = 2 << 2;
        public static final int RIGHT = 2 << 3;
    }

    private SeekDragListener listener;

    public interface SeekDragListener {
        void onSeekDraging(double leftValue, double rightValue);

        void onSeekDragFinished(double leftValue, double rightValue);
    }
}
