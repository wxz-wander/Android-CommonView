package wxz.android.commonviews.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.List;

import wxz.android.commonviews.R;

import static android.R.attr.bitmap;
import static android.R.attr.includeFontPadding;
import static android.R.attr.src;
import static android.R.attr.theme;
import static android.R.id.list;

/**
 * Created by wxz11 on 2017/8/18.
 */

public class HexagonView extends View {

    private String text = "星星";
    private int textColor;
    private int textSize;
    private int bgColor;
    private Drawable drawable;
    private ScaleAnimation scaleAnimation;
    private ScaleAnimation endAnimation;
    private int viewWidth;
    private int viewHeight;
    private int viewCenter;
    private Paint paint;
    private Paint paint1;
    private Path path;
    private ShapeDrawable mDrawable;
    private List<PointF> points;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public HexagonView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public HexagonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #View(Context, AttributeSet)
     */
    public HexagonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(context, attrs);
        initAnimation();
    }

    private void init() {
        if (paint == null) {
            paint = new Paint();
        }
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgColor);
        paint.setAlpha(150);
        if (paint1 == null) {
            paint1 = new Paint();
        }
        paint1.setAntiAlias(true);
        if (text.length() > 4) {
            paint1.setTextSize(100 / text.length());
        } else {
            paint1.setTextSize(25);
        }
        paint1.setColor(textColor);
        paint1.setAlpha(255);
        paint1.setTextAlign(Paint.Align.CENTER);
    }

    private void initAnimation() {
        //缩放动画
        float start = 1.0f;
        float end = 0.9f;
        scaleAnimation = new ScaleAnimation(start, end, start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(30);
        scaleAnimation.setFillAfter(true);
        endAnimation = new ScaleAnimation(end, start, end, start,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        endAnimation.setDuration(30);
        endAnimation.setFillAfter(true);
    }

    private void obtainAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HexagonView);
        text = typedArray.getString(R.styleable.HexagonView_text);
        textColor = typedArray.getColor(R.styleable.HexagonView_textColor, Color.WHITE);
        textSize = typedArray.getDimensionPixelSize(R.styleable.HexagonView_textSize, 0);
        bgColor = typedArray.getColor(R.styleable.HexagonView_backColor, Color.BLACK);
        drawable = typedArray.getDrawable(R.styleable.HexagonView_src);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(86, MeasureSpec.EXACTLY);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewWidth = getWidth();
        viewHeight = getHeight();
        viewCenter = viewWidth / 2;
        double r30 = 30 * Math.PI / 180;
        float a = (float) (viewCenter * Math.sin(r30));
        float b = (float) (viewCenter * Math.cos(r30));
        float c = (viewHeight - 2 * b) / 2;
        init();
        //画六边形
        if (path == null) {
            path = new Path();
        }
        path.moveTo(viewWidth, viewHeight / 2);
        path.lineTo(viewWidth - a, viewHeight - c);
        path.lineTo(viewWidth - a - viewCenter, viewHeight - c);
        path.lineTo(0, viewHeight / 2);
        path.lineTo(a, c);
        path.lineTo(viewWidth - a, c);
        path.close();
        canvas.drawPath(path, paint);
//        if (drawable != null) {
//            paint.setAlpha(255);
//            //从drawable获取bitmap
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            //画背景图片矩阵
//            Matrix matrix = new Matrix();
//            matrix.postTranslate(viewWidth / 2 - bitmap.getWidth() / 2, viewHeight / 2 - bitmap.getHeight() / 2);
//            // 生成正六边形
//            mDrawable = new ShapeDrawable(new PathShape(path, viewWidth, viewHeight));
//            Shader aShader = new BitmapShader(zoomBitmap(bitmap, viewWidth,
//                    viewHeight), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//
//            mDrawable.getPaint().setShader(aShader); // 填充位图
//            mDrawable.setBounds(0, 0, viewWidth, viewHeight); // 设置边界尺寸
//            mDrawable.draw(canvas);
//
//            // 绘制图片
//            canvas.drawBitmap(bitmap, matrix, paint);
//        }
        if (text != null) {
            // 计算文字高度,处理垂直居中
            Paint.FontMetrics fontMetrics = paint1.getFontMetrics();
            // 计算文字高度
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            // 画字体
            if (drawable != null) {// 如果有图片文字在底部
                //Log.v(TAG, "src not null!");
                canvas.drawText(text, viewWidth / 2, 2 * b + c - 3, paint1);
            } else {// 没有图片时文字居于视图中央
                float textBaseY = viewHeight - (viewHeight - fontHeight) / 2
                        - fontMetrics.bottom;
                canvas.drawText(text, viewWidth / 2, textBaseY, paint1);
            }
        }
        //六边形顶点位置
        if (points == null) {
            points = new ArrayList<PointF>();
        } else {
            points.clear();
        }
        PointF pf = new PointF();
        pf.set(viewWidth, viewHeight / 2);
        points.add(pf);
        PointF pf1 = new PointF();
        pf1.set(viewWidth - a, viewHeight - c);
        points.add(pf1);
        PointF pf2 = new PointF();
        pf2.set(viewWidth - a - viewCenter, viewHeight - c);
        points.add(pf2);
        PointF pf3 = new PointF();
        pf3.set(0, viewHeight / 2);
        points.add(pf3);
        PointF pf4 = new PointF();
        pf4.set(a, c);
        points.add(pf4);
        PointF pf5 = new PointF();
        pf5.set(viewWidth - a, c);
        points.add(pf5);

    }

    public void setDrawable(Drawable drawable){
        this.drawable = drawable;
        invalidate();
    }

    public void setText(String text){
        this.text = text;
        invalidate();
    }


    /**
     * 一个点是否在外凸多边形内
     */
    public boolean containPoint(List<PointF> points, float x, float y) {
        int size = points.size();
        float[] px = new float[size];
        float[] py = new float[size];

        for (int i = 0; i < size; i++) {
            px[i] = points.get(i).x;
            py[i] = points.get(i).y;
        }
        boolean result = false;
        for (int i = 0, j = size - 1; i < size; j = i++) {
            if ((py[i] < y && py[j] >= y)
                    || (py[j] < y && py[i] >= y)) {
                if (px[i] + (y - px[i]) / (py[j] - py[i])
                        * (px[j] - px[i]) < x) {
                    result = !result;
                }
            }
        }
        return result;
    }

    /**
     * 按宽/高缩放图片到指定大小并进行裁剪得到中间部分图片
     *
     * @param bitmap 源bitmap
     * @param w      缩放后指定的宽度
     * @param h      缩放后指定的高度
     * @return 缩放后的中间部分图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidht, scaleHeight, x, y;
        Bitmap newbmp;
        Matrix matrix = new Matrix();
        if (width > height) {
            scaleWidht = ((float) h / height);
            scaleHeight = ((float) h / height);
            x = (width - w * height / h) / 2;// 获取bitmap源文件中x做表需要偏移的像数大小
            y = 0;
        } else if (width < height) {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = (height - h * width / w) / 2;// 获取bitmap源文件中y做表需要偏移的像数大小
        } else {
            scaleWidht = ((float) w / width);
            scaleHeight = ((float) w / width);
            x = 0;
            y = 0;
        }
        matrix.postScale(scaleWidht, scaleHeight);
        try {
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y,
                    (int) (width - x), (int) (height - y), matrix, true);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute or style resource. This constructor of View allows
     * subclasses to use their own base style when they are inflating.
     * <p>
     * When determining the final value of a particular attribute, there are
     * four inputs that come into play:
     * <ol>
     * <li>Any attribute values in the given AttributeSet.
     * <li>The style resource specified in the AttributeSet (named "style").
     * <li>The default style specified by <var>defStyleAttr</var>.
     * <li>The default style specified by <var>defStyleRes</var>.
     * <li>The base values in this theme.
     * </ol>
     * <p>
     * Each of these inputs is considered in-order, with the first listed taking
     * precedence over the following ones. In other words, if in the
     * AttributeSet you have supplied <code>&lt;Button * textColor="#ff000000"&gt;</code>
     * , then the button's text will <em>always</em> be black, regardless of
     * what is specified in any of the styles.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     * @see #View(Context, AttributeSet, int)
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HexagonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
