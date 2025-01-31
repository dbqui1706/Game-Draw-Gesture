package fit.nlu.adapter.recycleview.room;

public class RoomItem {
    private String id;
    private int roomNumber;
    private int playerCount;
    private int capacity;

    public RoomItem(int roomNumber, int playerCount,int capacity) {
        this.roomNumber = roomNumber;
        this.playerCount = playerCount;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }
    public int getRoomNumber() {
        return roomNumber;
    }

    public int getPlayerCount() {
        return playerCount;
    }
    public int getCapacity() {
        return capacity;
    }
}
