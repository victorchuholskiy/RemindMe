package com.gmail.victorchuholskiy.todolist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.gmail.victorchuholskiy.todolist.R;


/**
 * Created by Admin on 03.01.2016.
 */
public class ViewCircle extends View {

    // Простая самадельная View (круг с/без границы)

    private Paint paint = new Paint();
    private Canvas canvas;
    private int color;
    private int colorBorder;
    private boolean withBorder = false;

    public ViewCircle(Context context) {
        super(context);
    }

    public ViewCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColorBorder(int colorBorder) {
        this.withBorder = true;
        this.colorBorder = colorBorder;
    }

    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(height, width) / 4;

        float center_x, center_y;
        center_x = width / 2;
        center_y = height / 2;

        final RectF oval = new RectF();
        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);

        paint.setAntiAlias(true);

        if (withBorder == true) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(20);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(colorBorder);
            canvas.drawOval(oval, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawOval(oval, paint);
    }
}
