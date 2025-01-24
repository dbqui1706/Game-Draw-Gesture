package fit.nlu.fagment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fit.nlu.canvas.DrawingView;
import fit.nlu.main.R;

public class FragmentDraw extends Fragment {
    private DrawingView drawingView;
    private ImageButton btnClear, btnUndo, btnPen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        // Khởi tạo các view
        drawingView = view.findViewById(R.id.drawingView);
        btnClear = view.findViewById(R.id.btnClear);
        btnUndo = view.findViewById(R.id.btnUndo);
        btnPen = view.findViewById(R.id.btnPen);

        // Xử lý sự kiện cho các nút
        setupButtons();

        // Áp dụng animation cho root view của fragment
        view.setTranslationY(-container.getHeight()); // Đặt vị trí ban đầu ở trên màn hình
        view.animate()
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return view;
    }
    private void setupButtons() {
        // Xử lý nút Clear
        btnClear.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận trước khi xóa
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa bản vẽ")
                    .setMessage("Bạn có chắc muốn xóa toàn bộ bản vẽ?")
                    .setPositiveButton("Xóa", (dialog, which) -> drawingView.clear())
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // Xử lý nút Undo
        btnUndo.setOnClickListener(v -> drawingView.undo());
    }
}
