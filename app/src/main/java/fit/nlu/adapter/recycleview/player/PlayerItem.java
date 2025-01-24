package fit.nlu.adapter.recycleview.player;

public class PlayerItem {
    private String name;        // Tên người chơi
    private int score;          // Điểm số
    private String avatarUrl;   // URL của avatar (nếu có)

    // Constructor
    public PlayerItem(String name, int score, String avatarUrl) {
        this.name = name;
        this.score = score;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}