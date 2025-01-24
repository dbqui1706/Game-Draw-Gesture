package fit.nlu.adapter.recycleview.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fit.nlu.main.R;

// RoomAdapter kế thừa từ RecyclerView.Adapter để quản lý danh sách phòng
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    // Danh sách các phòng sẽ được hiển thị
    private List<RoomItem> roomList;
    // Context của ứng dụng, cần thiết cho việc tạo view
    private Context context;

    // Constructor nhận vào danh sách phòng
    public RoomAdapter(List<RoomItem> roomList) {
        this.roomList = roomList;
    }

    // Phương thức này được gọi khi RecyclerView cần tạo một ViewHolder mới
    // ViewHolder đại diện cho giao diện của một phòng
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Tạo view từ file layout item_room.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    // Phương thức này được gọi để hiển thị dữ liệu tại một vị trí cụ thể
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        // Lấy thông tin phòng tại vị trí position
        RoomItem room = roomList.get(position);
        // Cập nhật giao diện với thông tin phòng
        holder.roomName.setText("Phòng " + room.getRoomNumber());
        holder.playerCount.setText(
                room.getPlayerCount() + "/" + room.getCapacity()
        );
    }

    // Trả về tổng số phòng trong danh sách
    @Override
    public int getItemCount() {
        return roomList != null ? roomList.size() : 0;
    }

    // RoomViewHolder đại diện cho giao diện của một phòng
    static class RoomViewHolder extends RecyclerView.ViewHolder {
        // Các thành phần giao diện của một phòng
        ImageView roomIcon;      // Biểu tượng phòng
        TextView roomName;       // Tên phòng
        TextView playerCount;    // Số lượng người chơi


        // ViewHolder liên kết các thành phần giao diện
        RoomViewHolder(View itemView) {
            super(itemView);
            // Tìm và gán các view từ layout
            roomIcon = itemView.findViewById(R.id.roomIcon);
            roomName = itemView.findViewById(R.id.roomName);
            playerCount = itemView.findViewById(R.id.playerCount);
        }
    }
}