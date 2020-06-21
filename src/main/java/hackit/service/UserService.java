package hackit.service;


import hackit.model.User;
import hackit.repository.IUserRepository;
import hackit.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class UserService {

    public @Nullable List<User> findAll() throws Exception {
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        @NotNull final List<User> list = userRepository.findAll();
        if (list.isEmpty()) return null;
        return list;
    }

    public @Nullable User findById(@Nullable String id) throws Exception {
        if (id == null || id.isEmpty()) return null;
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        @Nullable final User user = userRepository.findOneById(id);
        if (user == null) return null;
        return user;
    }

    @Nullable
    public final User findByChatId(@Nullable final String chatId) throws Exception {
        if (chatId == null) return null;
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        @Nullable final User user = userRepository.findByChatId(chatId);
        if (user == null) return null;
        return user;
    }

    public void persist(@Nullable final User user) throws Exception {
        if (user == null) return;
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        try {
            userRepository.persist(user);
            sql.commit();
        } catch (Exception e) {
            sql.rollback();
        } finally {
            sql.close();
        }
    }

    public void merge(@Nullable User user) throws Exception {
        if (user == null) return;
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        try {
            userRepository.merge(user);
            sql.commit();
        } catch (Exception e) {
            sql.rollback();
        } finally {
            sql.close();
        }
    }

    public void remove(@Nullable String id) throws Exception {
        if (id == null || id.isEmpty()) return;
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        try {
            userRepository.remove(id);
            sql.commit();
        } catch (Exception e) {
            sql.rollback();
        } finally {
            sql.close();
        }
    }

    public void removeAll() throws Exception {
        @NotNull final SqlSession sql = MyBatisUtil.getSqlSessionFactory().openSession();
        @NotNull final IUserRepository userRepository = sql.getMapper(IUserRepository.class);
        try {
            userRepository.removeAll();
            sql.commit();
        } catch (Exception e) {
            sql.rollback();
        } finally {
            sql.close();
        }
    }
}
