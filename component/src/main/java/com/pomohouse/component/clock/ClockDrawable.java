package com.pomohouse.component.clock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.pomohouse.component.R;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Cap.BUTT;
import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static android.util.Log.d;

/**
 * Created by sirawit on 15/07/2016.
 */

public class ClockDrawable extends Drawable implements Animatable {

    private final static int ANIMATION_DURATION = 500;

   /* private static final
    @ColorRes
    int FACE_COLOR = R.color.clock_default_hour;
    private static final
    @ColorRes
    int RIM_COLOR = R.color.clock_default_min;*/

    //private final Paint facePaint;
    private final Paint rimPaintMinute;
    private final Paint rimPaintHour;
    private final Paint rimPaintCircle;

    private final Paint rimPaintCircleTop;
    private final Paint rimPaintMinuteCircleTop;
    private final ValueAnimator minAnimator;
    private final ValueAnimator hourAnimator;

    private float faceRadius;
    private float screwRadius;

    private final Path hourHandPath;
    private final Path minuteHandPath;
    private final Path circleHandPath;
    private final Path circleTopHourHandPath;
    private final Path circleTopMinuteHandPath;

    private float remainingHourRotation = 0f;
    private float remainingMinRotation = 0f;

    private float targetHourRotation = 0f;
    private float targetMinRotation = 0f;

    private float currentHourRotation = 0f;
    private float currentMinRotation;

    private boolean hourAnimInterrupted;
    private boolean minAnimInterrupted;

    private LocalDateTime previousTime;

    private boolean animateDays = true;
    private
    @ColorRes
    int hourColor = R.color.default_circle_indicator_fill_color;
    private
    @ColorRes
    int minuteColor = R.color.default_circle_indicator_fill_color;

