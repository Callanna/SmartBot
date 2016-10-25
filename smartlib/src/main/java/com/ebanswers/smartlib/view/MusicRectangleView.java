package com.ebanswers.smartlib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by TuoZhaoBing on 2016/4/12 0012.
 */
public class MusicRectangleView extends View {
    public static final String TAG = "MusicRectangleView";
    private Paint mPaint;
    public int mOffset = 10;
    public int mRectWidth ;
    public int mRectHeight ;
    private int mRectCount = 10;
    private float currentHeight = 0;
    private int mWidth,centerY;
    private RectF drawrectF;
    private LinearGradient mLinearGradient;

    public MusicRectangleView(Context context) {
        super(context);
    }

    public MusicRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint = new Paint();
        mWidth = getWidth();
        mRectHeight = getHeight();
        centerY = (getHeight()-getPaddingBottom()-getPaddingTop())/2;
        mRectWidth = (int)(mWidth*0.6/mRectCount);
        mLinearGradient = new LinearGradient(0,0,mRectWidth,mRectHeight, Color.YELLOW,Color.BLUE, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
        drawrectF = new RectF(0,0,0,mRectWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i=0;i<mRectCount;i++){
            //currentHeight = (float)(Math.random()*mRectHeight);
            drawrectF.left = (float) (mWidth*0.4/2+mRectWidth*i+mOffset);
            drawrectF.top = centerY-currentHeight/2;
            drawrectF.right = (float) (mWidth*0.4/2+mRectWidth*(i+1)+mOffset);
            drawrectF.bottom = centerY+currentHeight/2;
            canvas.drawRoundRect(drawrectF,5,5,mPaint);
            postInvalidateDelayed(300);
        }
    }
    public void setHeight(float height){
        currentHeight = height;
    }
}
