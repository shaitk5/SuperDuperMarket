package chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMassage {
    private final String message;
    private final String username;
    private final String time;

    public ChatMassage(String message, String username) {
        this.message = message;
        this.username = username;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.time = myDateObj.format(myFormatObj);
    }

    public String getChatString() {
        return this.message;
    }

    public String getTime() {
        return this.time;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return (this.username != null ? this.username + ": " : "") + this.message;
    }
}
