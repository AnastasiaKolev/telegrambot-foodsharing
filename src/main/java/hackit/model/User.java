package hackit.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class User extends AbstractEntity {

    @NotNull
    private String chatId;

    @Nullable
    private String userName;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String location;

    public User() {
    }

    public User(@Nullable final String chatId) {
        super();
        this.chatId = chatId;
    }

    public @NotNull String getChatId() {
        return chatId;
    }

    public void setChatId(@NotNull final String chatId) {
        this.chatId = chatId;
    }

    public @Nullable String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable final String userName) {
        this.userName = userName;
    }

    public @Nullable String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nullable final String firstName) {
        this.firstName = firstName;
    }

    public @Nullable String getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable final String lastName) {
        this.lastName = lastName;
    }

    public @Nullable String getLocation() {
        return location;
    }

    public void setLocation(@Nullable final String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId='" + chatId + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", location='" + location + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
