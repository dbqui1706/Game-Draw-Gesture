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

import com.google.gson.Gson;

import java.util.*;

import fit.nlu.service.GameWebSocketService;

public class DrawingView extends View {
    private static final float TOUCH_TOLERANCE = 4; // Sai số cho việc vẽ
    private GameWebSocketService webSocketService;
    private String roomId;
    private float mX, mY; // Tọa độ điểm cuối cùng của đường vẽ
    private Path mPath; // Đường vẽ
    private Paint mPaint; // Thuộc tính của nét vẽ
    private Canvas mCanvas; // Vùng vẽ
    private Bitmap mBitmap; // Hình ảnh vẽ
    private ArrayList<Path> paths;  // Lưu các đường vẽ để có thể undo
    private ArrayList<Paint> paints; // Lưu các thuộc tính của nét vẽ
    private boolean drawingEnabled;
    private Gson gson = new Gson();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paths = new ArrayList<>();
        paints = new ArrayList<>();
        setupDrawing();
    }

    public void setWebSocketService(GameWebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

        // Gửi event start
        if (webSocketService != null) {
            DrawingData drawingData = new DrawingData(
                    x, y, DrawingData.ActionType.START,
                    mPaint.getColor(), mPaint.getStrokeWidth()
            );
            String data = gson.toJson(drawingData);
            webSocketService.sendMessage("/app/room/" + roomId + "/draw", data);
        }
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

            // Gửi event move
            if (webSocketService != null) {
                DrawingData drawingData = new DrawingData(
                        x, y, DrawingData.ActionType.MOVE,
                        mPaint.getColor(), mPaint.getStrokeWidth()
                );
                String data = gson.toJson(drawingData);
                webSocketService.sendMessage("/app/room/" + roomId + "/draw", data);
            }
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

        // Gửi event up
        if (webSocketService != null) {
            DrawingData drawingData = new DrawingData(
                    mX, mY, DrawingData.ActionType.UP,
                    mPaint.getColor(), mPaint.getStrokeWidth()
            );
            String data = gson.toJson(drawingData);
            webSocketService.sendMessage("/app/room/" + roomId + "/draw", data);
        }
    }

    // Thêm method để xử lý drawing data từ người khác
    public void handleRemoteDrawing(DrawingData data) {
        DrawingData.ActionType action = data.getAction();
        switch (action) {
            case START:
                remoteDrawStart(data);
                break;
            case MOVE:
                remoteDrawMove(data);
                break;
            case UP:
                remoteDrawUp(data);
                break;
            case UNDO:
                remoteDrawUndo();
                break;
            case CLEAR:
                remoteDrawClear();
                break;
        }
        invalidate();
    }

    private void remoteDrawStart(DrawingData data) {
        mPath = new Path();
        mPath.moveTo(data.getX(), data.getY());
        mX = data.getX();
        mY = data.getY();
        mPaint.setColor(data.getColor());
        mPaint.setStrokeWidth(data.getStrokeWidth());
    }

    private void remoteDrawMove(DrawingData data) {
        mPath.quadTo(mX, mY, (data.getX() + mX) / 2, (data.getY() + mY) / 2);
        mX = data.getX();
        mY = data.getY();
    }

    private void remoteDrawUp(DrawingData data) {
        mPath.lineTo(data.getX(), data.getY());
        mCanvas.drawPath(mPath, mPaint);
        paths.add(new Path(mPath));
        paints.add(new Paint(mPaint));
        mPath = new Path();
    }


    private void remoteDrawUndo() {
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

    private void remoteDrawClear() {
        paths.clear();
        paints.clear();
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    /**
     * Phương thức này dùng để replay các sự kiện vẽ từ server
     * nếu như có sự kiện join room muộn hơn
     * @param events : Danh sách các sự kiện vẽ
     */
    public void replayDrawingEvents(List<DrawingData> events) {
        for (DrawingData event : events) {
            handleRemoteDrawing(event);
        }
        invalidate();
    }

    /**
     * onTouchEvent được gọi khi người dùng chạm vào view
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Nếu không cho phép vẽ, bỏ qua tất cả sự kiện touch
        if (!drawingEnabled) {
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        invalidate();
        return true;
    }

    /**
     * undo được gọi khi người dùng muốn xóa bước vẽ trước đó
     */
    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);

            // Vẽ lại các path còn lại
            mCanvas.drawColor(Color.WHITE);
            for (int i = 0; i < paths.size(); i++) {
                mCanvas.drawPath(paths.get(i), paints.get(i));
            }
            invalidate();

            // Gửi event undo
            if (webSocketService != null) {
                DrawingData drawingData = new DrawingData(
                        0, 0, DrawingData.ActionType.UNDO,
                        mPaint.getColor(), mPaint.getStrokeWidth()
                );
                String data = gson.toJson(drawingData);
                webSocketService.sendMessage("/app/room/" + roomId + "/draw", data);
            }
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

        // Gửi event clear
        if (webSocketService != null) {
            DrawingData drawingData = new DrawingData(
                    0, 0, DrawingData.ActionType.CLEAR,
                    mPaint.getColor(), mPaint.getStrokeWidth()
            );
            String data = gson.toJson(drawingData);
            webSocketService.sendMessage("/app/room/" + roomId + "/draw", data);
        }
    }

    /**
     * Phương thức này dùng để bật/tắt thao tác vẽ.
     * Khi allowDrawing = false, các sự kiện touch sẽ không tạo ra nét vẽ mới.
     */
    public void setDrawingEnabled(boolean allowDrawing) {
        this.drawingEnabled = allowDrawing;
    }
}
