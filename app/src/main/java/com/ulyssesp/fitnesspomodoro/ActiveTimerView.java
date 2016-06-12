package com.ulyssesp.fitnesspomodoro;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.google.auto.value.AutoValue;

public class ActiveTimerView extends View {
    private static final int COMPLETED_COLOR = 0xff00E676;
    private static final float CIRCLE_STROKE_MODIFIER = 1f / 80f;
    private static final int TEXT_SIZE_DP = 32;

    private Paint mCompletedPaint = new Paint();
    private Paint mCompletedCirclePaint = new Paint();
    private Paint mIncompletePaint = new Paint();
    private Paint mTextPaint = new Paint();
    private RectF mBounds;
    private Model mModel;

    @AutoValue
    abstract static class Model {
        abstract public Long timeRemaining();
        abstract public Float percentDone();
        abstract public Boolean paused();

        public static Model create(Long timeRemaining, Float percentDone, Boolean paused) {
            return new AutoValue_ActiveTimerView_Model(timeRemaining, percentDone, paused);
        }
    }

    public ActiveTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setWillNotDraw(false);

        mIncompletePaint.setColor(0xffE57373);
        mIncompletePaint.setStyle(Paint.Style.STROKE);

        mCompletedPaint.setColor(COMPLETED_COLOR);
        mCompletedPaint.setStyle(Paint.Style.STROKE);

        mCompletedCirclePaint.setColor(COMPLETED_COLOR);
        mCompletedCirclePaint.setStyle(Paint.Style.FILL);

        float textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DP,
                    context.getResources().getDisplayMetrics());

        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xff000000);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
      super.onMeasure(widthSpec, heightSpec);
      int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
      setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float strokeWidth = w * CIRCLE_STROKE_MODIFIER;
        float completedCircleStrokeWidth = strokeWidth * 1.5f;
        float margin = completedCircleStrokeWidth ;
        float circleWidth = w - 2 * margin;

        mBounds = new RectF(margin, margin, circleWidth + margin, circleWidth + margin);

        mIncompletePaint.setStrokeWidth(strokeWidth);
        mCompletedPaint.setStrokeWidth(strokeWidth);

        mCompletedCirclePaint.setStrokeWidth(completedCircleStrokeWidth);
    }

    public void update(Model model) {
        mModel = model;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mModel == null) {
            return;
        }

        float r = mBounds.width() / 2;

        canvas.drawCircle(r + mBounds.left, r + mBounds.top, r, mIncompletePaint);

        double a = 360 * Math.min(mModel.percentDone(), 1);
        float start = (float) -a - 90;
        float sweep = (float) a;
        canvas.drawArc(mBounds, start, sweep, false, mCompletedPaint);

        float xPos = r * (float) Math.cos(Math.toRadians(start));
        float yPos = r * (float) Math.sin(Math.toRadians(start));
        canvas.drawCircle(xPos + r + mBounds.left, yPos + r + mBounds.top,
                mCompletedCirclePaint.getStrokeWidth(), mCompletedCirclePaint);

        float secondsRemaining = Math.abs(mModel.timeRemaining() / 1000f);
        String timeSign = mModel.timeRemaining() > 0 ? "" : "-";

        String time =
            String.format("%02d:%02d",
                    (int) secondsRemaining / 60,
                    (int) secondsRemaining % 60
            );

        float ascentAdjust = (mTextPaint.ascent() + mTextPaint.descent()) * 0.5f;

        canvas.drawText( timeSign + time, mBounds.left + r,
                mBounds.top + r - ascentAdjust, mTextPaint );
    }
}
