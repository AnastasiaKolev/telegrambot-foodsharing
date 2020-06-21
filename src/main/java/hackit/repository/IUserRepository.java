package hackit.repository;

import hackit.model.User;
import org.apache.ibatis.annotations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IUserRepository {
    
    @NotNull
    @Select("SELECT * FROM app_user;")
    List<User> findAll() throws Exception;

    @Nullable
    @Select("SELECT * FROM app_user WHERE id = #{id};")
    User findOneById(@NotNull final String id) throws Exception;

    @Nullable
    @Select("SELECT * FROM app_user WHERE \"chatId\" = #{chatId};")
    User findByChatId(@NotNull final String chatId) throws Exception;

    @Insert("INSERT INTO app_user (\"id\", \"chatId\", \"userName\","
            + "\"firstName\", \"lastName\", \"location\") VALUES (#{id}, #{chatId},"
            + "#{userName}, #{firstName}, #{lastName}, #{location});")
    void persist(@NotNull final User user) throws Exception;

    @Update("UPDATE app_user SET \"chatId\" = #{chatId}, \"userName\" = #{userName},"
            + "\"firstName\" = #{firstName}, \"lastName\" = #{lastName},"
            + "\"location\" = #{location} WHERE \"id\" = #{id};")
    void merge(@NotNull final User user) throws Exception;

    @Delete("DELETE FROM app_user WHERE id = #{id};")
    void remove(@NotNull final String id) throws Exception;

    @Delete("DELETE FROM app_user;")
    void removeAll() throws Exception;
}
