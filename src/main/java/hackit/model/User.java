package hackit.model;

import org.jetbrains.annotations.NotNull;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "app_user")
public class User extends AbstractEntity {

    @Basic(optional = false)
    private Long chatId;

    public User() {
    }

    public User(@NotNull final Long chatId) {
        this.chatId = chatId;
    }

    @NotNull
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(@NotNull final Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", id='" + id + '\'' +
                '}';
    }
}
