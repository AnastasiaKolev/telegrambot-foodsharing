package hackit.service;


import hackit.model.User;
import hackit.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    public UserService() {
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Optional
    public User findByChatId(final Long chatId) {
        if (chatId == null) return null;
        return userRepository.findByChatId(chatId);
    }

    public void persist(@Nullable final User user) {
        if (user == null) return;
        userRepository.save(user);
    }

    public void merge(@Nullable User user) {
        if (user == null) return;
        userRepository.save(user);
    }

    public void remove(@Nullable String id) {
        if (id == null || id.isEmpty()) return;
        @NotNull final User user = userRepository.getOne(id);
        userRepository.delete(user);
    }

    public void removeAll() {
        userRepository.deleteAll();
    }
}
