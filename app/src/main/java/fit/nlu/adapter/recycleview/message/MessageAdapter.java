package fit.nlu.adapter.recycleview.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fit.nlu.adapter.recycleview.player.PlayerAdapter;
import fit.nlu.enums.MessageType;
import fit.nlu.main.R;
import fit.nlu.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;

    public MessageAdapter(Context context) {
        this.context = context;
        messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view mới từ layout item
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_message_rv, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        String messageContent = messageFactory(message);
        holder.tvMessage.setText(messageContent);
    }

    private String messageFactory(Message message) {
        MessageType type = message.getType();
        switch (type) {
            case GAME_START:
            case ROUND_START:
            case ROUND_END:
            case TURN_START:
            case TURN_END:
            case GAME_END:
            case CHAT:
                return message.getContent();
            case CREATE_ROOM:
                return message.getSender().getNickname() + " là chủ phòng";
            case UPDATE_OWNER:
                return message.getSender().getNickname() + " đã trở thành chủ phòng mới";
            case PLAYER_JOIN:
                return message.getSender().getNickname() + " đã tham gia phòng";
            case PLAYER_LEAVE:
                return message.getSender().getNickname() + " đã rời phòng";
            default:
                Log.e("MessageAdapter", "Unknown message type: " + type);
                return "Error";
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.item_chat_row);
        }
    }
}
