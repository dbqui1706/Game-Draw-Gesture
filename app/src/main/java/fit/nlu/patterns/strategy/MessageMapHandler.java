package fit.nlu.patterns.strategy;

import java.util.Map;

import fit.nlu.enums.MessageType;

public class MessageMapHandler {
    public static final Map<MessageType, MessageHandler> messageHandlers = Map.of(
            MessageType.PLAYER_LEAVE, new LeaveMessage(),
            MessageType.GAME_END, new GameEndMessage()
    );
    public static MessageHandler getHandler(MessageType type) {
        return messageHandlers.get(type);
    }
}
