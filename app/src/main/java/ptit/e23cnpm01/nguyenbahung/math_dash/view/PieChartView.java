package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

// Biểu đồ vành khuyên thắng / hòa / thua, tự vẽ bằng Canvas
public class PieChartView extends View {
    private int wins;
    private int draws;
    private int losses;
    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        arcPaint.setStyle(Paint.Style.STROKE);
        percentPaint.setTextAlign(Paint.Align.CENTER);
        percentPaint.setFakeBoldText(true);
        percentPaint.setColor(context.getColor(R.color.arcade_text_bright));
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setFakeBoldText(true);
        labelPaint.setColor(context.getColor(R.color.arcade_text_dim));
    }

    public void setData(int wins, int draws, int losses) {
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float stroke = size * 0.16f;
        float left = (getWidth() - size) / 2 + stroke / 2;
        float top = (getHeight() - size) / 2 + stroke / 2;
        oval.set(left, top, left + size - stroke, top + size - stroke);
        arcPaint.setStrokeWidth(stroke);

        int total = wins + draws + losses;
        if (total == 0) {
            arcPaint.setColor(getContext().getColor(R.color.arcade_btn_stroke));
            canvas.drawArc(oval, 0, 360, false, arcPaint);
        } else {
            // Từ đỉnh, theo chiều kim đồng hồ
            float start = -90;
            start = drawPart(canvas, start, wins, total, R.color.arcade_green);
            start = drawPart(canvas, start, draws, total, R.color.arcade_amber);
            drawPart(canvas, start, losses, total, R.color.arcade_red);
        }

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        percentPaint.setTextSize(size * 0.2f);
        labelPaint.setTextSize(size * 0.1f);
        String percent = total == 0 ? getContext().getString(R.string.label_no_time)
                : Math.round(wins * 100f / total) + "%";
        canvas.drawText(percent, cx, cy + size * 0.03f, percentPaint);
        canvas.drawText(getContext().getString(R.string.label_win_rate), cx, cy + size * 0.16f, labelPaint);
    }

    // Trả về góc bắt đầu của cung kế tiếp
    private float drawPart(Canvas canvas, float start, int count, int total, int colorRes) {
        if (count == 0) {
            return start;
        }
        float sweep = 360f * count / total;
        arcPaint.setColor(getContext().getColor(colorRes));
        canvas.drawArc(oval, start, sweep, false, arcPaint);
        return start + sweep;
    }
}
