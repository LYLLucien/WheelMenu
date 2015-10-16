package com.lucienli.wheelmenulib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucien.li on 2015/10/12.
 */
public class WheelMenu extends View {

    private final String TAG = WheelMenu.class.getSimpleName();
    private final String TAG_POSITION = "position";

    private boolean isDebug = true;
    private Paint paint;
    private static Bitmap myBitmap;
    private static Canvas bmpCanvas;

    private RectF oval;

    private int viewWidth, viewHeight;
    private float roundWidth = 220;
    /**
     * central coordinate
     */
    private int centre;

    /**
     * button gradient color
     */
    private int[] rightButtonColors = new int[]{0xff104E8B, 0xFF59A0E6, 0xff104E8B, 0xFF59A0E6};
    private int[] bottomButtonColors = new int[]{0xffFF7F50, 0xffFFB90F, 0xffFF7F50};
    private int[] leftButtonColors = new int[]{0xffEEC900, 0xffEEEE00, 0xffEEC900};
    private int[] topButtonColors = new int[]{0xff008B00, 0xff00EE00, 0xff008B00};

    /**
     * button position
     */
    private float[] rightButtonPosition = new float[]{0.125f, 0f, 0.875f, 1f};
    private float[] bottomButtonPosition = new float[]{0.125f, 0.25f, 0.375f};
    private float[] leftButtonPosition = new float[]{0.375f, 0.5f, 0.625f};
    private float[] topButtonPosition = new float[]{0.625f, 0.75f, 0.875f};

    private boolean isInitBitmap = true;

    /**
     * button color
     */
    private int rightButtonColor, bottomButtonColor, leftButtonColor, topButtonColor;
    /**
     * button shadow color
     */
    private int rightButtonShadow, bottomButtonShadow, leftButtonShadow, topButtonShadow;

    /**
     * icon resource
     */
    private Drawable icRightDrawable, icBottomDrawable, icLeftDrawable, icTopDrawable;

    /**
     * icon bitmap
     */
    private Bitmap icRightBitmap, icBottomBitmap, icLeftBitmap, icTopBitmap;
    /**
     * text resource
     */
    private String textRight, textBottom, textLeft, textTop;
    /**
     * text color
     */
    private int textColor, rightTextColor, bottomTextColor, leftTextColor, topTextColor;

    /**
     * cached darken colors
     */
    private Map<Position, int[]> cachedDarkenColors;


    private int padding, paddingRight, paddingBottom, paddingLeft, paddingTop;
    private int margin;

    private float textSize;

    private OnMenuClickListener listener;
    private Position lastPosition;

    public enum Position {
        Outside, Right, Bottom, Left, Top, Middle;

        public static String GetPositionName(Position position) {
            return String.valueOf(position);
        }
    }

    public interface OnMenuClickListener {

        void onClick(Position position);
    }


    public WheelMenu(Context context) {
        super(context);
        init(context, null);
    }

