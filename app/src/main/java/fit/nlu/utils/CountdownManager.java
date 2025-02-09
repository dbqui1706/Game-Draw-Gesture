package fit.nlu.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;


public class CountdownManager {
    private TextView countdownTextView;
    private Handler handler;
    private Runnable countdownRunnable;
    private int currentValue;
    private boolean isRunning = false;

    public CountdownManager(TextView textView) {
        this.countdownTextView = textView;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void startCountdown(final int startValue) {
        // Dừng countdown hiện tại nếu đang chạy
        stopCountdown();

        currentValue = startValue;
        isRunning = true;

        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                // Cập nhật UI trên main thread
                countdownTextView.setText(String.valueOf(currentValue));

                if (currentValue > 0) {
                    currentValue--;
                    // Lập lịch chạy lại sau 1 giây
                    handler.postDelayed(this, 1000);
                } else {
                    isRunning = false;
                    onCountdownFinished();
                }
            }
        };

        // Bắt đầu countdown
        handler.post(countdownRunnable);
    }

    public void updateTimeFromServer(int serverTime) {
        // Cập nhật thời gian từ server
        currentValue = serverTime;
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        if (countdownTextView != null) {
            countdownTextView.post(() -> countdownTextView.setText(String.valueOf(currentValue)));
        }
    }


    public void stopCountdown() {
        isRunning = false;
        if (handler != null && countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable);
        }
    }

    public void pauseCountdown() {
        if (isRunning) {
            stopCountdown();
        }
    }

    public void resumeCountdown() {
        if (!isRunning && currentValue > 0) {
            startCountdown(currentValue);
        }
    }

    public int getCurrentValue() {
        return currentValue;
    }

    private void onCountdownFinished() {
        // Override phương thức này để xử lý khi đếm ngược kết thúc
    }

    // Trong Activity hoặc Fragment
    public void cleanup() {
        stopCountdown();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}