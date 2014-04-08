package com.techie.musicplayer.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author: larry.pham
 * @date: 2014.04.08
 * <p/>
 * Description:
 * Copyright (C) 2014 TechieDB Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FrameBlurView extends FrameLayout {

    private static final int DEFAULT_BLUR_RADIUS= 15;
    private static final int MAX_BLUR_RADIUS= 25;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur blurIntrinsic;

    private Bitmap mOriginalBackground;
    private Bitmap mBlurredBackground;
    private boolean parentDrawn = false;
    private Canvas blurredCanvas;

    private Allocation in;
    private Allocation out;
    private float mBlurRadius= 0;

    public FrameBlurView(Context context) {
        super(context);
        setUpBlureIntrinsic(context);
    }

    public FrameBlurView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setUpStylableAttributes(attrs);
    }

    public FrameBlurView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpStylableAttributes(attrs);
        setUpBlureIntrinsic(context);
    }

    private void setUpBlureIntrinsic(Context context){
        mRenderScript = RenderScript.create(context);
        blurIntrinsic = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    private void setUpStylableAttributes(AttributeSet attrs){
        TypedArray styles = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FrameBlurView, 0, 0);
        if (styles != null) {
            try {
                mBlurRadius = styles.getFloat(R.styleable.FrameBlurView_blurRadius, DEFAULT_BLUR_RADIUS);
                if (mBlurRadius > MAX_BLUR_RADIUS) {
                    throw new RuntimeException("Invalid blur radius must be 0 < blurRadius < 25");
                }
            } finally {
                styles.recycle();
            }
        }
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        createBitmaps();
    }

    protected void createBitmaps(){
        mOriginalBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mBlurredBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        in = Allocation.createFromBitmap(mRenderScript, mOriginalBackground);
        out= Allocation.createFromBitmap(mRenderScript, mBlurredBackground);
        blurredCanvas = new Canvas(mOriginalBackground);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onBlur(){
        blurIntrinsic.setRadius(mBlurRadius);
        blurIntrinsic.setInput(in);
        blurIntrinsic.forEach(out);
        out.copyTo(mBlurredBackground);
    }

    @Override
    public void draw(Canvas canvas) {
        View view = (View) getParent();
        if (parentDrawn){
            return;
        }
        parentDrawn = true;
        drawParentInBitmap(view);
        onBlur();
        canvas.drawBitmap(mBlurredBackground, 0, 0, null);
        super.draw(canvas);
        parentDrawn = false;
    }

    protected void drawParentInBitmap(View view){
        blurredCanvas.save();
        blurredCanvas.translate(-getLeft(), -getTop());
        view.draw(blurredCanvas);
        blurredCanvas.restore();
    }

    public void setBlurRadius(float blurRadius){
        this.mBlurRadius = blurRadius;
        invalidate();
    }

    public float getBlurRadius(){
        return this.mBlurRadius;
    }
}