    public WheelMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WheelMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        cachedDarkenColors = new HashMap<>();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelMenu);
            // init button color
            rightButtonColor = a.getColor(R.styleable.WheelMenu_rightButtonColor, rightButtonColors[1]);
            bottomButtonColor = a.getColor(R.styleable.WheelMenu_bottomButtonColor, bottomButtonColors[1]);
            leftButtonColor = a.getColor(R.styleable.WheelMenu_leftButtonColor, leftButtonColors[1]);
            topButtonColor = a.getColor(R.styleable.WheelMenu_topButtonColor, topButtonColors[1]);

            rightButtonShadow = a.getColor(R.styleable.WheelMenu_rightButtonShadow, rightButtonColors[0]);
            bottomButtonShadow = a.getColor(R.styleable.WheelMenu_bottomButtonShadow, bottomButtonColors[0]);
            leftButtonShadow = a.getColor(R.styleable.WheelMenu_leftButtonShadow, leftButtonColors[0]);
            topButtonShadow = a.getColor(R.styleable.WheelMenu_topButtonShadow, topButtonColors[0]);

            // init icon resource
            icRightDrawable = a.getDrawable(R.styleable.WheelMenu_rightIconDrawable);
            icBottomDrawable = a.getDrawable(R.styleable.WheelMenu_bottomIconDrawable);
            icLeftDrawable = a.getDrawable(R.styleable.WheelMenu_leftIconDrawable);
            icTopDrawable = a.getDrawable(R.styleable.WheelMenu_topIconDrawable);

            // init text resource
            textRight = a.getString(R.styleable.WheelMenu_rightText);
            textBottom = a.getString(R.styleable.WheelMenu_bottomText);
            textLeft = a.getString(R.styleable.WheelMenu_leftText);
            textTop = a.getString(R.styleable.WheelMenu_topText);
            // init text color
            textColor = a.getColor(R.styleable.WheelMenu_textColor, Color.BLACK);
            rightTextColor = a.getColor(R.styleable.WheelMenu_rightTextColor, textColor);
            bottomTextColor = a.getColor(R.styleable.WheelMenu_bottomTextColor, textColor);
            leftTextColor = a.getColor(R.styleable.WheelMenu_leftTextColor, textColor);
            topTextColor = a.getColor(R.styleable.WheelMenu_topTextColor, textColor);

            textSize = a.getDimensionPixelSize(R.styleable.WheelMenu_buttonTextSize, 14);

            margin = a.getDimensionPixelOffset(R.styleable.WheelMenu_textMargin, 0);

            padding = a.getDimensionPixelOffset(R.styleable.WheelMenu_textPadding, 0);
            paddingRight = a.getDimensionPixelOffset(R.styleable.WheelMenu_textPaddingRight, padding);
            paddingBottom = a.getDimensionPixelOffset(R.styleable.WheelMenu_textPaddingBottom, padding);
            paddingLeft = a.getDimensionPixelOffset(R.styleable.WheelMenu_textPaddingLeft, padding);
            paddingTop = a.getDimensionPixelOffset(R.styleable.WheelMenu_textPaddingTop, padding);

            a.recycle();
            setButtonColors();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v(TAG, "onMeasure");
        int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightPixels = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (getWidth() < getHeight()) {
            int newWidthSpec = MeasureSpec.makeMeasureSpec(heightPixels, heightMode);
            super.onMeasure(newWidthSpec, heightMeasureSpec);
        } else {
            int newHeightSpec = MeasureSpec.makeMeasureSpec(widthPixels, widthMode);
            super.onMeasure(widthMeasureSpec, newHeightSpec);
        }
    }

    private void setButtonColors() {
        rightButtonColors = new int[]{rightButtonShadow, rightButtonColor, rightButtonShadow, rightButtonColor};
        bottomButtonColors = new int[]{bottomButtonShadow, bottomButtonColor, bottomButtonShadow};
        leftButtonColors = new int[]{leftButtonShadow, leftButtonColor, leftButtonShadow};
        topButtonColors = new int[]{topButtonShadow, topButtonColor, topButtonShadow};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw");
        viewWidth = getWidth();
        viewHeight = getHeight();
        roundWidth = viewWidth / 4;

        if (myBitmap == null) {
            myBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            bmpCanvas = new Canvas(myBitmap);
        }
        if (bmpCanvas != null) {
            initBitmapCanvas(isInitBitmap);
        }
        if (myBitmap != null) {
            paint.setAlpha(200);
            paint.setAntiAlias(true);
            canvas.drawBitmap(myBitmap, 0, 0, paint);

            drawIcon(canvas, icRightDrawable, true, Position.Right);
            drawIcon(canvas, icBottomDrawable, false, Position.Bottom);
            drawIcon(canvas, icLeftDrawable, true, Position.Left);
            drawIcon(canvas, icTopDrawable, false, Position.Top);
        }
    }

    /**
     * initialize bitmap canvas if flag is true
     *
     * @param isInitBitmap flag, which means need init bitmap or not
     */
    private void initBitmapCanvas(boolean isInitBitmap) {
        if (isInitBitmap) {
            if (isDebug)
                Log.d(TAG, "init bitmap canvas");
            /**
             * outside ring
             */
            centre = viewWidth / 2; //central coordinate
            int radius = (int) (centre - roundWidth / 2); //radius
//            paint.setColor(Color.CYAN); //set ring color
//            paint.setStyle(Paint.Style.STROKE); //set stroke
//            paint.setStrokeWidth(roundWidth); //set ring width
//            paint.setAntiAlias(true);  //anti-alias
//            paint.setDither(true);
//            paint.setShadowLayer(viewWidth / 2, 0, 0, Color.GRAY);
//            bmpCanvas.drawCircle(centre, centre, radius, paint); //draw ring
//

            oval = new RectF(centre - radius, centre - radius, centre
                    + radius, centre + radius);  //define shape, size and bound
            float rotate = 270f;
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.preRotate(rotate, viewWidth / 2, viewHeight / 2);
            // draw buttons
            drawButton(315, 90, rightButtonColors, rightButtonPosition, rotateMatrix);

            drawButton(45, 90, bottomButtonColors, bottomButtonPosition, null);

            drawButton(135, 90, leftButtonColors, leftButtonPosition, null);

            drawButton(225, 90, topButtonColors, topButtonPosition, null);

//            drawCentralButtons();

        }
    }

