package fit.nlu.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class DrawingView extends View {
    private static final float TOUCH_TOLERANCE = 4; // Sai số cho việc vẽ

    private float mX, mY; // Tọa độ điểm cuối cùng của đường vẽ
    private Path mPath; // Đường vẽ
    private Paint mPaint; // Thuộc tính của nét vẽ
    private Canvas mCanvas; // Vùng vẽ
    private Bitmap mBitmap; // Hình ảnh vẽ
    private ArrayList<Path> paths;  // Lưu các đường vẽ để có thể undo
    private ArrayList<Paint> paints; // Lưu các thuộc tính của nét vẽ

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paths = new ArrayList<>();
        paints = new ArrayList<>();
        setupDrawing();
    }


    // Set up drawing cho view
    private void setupDrawing() {
        // Khởi tạo path và paint cho nét vẽ
        mPath = new Path();
        mPaint = new Paint();
        paints = new ArrayList<>();

        // Thiết lập paint mặc định
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    /**
     * onSizeChanged được gọi khi kích thước của view thay đổi
     *
     * @param w: Chiều rộng mới
     *           h: Chiều cao mới
     *           oldw: Chiều rộng cũ
     *           oldh: Chiều cao cũ
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Tạo bitmap với kích thước của view
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.WHITE); // Màu nền trắng
    }


    /**
     * onDraw được gọi khi view cần vẽ lại
     *
     * @param canvas : Vùng vẽ
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // Vẽ bitmap chứa các đường vẽ
        canvas.drawBitmap(mBitmap, 0, 0, null);
        // Vẽ đường đang được vẽ
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * touchStart được gọi khi người dùng chạm vào
     *
     * @param x : Tọa độ x
     * @param y : Tọa độ y
     */
    private void touchStart(float x, float y) {
        // Đặt path về vị trí bắt đầu
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * touchMove được gọi khi người dùng di chuyển ngón tay
     *
     * @param x : Tọa độ x
     * @param y : Tọa độ y
     */
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2); // Vẽ đường cong
            mX = x;
            mY = y;
        }
    }

    /**
     * touchUp được gọi khi người dùng nhấc ngón tay ra
     */
    private void touchUp() {
        mPath.lineTo(mX, mY);
        // Vẽ path vào canvas
        mCanvas.drawPath(mPath, mPaint);
        // Lưu lại path và paint để có thể undo
        paths.add(new Path(mPath));
        paints.add(new Paint(mPaint));
        // Tạo path mới cho lần vẽ tiếp theo
        mPath = new Path();
    }

    /**
     * onTouchEvent được gọi khi người dùng chạm vào view
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * undo được gọi khi người dùng muốn xóa bước vẽ trước đó
     */
    public void undo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);

            // Vẽ lại các path còn lại
            mCanvas.drawColor(Color.WHITE);
            for (int i = 0; i < paths.size(); i++) {
                mCanvas.drawPath(paths.get(i), paints.get(i));
            }
            invalidate();
        }
    }

    /**
     * clear được gọi khi người dùng muốn xóa toàn bộ vẽ
     */
    public void clear() {
        paths.clear();
        paints.clear();
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }
}
