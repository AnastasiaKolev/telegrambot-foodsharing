package hackit.repository;

import hackit.model.User;
import org.apache.ibatis.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IUserRepository {
    
    @NotNull
    @Select("SELECT * FROM telegrambot.app_user;")
    @Results({
            @Result(property = "passwordHash", column = "password_hash"),
            @Result(property = "roleType", column = "role_type"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "middleName", column = "middle_name")
    })
    List<User> findAll() throws Exception;

    @Nullable
    @Select("SELECT * FROM telegrambot.app_user WHERE id = #{id};")
    @Results({
            @Result(property = "passwordHash", column = "password_hash"),
            @Result(property = "roleType", column = "role_type"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "middleName", column = "middle_name")
    })
    User findOneById(@NotNull final String id) throws Exception;

    @Nullable
    @Select("SELECT * FROM app_user WHERE \"chatId\" = #{chatId};")
//    @Results({
//            @Result(property = "passwordHash", column = "password_hash"),
//            @Result(property = "roleType", column = "role_type"),
//            @Result(property = "firstName", column = "first_name"),
//            @Result(property = "lastName", column = "last_name"),
//            @Result(property = "middleName", column = "middle_name")
//    })
    User findByChatId(@NotNull final String chatId) throws Exception;

    @Insert("INSERT INTO app_user (\"id\", \"chatId\", \"userName\", "
            + "\"firstName\", \"lastName\", \"location\") VALUES (#{id}, #{chatId}, "
            + "#{userName}, #{firstName}, #{lastName}, #{location});")
    void persist(@NotNull final User user) throws Exception;

    @Update("UPDATE telegrambot.app_user SET login = #{login}, "
            + "password_hash = #{passwordHash}, role_type = #{roleType}, email = #{email}, "
            + "first_name = #{firstName}, last_name = #{lastName}, middle_name = #{middleName},"
            + " phone = #{phone}, locked = #{locked} WHERE id = #{id};")
    void merge(@NotNull final User user) throws Exception;

    @Delete("DELETE FROM telegrambot.app_user WHERE id = #{id};")
    void remove(@NotNull final String id) throws Exception;

    @Delete("DELETE FROM telegrambot.app_user;")
    void removeAll() throws Exception;
}
