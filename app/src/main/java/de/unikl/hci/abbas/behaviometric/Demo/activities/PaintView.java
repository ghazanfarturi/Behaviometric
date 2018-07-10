package de.unikl.hci.abbas.behaviometric.Demo.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;


public class PaintView extends View {
    private Path path;
    private Paint paint;

    public PaintView(Context context) {
        super(context);
        this.setFocusable(true);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.parseColor("#8EE5EE"));
//        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(e.getX(), e.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(e.getX(), e.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //path.close();
                invalidate();
                break;
        }
        return true;
    }
}
