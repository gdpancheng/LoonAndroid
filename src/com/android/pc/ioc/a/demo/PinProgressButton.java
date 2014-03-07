package com.android.pc.ioc.a.demo;

/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.wash.activity.R;

/**
 * A two-state button that indicates whether some related content is pinned
 * (the checked state) or unpinned (the unchecked state), and the download
 * progress for this content.
 * <p>
 * See <a href="http://developer.android.com/design/building-blocks/progress.html#custom-indicators">Android
 * Design: Progress &amp; Activity</a> for more details on this custom
 * indicator.
 */
public class PinProgressButton extends CompoundButton {
    private int mMax;
    private int mProgress;

    private Drawable mShadowDrawable;
    private Drawable mUnpinnedDrawable;
    private Drawable mPinnedDrawable;

    private Paint mCirclePaint;
    private Paint mProgressPaint;
    private Rect mTempRect = new Rect();
    private RectF mTempRectF = new RectF();

    private int mDrawableSize;
    private int mInnerSize;

    public PinProgressButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PinProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PinProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mMax = 100;
        mProgress = 0;

        final Resources res = getResources();
        int circleColor = Color.parseColor("#80ffffff");
        int progressColor = Color.parseColor("#33b5e5");

        // Other initialization
        mPinnedDrawable = res.getDrawable(R.drawable.pin_progress_pinned);
        mPinnedDrawable.setCallback(this);
        mUnpinnedDrawable = res.getDrawable(R.drawable.pin_progress_unpinned);
        mUnpinnedDrawable.setCallback(this);
        mShadowDrawable = res.getDrawable(R.drawable.pin_progress_shadow);
        mShadowDrawable.setCallback(this);

        mDrawableSize = mShadowDrawable.getIntrinsicWidth();
        mInnerSize = 30;

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
    }

    /**
     * Returns the maximum download progress value.
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Sets the maximum download progress value. Defaults to 100.
     */
    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    /**
     * Returns the current download progress from 0 to max.
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Sets the current download progress (between 0 and max).
     * @see #setMax(int)
     */
    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                resolveSize(mDrawableSize, widthMeasureSpec),
                resolveSize(mDrawableSize, heightMeasureSpec));
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mPinnedDrawable.isStateful()) {
            mPinnedDrawable.setState(getDrawableState());
        }
        if (mUnpinnedDrawable.isStateful()) {
            mUnpinnedDrawable.setState(getDrawableState());
        }
        if (mShadowDrawable.isStateful()) {
            mShadowDrawable.setState(getDrawableState());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTempRect.set(0, 0, mDrawableSize, mDrawableSize);
        mTempRect.offset((getWidth() - mDrawableSize) / 2, (getHeight() - mDrawableSize) / 2);

        mTempRectF.set(-0.5f, -0.5f, mInnerSize + 0.5f, mInnerSize + 0.5f);
        mTempRectF.offset((getWidth() - mInnerSize) / 2, (getHeight() - mInnerSize) / 2);

        canvas.drawArc(mTempRectF, 0, 360, true, mCirclePaint);
        canvas.drawArc(mTempRectF, -90, 360 * mProgress / mMax, true, mProgressPaint);

        Drawable iconDrawable = isChecked() ? mPinnedDrawable : mUnpinnedDrawable;
        iconDrawable.setBounds(mTempRect);
        iconDrawable.draw(canvas);

        mShadowDrawable.setBounds(mTempRect);
        mShadowDrawable.draw(canvas);
    }

    /**
     * A {@link Parcelable} representing the {@link PinProgressButton}'s state.
     */
    public static class SavedState extends BaseSavedState {
        private int mProgress;
        private int mMax;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mProgress = in.readInt();
            mMax = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
            out.writeInt(mMax);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (isSaveEnabled()) {
            SavedState ss = new SavedState(superState);
            ss.mMax = mMax;
            ss.mProgress = mProgress;
            return ss;
        }
        return superState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        mMax = ss.mMax;
        mProgress = ss.mProgress;
    }
}
