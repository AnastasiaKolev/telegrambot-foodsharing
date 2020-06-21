package hackit.alerts;

import com.google.gson.Gson;
import hackit.dto.Item;
import hackit.dto.PostData;
import hackit.logger.Logger;
import hackit.model.User;
import hackit.service.PostService;
import hackit.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertsHandler {

    private static Logger LOGGER = new Logger("log/log-messages");

    private UserService userService = new UserService();

    private PostService postService = new PostService();

    private PostData postData = new PostData();

    protected AlertsHandler() {
        super();

        //users = Users.getInstance();
    }

    public void startAlertTimers() {
        final LocalDateTime localNow = LocalDateTime.now(Clock.systemUTC());

        TimerExecutor currentTimer = new TimerExecutor();
        currentTimer.startExecutionEveryDayAt(new CustomTimerTask() {
            @Override
            public void execute() throws Exception {
                sendAlerts();
            }
        }, localNow.getHour(), localNow.getMinute(), localNow.getSecond());
    }

    private void sendAlerts() throws Exception {
        List<User> allUsers = userService.findAll();
        if (allUsers == null) return;

        List<Item> newItems = postService.findNewPosts();
        if (newItems == null) return;

        for (User user : allUsers) {
            synchronized (Thread.currentThread()) {
                try {
                    Thread.currentThread().wait(35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            LOGGER.log("alert", user.getId(), "message", "alert on new update");

            List<Item> items = postService.findPostsByGeo("10", user.getLocation(), "5000");

            List<Item> posts = new ArrayList<>();

            for (Item post : items) {
                for (Item newPost : newItems) {
                    if (newPost.getGeoDataString().equals(post.getGeoDataString()))
                        posts.add(newPost);
                }
            }

            if (posts.isEmpty()) return;
            int counter = 0;
            StringBuilder post = new StringBuilder();
            if (items != null || !items.isEmpty()) {
                for (Item item : posts) {
                    int itemSize = item.getText().length();
                    if (itemSize < 150) {
                        post
                                .append("<b>")
                                .append(++counter)
                                .append(".</b> ")
                                .append(item.getText(), 0, itemSize - 1)
                                .append("...\n<b>Локация</b>: ")
                                .append(item.getGeoDataString())
                                .append("\n<b>Перейти к объявлению</b>: ")
                                .append(item.getLink())
                                .append("\n\n");
                    } else {
                        post
                                .append("<b>")
                                .append(++counter)
                                .append(".</b> ")
                                .append(item.getText(), 0, 149)
                                .append("...\n<b>Локация</b>: ")
                                .append(item.getGeoDataString())
                                .append("\n<b>Перейти к объявлению</b>: ")
                                .append(item.getLink())
                                .append("\n\n");
                    }
                }
                SendMessage postInfo = new SendMessage()
                        .setChatId(user.getChatId())
                        .setText(post.toString())
                        .enableHtml(true);

                executeAlert(postInfo);
            }
        }
    }

    public void executeAlert(SendMessage msg) {

    }
}