    public ClockDrawable(Resources resources, int hourColor, int minuteColor) {
        this.hourColor = hourColor;
        this.minuteColor = minuteColor;
        /*facePaint = new Paint(ANTI_ALIAS_FLAG);
        facePaint.setColor(Color.parseColor("#00000000"));
        facePaint.setStyle(FILL);*/

        rimPaintMinute = new Paint(ANTI_ALIAS_FLAG);
        rimPaintMinute.setColor(minuteColor);
        rimPaintMinute.setStyle(STROKE);
        rimPaintMinute.setStrokeCap(ROUND);
        rimPaintMinute.setStrokeWidth(resources.getDimension(R.dimen.clock_minute_stroke_width));

        rimPaintHour = new Paint(ANTI_ALIAS_FLAG);
        rimPaintHour.setColor(hourColor);
        rimPaintHour.setStyle(STROKE);
        rimPaintHour.setStrokeCap(ROUND);
        rimPaintHour.setStrokeWidth(resources.getDimension(R.dimen.clock_hours_stroke_width));

        rimPaintCircle = new Paint(ANTI_ALIAS_FLAG);
        rimPaintCircle.setColor(hourColor);
        rimPaintCircle.setStyle(FILL);
        rimPaintCircle.setStrokeCap(BUTT);
        //rimPaintCircle.setStrokeWidth(resources.getDimension(R.dimen.clock_circle_stroke_width));


        rimPaintCircleTop = new Paint(ANTI_ALIAS_FLAG);
        rimPaintCircleTop.setColor(hourColor);
        rimPaintCircleTop.setStyle(FILL);
        rimPaintCircleTop.setStrokeCap(ROUND);
        rimPaintCircleTop.setStrokeWidth(resources.getDimension(R.dimen.clock_circle_top_width));

        rimPaintMinuteCircleTop = new Paint(ANTI_ALIAS_FLAG);
        rimPaintMinuteCircleTop.setColor(hourColor);
        rimPaintMinuteCircleTop.setStyle(FILL);
        rimPaintMinuteCircleTop.setStrokeCap(ROUND);
        rimPaintMinuteCircleTop.setStrokeWidth(resources.getDimension(R.dimen.clock_circle_top_width));

        hourHandPath = new Path();
        minuteHandPath = new Path();
        circleHandPath = new Path();
        circleTopHourHandPath = new Path();
        circleTopMinuteHandPath = new Path();

        hourAnimator = ValueAnimator.ofFloat(0, 0);
        hourAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hourAnimator.setDuration(ANIMATION_DURATION);
        hourAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = (float) valueAnimator.getAnimatedValue();
                //d("ANIM", "Hfraction = " + fraction + ", remaining hour rotation = " + remainingHourRotation);
                remainingHourRotation = targetHourRotation - fraction;
                currentHourRotation = fraction;
                invalidateSelf();
            }
        });
        hourAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!hourAnimInterrupted) {
                    remainingHourRotation = 0f;
                }
                //i("ANIM", "END! remaining hour rotation = " + remainingHourRotation);
            }
        });


        minAnimator = ValueAnimator.ofFloat(0, 0);
        minAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        minAnimator.setDuration(ANIMATION_DURATION);
        minAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = (float) valueAnimator.getAnimatedValue();
                //d("ANIM", "Mfraction = " + fraction + ", remaining minute rotation = " + remainingMinRotation);
                remainingMinRotation = targetMinRotation - fraction;
                currentMinRotation = fraction;
                invalidateSelf();
            }
        });
        minAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!minAnimInterrupted) {
                    remainingMinRotation = 0f;
                }
                //i("ANIM", "END! remaining minute rotation = " + remainingMinRotation);
            }
        });

        previousTime = LocalDateTime.now().withTime(0, 0, 0, 0);
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        float rimRadius = Math.min(bounds.width(), bounds.height()) / 2f - rimPaintMinute.getStrokeWidth();
        faceRadius = rimRadius - rimPaintMinute.getStrokeWidth();
        screwRadius = rimPaintMinute.getStrokeWidth();
        float hourHandLength = (float) (0.35 * faceRadius);
        float minuteHandLength = (float) (0.7 * faceRadius);
        float top = bounds.centerY();

        hourHandPath.reset();
        hourHandPath.moveTo(bounds.centerX(), bounds.centerY());

        /*canvas.drawCircle(radius, radius, radius, mainPaint);
        canvas.drawCircle(right - radius, radius, radius, mainPaint);*/

        //hourHandPath.addRect(bounds.centerX(), top + hourHandLength, bounds.centerX(), top - hourHandLength, Direction.CCW);

        /*hourHandPath.addCircle(bounds.centerX(), top - hourHandLength, 4, Direction.CW);
        */

        hourHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - hourHandLength, Direction.CCW);
        hourHandPath.close();

        circleTopHourHandPath.reset();
        circleTopHourHandPath.moveTo(bounds.centerX(), bounds.centerY());
        circleTopHourHandPath.addCircle(bounds.centerX(), top - hourHandLength, 2.5f, Direction.CCW);
        circleTopHourHandPath.close();

        circleHandPath.reset();
        circleHandPath.moveTo(bounds.centerX(), bounds.centerY());
        circleHandPath.addCircle(bounds.centerX(), bounds.centerY(), 6, Direction.CCW);
        circleHandPath.close();

        minuteHandPath.reset();
        minuteHandPath.moveTo(bounds.centerX(), bounds.centerY());

        /*minuteHandPath.addCircle(bounds.centerX(), top - minuteHandLength, 4, Direction.CCW);*/
        minuteHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - minuteHandLength, Direction.CCW);
        minuteHandPath.close();

        circleTopMinuteHandPath.reset();
        circleTopMinuteHandPath.moveTo(bounds.centerX(), bounds.centerY());
        circleTopMinuteHandPath.addCircle(bounds.centerX(), top - minuteHandLength, 2.5f, Direction.CCW);
        circleTopMinuteHandPath.close();
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        // draw the outer rim of the clock
        //canvas.drawCircle(bounds.centerX(), bounds.centerY(), rimRadius, rimPaint);
        // draw the face of the clock
        //canvas.drawCircle(bounds.centerX(), bounds.centerY(), faceRadius, facePaint);
        // draw the little rim in the middle of the clock
        //canvas.drawCircle(bounds.centerX(), bounds.centerY(), screwRadius, rimPaintMinute);

        int saveCount = canvas.save();
        canvas.rotate(currentHourRotation, bounds.centerX(), bounds.centerY());
        // draw hour hand
        canvas.drawPath(hourHandPath, rimPaintHour);
        canvas.drawPath(circleTopHourHandPath, rimPaintCircleTop);
        canvas.restoreToCount(saveCount);

        saveCount = canvas.save();
        canvas.rotate(currentMinRotation, bounds.centerX(), bounds.centerY());
        // draw minute hand
        canvas.drawPath(minuteHandPath, rimPaintMinute);
        canvas.drawPath(circleTopMinuteHandPath, rimPaintMinuteCircleTop);
        canvas.restoreToCount(saveCount);

        saveCount = canvas.save();
        canvas.rotate(currentMinRotation, bounds.centerX(), bounds.centerY());
        canvas.drawPath(circleHandPath, rimPaintCircle);
        canvas.restoreToCount(saveCount);
        /*Paint centerCircle = new Paint();
        centerCircle.setColor(minuteColor);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), 4f, centerCircle);*/
    }

    @Override
    public void setAlpha(int alpha) {
        //rimPaint.setAlpha(alpha);
        //facePaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        //rimPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void start() {
        hourAnimInterrupted = false;
        minAnimInterrupted = false;
        hourAnimator.start();
        minAnimator.start();
    }

    public void setAnimateDays(boolean animateDays) {
        this.animateDays = animateDays;
    }

    public void start(LocalDateTime newTime) {
        int minDiff = getMinuteBetween(previousTime, newTime);
        // 60min ... 360grade
        // minDif .. minDelta
        float minDeltaRotation = ((float) minDiff * 360f) / 60f;
        // 720min ... 360grade = 12h ... 360grade
        // minDif ... hourDelta
        float hourDeltaRotation = ((float) minDiff * 360f) / 720f;

        remainingMinRotation += minDeltaRotation;
        remainingHourRotation += hourDeltaRotation;

        d("ANIM", "current hour rotation = " + currentHourRotation + ", current min rotation = " + currentMinRotation);

        if (isRunning()) {
            stop();
        }

        targetHourRotation = currentHourRotation + remainingHourRotation;
        hourAnimator.setFloatValues(currentHourRotation, targetHourRotation);

        targetMinRotation = currentMinRotation + remainingMinRotation;
        minAnimator.setFloatValues(currentMinRotation, targetMinRotation);

        start();

        previousTime = newTime;
    }

    @Override
    public void stop() {
        hourAnimInterrupted = true;
        minAnimInterrupted = true;
        hourAnimator.cancel();
        minAnimator.cancel();
    }

    @Override
    public boolean isRunning() {
        return hourAnimator.isRunning() || minAnimator.isRunning();
    }

    private int getMinuteBetween(LocalDateTime t1, LocalDateTime t2) {
        if (animateDays) {
            return Minutes.minutesBetween(t1, t2).getMinutes();
        }
        return Minutes.minutesBetween(t1, t2.withDate(t1.getYear(), t1.getMonthOfYear(), t1.getDayOfMonth())).getMinutes();
    }
}