//    Bitmap middleBitmap;

//    private void drawCentralButtons() {
//
//        int radius = centre / 2;
//        if ((icBottomBitmap == null || icBottomBitmap.isRecycled()))
//            middleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_wheel_menu_middle);
//        RectF centralOval = new RectF(centre - radius, centre - radius, centre
//                + radius, centre + radius);
//
//        bmpCanvas.drawBitmap(middleBitmap, centre - radius, centre - radius, null);
//    }

    /**
     * draw button by start angle and degree
     *
     * @param startArg     start angle
     * @param degree       button degree
     * @param colors       button colors
     * @param position     color gradient position
     * @param rotateMatrix color rotate matrix
     */
    private synchronized void drawButton(int startArg, int degree, int[] colors, float[] position, Matrix rotateMatrix) {
        Shader sweepGradient = new SweepGradient(viewWidth / 2, viewHeight / 2, colors, position);
        int[] radialColors = new int[]{colors[1], colors[0]};

        float[] stop = new float[]{0.8f, 1.0f};
        Shader radialGradient = new RadialGradient(viewWidth / 2, viewHeight / 2,
                viewWidth / 2, radialColors, stop, Shader.TileMode.CLAMP);
        Shader shader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.MULTIPLY);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);
        if (rotateMatrix != null) {
            sweepGradient.setLocalMatrix(rotateMatrix);
        }
        paint.setShader(shader);
        bmpCanvas.drawArc(oval, startArg, degree, false, paint);
    }


    /**
     * draw icon inside button
     *
     * @param canvas     canvas which used to draw
     * @param drawable   icon drawable
     * @param isVertical is icon in vertical button
     * @param position   icon position
     */
    private synchronized void drawIcon(Canvas canvas, Drawable drawable, boolean isVertical, Position position) {
        int newWidth = 0, newHeight = 0;
        float[] iconPosition;
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Matrix matrix = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale;
            if (isVertical) {
                newWidth = viewWidth / 16 * 3;
                scale = ((float) newWidth) / width;
                newHeight = (int) (height * scale);
            } else {
                newHeight = viewHeight / 8;
                scale = ((float) newHeight) / height;
                newWidth = (int) (width * scale);
            }
            matrix.postScale(scale, scale);

            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, width, height, matrix, false);
            iconPosition = getIconPosition(newWidth, newHeight, position);
            paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            canvas.drawBitmap(resizedBitmap, iconPosition[0], iconPosition[1], paint);
        } else {
            iconPosition = getIconPosition(newWidth, newHeight, position);
        }

        drawText(canvas, getTextByPosition(position), iconPosition, newWidth, newHeight, position);
    }

    /**
     * get icon position in view
     *
     * @param width    icon width
     * @param height   icon height
     * @param position icon position
     * @return position array float[2]{left, top}
     */
    private float[] getIconPosition(int width, int height, Position position) {
        float compensateWidth = viewWidth / 64;
        float compensateHeight = 0;

        switch (position) {
            case Right:
                if (!TextUtils.isEmpty(textRight)) {
                    compensateHeight = viewHeight / 16;
                }
                return new float[]{
                        viewWidth / 8 * 7 - width / 2 - compensateWidth,
                        viewHeight / 2 - height / 2 - compensateHeight};
            case Bottom:
                if (!TextUtils.isEmpty(textBottom)) {
                    compensateHeight = viewHeight / 32;
                }
                return new float[]{
                        viewWidth / 2 - width / 2,
                        viewHeight / 8 * 7 - height / 2 - compensateHeight};
            case Left:
                if (!TextUtils.isEmpty(textLeft)) {
                    compensateHeight = viewHeight / 16;
                }
                return new float[]{
                        viewWidth / 8 - width / 2 + compensateWidth,
                        viewHeight / 2 - height / 2 - compensateHeight};
            case Top:
                if (!TextUtils.isEmpty(textTop)) {
                    compensateHeight = viewHeight / 32;
                }
                return new float[]{
                        viewWidth / 2 - width / 2,
                        viewHeight / 8 - height / 2 - compensateHeight
                };
            default:
                throw new IllegalArgumentException("No such Position Exception!");
        }
    }

    /**
     * draw button text
     *
     * @param canvas       canvas which used to draw
     * @param text         text on button
     * @param iconPosition icon drawable position, text is under icon
     * @param iconWidth    icon width
     * @param iconHeight   icon height
     */
    private synchronized void drawText(Canvas canvas, String text, float[] iconPosition,
                                       int iconWidth, int iconHeight, Position position) {
        if (!TextUtils.isEmpty(text)) {
            Paint textPaint = new Paint();
            textPaint.setTextSize(textSize);
            textPaint.setAntiAlias(true);

            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            float textWidth = textPaint.measureText(text);
            textPaint.setColor(Color.WHITE);
            canvas.drawRect(
                    (int) (iconPosition[0] + iconWidth / 2 - textWidth / 2 - paddingLeft),
                    (int) (iconPosition[1] + iconHeight + margin + metrics.top + textSize),
                    (int) (iconPosition[0] + iconWidth / 2 + textWidth / 2 + paddingRight),
                    (int) (iconPosition[1] + iconHeight + margin + paddingTop + paddingBottom + metrics.bottom + textSize),
                    textPaint
            );
            textPaint.setColor(getTextColorByPosition(position));
            canvas.drawText(text,
                    iconPosition[0] + iconWidth / 2 - textWidth / 2,
                    iconPosition[1] + iconHeight + margin + textSize + paddingTop,
                    textPaint);
        }
    }

    private int getTextColorByPosition(Position position) {
        switch (position) {
            case Right:
                return rightTextColor;
            case Bottom:
                return bottomTextColor;
            case Left:
                return leftTextColor;
            case Top:
                return topTextColor;
            default:
                return Color.BLACK;
        }
    }

    private String getTextByPosition(Position position) {
        switch (position) {
            case Right:
                return textRight;
            case Bottom:
                return textBottom;
            case Left:
                return textLeft;
            case Top:
                return textTop;
            default:
                return "";
        }
    }

    /**
     * get button position
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param d Diameter of the circle
     * @return button position
     */
    private Position getTouchPosition(float x, float y, float d) {
        if (Math.pow((x - d / 2), 2) + Math.pow((y - d / 2), 2) <= Math.pow(d / 2, 2) &&
                Math.pow((x - d / 2), 2) + Math.pow((y - d / 2), 2) >= Math.pow(d / 4, 2)) {
            if (x > y) {
                if (x + y < d) {
                    Log.v(TAG_POSITION, "top button");
                    return Position.Top;
                }
                Log.v(TAG_POSITION, "right button");
                return Position.Right;
            } else {
                if (x + y < d) {
                    Log.v(TAG_POSITION, "left button");
                    return Position.Left;
                }
                Log.v(TAG_POSITION, "bottom button");
                return Position.Bottom;
            }
        } else if (Math.pow((x - d / 2), 2) + Math.pow((y - d / 2), 2) < Math.pow(d / 4, 2)) {
            Log.v(TAG_POSITION, "middle");
            return Position.Middle;
        } else {
            Log.v(TAG_POSITION, "outside");
            return Position.Outside;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (myBitmap == null) {
                myBitmap = getBackGroundBitmap();
            }
            if (myBitmap == null) {
                return super.dispatchTouchEvent(event);
            }
            try {
                int pixel = myBitmap.getPixel((int) event.getX(), (int) event.getY());
                if (isDebug)
                    Log.d(TAG, "pixel: " + pixel);
                lastPosition = getTouchPosition(event.getX(), event.getY(), viewWidth);
                switch (lastPosition) {
                    case Right:
                        float rotate = 270f;
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.preRotate(rotate, viewWidth / 2, viewHeight / 2);
                        new PressButtonThread(lastPosition, 315, 90, rightButtonColors, rightButtonPosition, rotateMatrix).start();
                        break;
                    case Bottom:
                        new PressButtonThread(lastPosition, 45, 90, bottomButtonColors, bottomButtonPosition, null).start();
                        break;
                    case Left:
                        new PressButtonThread(lastPosition, 135, 90, leftButtonColors, leftButtonPosition, null).start();
                        break;
                    case Top:
                        new PressButtonThread(lastPosition, 225, 90, topButtonColors, topButtonPosition, null).start();
                        break;
                    default:
                        break;
                }
                if (pixel == 0) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isInitBitmap = true;
            float x = event.getX();
            float y = event.getY();
            Position curPosition = getTouchPosition(x, y, viewWidth);
            if (curPosition == lastPosition) {
                Log.d(TAG_POSITION, "Click " + Position.GetPositionName(curPosition));
                if (listener != null)
                    listener.onClick(curPosition);
            } else {
                Log.d(TAG_POSITION, "Cancel click");
            }
            postInvalidate();
        }
        return true;
    }

    /**
     * darken origin color by transfer to hsv
     *
     * @param color origin color
     * @return darken color
     */
    private int getDarkenColor(int color) {
        float[] hsvValue = new float[3];
        Color.colorToHSV(color, hsvValue);
        hsvValue[2] = 0.8f * hsvValue[2];
        return Color.HSVToColor(hsvValue);
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public void setTextTop(String textTop) {
        this.textTop = textTop;
    }

    public void setTextLeft(String textLeft) {
        this.textLeft = textLeft;
    }

    public void setTextBottom(String textBottom) {
        this.textBottom = textBottom;
    }

    public void setTextRight(String textRight) {
        this.textRight = textRight;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTopTextColor(int topTextColor) {
        this.topTextColor = topTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
    }

    public void setBottomTextColor(int bottomTextColor) {
        this.bottomTextColor = bottomTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }

//    public void setResIconTop(int icTopDrawable) {
//        this.icTopDrawable = icTopDrawable;
//    }
//
//    public void setResIconLeft(int icLeftDrawable) {
//        this.icLeftDrawable = icLeftDrawable;
//    }
//
//    public void setResIconBottom(int icBottomDrawable) {
//        this.icBottomDrawable = icBottomDrawable;
//    }
//
//    public void setResIconRight(int icRightDrawable) {
//        this.icRightDrawable = icRightDrawable;
//    }

    public void setTopButtonShadow(int topButtonShadow) {
        this.topButtonShadow = topButtonShadow;
    }

    public void setLeftButtonShadow(int leftButtonShadow) {
        this.leftButtonShadow = leftButtonShadow;
    }

    public void setBottomButtonShadow(int bottomButtonShadow) {
        this.bottomButtonShadow = bottomButtonShadow;
    }

    public void setRightButtonShadow(int rightButtonShadow) {
        this.rightButtonShadow = rightButtonShadow;
    }

    public void setTopButtonColor(int topButtonColor) {
        this.topButtonColor = topButtonColor;
    }

    public void setLeftButtonColor(int leftButtonColor) {
        this.leftButtonColor = leftButtonColor;
    }

    public void setBottomButtonColor(int bottomButtonColor) {
        this.bottomButtonColor = bottomButtonColor;
    }

    public void setRightButtonColor(int rightButtonColor) {
        this.rightButtonColor = rightButtonColor;
    }

    private class PressButtonThread extends Thread {
        private Position position;
        private int startArg;
        private int degree;
        private int[] colors;
        private float[] positions;
        private Matrix rotateMatrix;

        public PressButtonThread(Position position, int startArg, int degree,
                                 int[] colors, float[] positions, Matrix rotateMatrix) {
            this.position = position;
            this.colors = colors;
            this.startArg = startArg;
            this.degree = degree;
            this.positions = positions;
            this.rotateMatrix = rotateMatrix;
            isInitBitmap = false;
        }

        @Override
        public void run() {
            int[] darkenColors = cachedDarkenColors.get(position);
            if (darkenColors == null) {
                darkenColors = new int[colors.length];
                for (int i = 0; i < colors.length; i++) {
                    darkenColors[i] = getDarkenColor(colors[i]);
                }
                cachedDarkenColors.put(position, darkenColors);
            }
            drawButton(startArg, degree, darkenColors, positions, rotateMatrix);
            postInvalidate();
        }
    }


    private Bitmap getBackGroundBitmap() {
        Drawable drawable = getBackground();
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            Drawable.ConstantState constantState = stateListDrawable.getConstantState();
            if (constantState instanceof DrawableContainer.DrawableContainerState) {
                DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) constantState;
                Drawable[] drawables = drawableContainerState.getChildren();
                if (drawables == null || drawables.length < 1) {
                    return null;
                }
                for (Drawable tmpDrawable : drawables) {
                    if (tmpDrawable instanceof BitmapDrawable) {
                        return ((BitmapDrawable) tmpDrawable).getBitmap();
                    }
                }
            }
        }
        return null;
    }
}
