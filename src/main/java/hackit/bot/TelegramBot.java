package hackit.bot;

import hackit.commons.Callback;
import hackit.commons.Command;
import hackit.dto.Item;
import hackit.logger.Logger;
import hackit.service.PostService;
import hackit.service.UserService;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TelegramBot extends TelegramLongPollingBot {

    private static ResourceBundle authBundle = ResourceBundle.getBundle("auth/bot-config");

    private static Logger LOGGER = new Logger("log/log-messages");

    public String getBotUsername() {
        return authBundle.getString("bot-username");
    }

    public String getBotToken() {
        return authBundle.getString("bot-token");
    }

    private final PostService postService = new PostService();

    private final UserService userService = new UserService();

    private String mode = Command.START;

    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String incomingText = update.getMessage().getText();
            User sender = update.getMessage().getFrom();

            if (update.getMessage().hasLocation()) {
                LOGGER.log("incoming_location", sender.getId().toString(), "message", update.getMessage().getLocation().getLatitude().toString());
            }

            String chatId = update.getMessage().getChatId().toString();

            @Nullable hackit.model.User user = null;

            try {
                user = userService.findByChatId(chatId);
                if (user == null) {
                    user = new hackit.model.User(chatId);
                    user.setUserName(sender.getUserName());
                    user.setFirstName(sender.getFirstName());
                    user.setLastName(sender.getLastName());
                    userService.persist(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* manage commands */

            if (incomingText.startsWith(Command.START)) {

                onCommandStart(update);
                LOGGER.log("incoming_location", sender.getId().toString(), "message", update.getCallbackQuery().getMessage().getLocation().toString());

            } else if (incomingText.contains("Поделиться геолокацией")) {

                SendMessage location = sendMessage(update.getMessage(), "");
                locationKeyboard(location);

                LOGGER.log("incoming_location", sender.getId().toString(), "message", update.getCallbackQuery().getMessage().getLocation().toString());

            } else if (incomingText.contains("Проверить геолокацию")) {

                String locationCheck = user.getLocation();

                if (locationCheck == null) {
                    locationCheck = "Геолокация не указана.";
                }

                SendMessage location = sendMessage(update.getMessage(), "Локация: " + locationCheck);
                locationKeyboard(location);

            } else if (incomingText.startsWith(Command.HELP)) {

                onCommandHelp(update);

            } else if (incomingText.contains("Последние 5 объявлений")) {

                onCommandLastFive(update);

            } else if (incomingText.contains("По количеству объявлений в радиусе 5км")) {

                mode = Command.COUNT_INPUT;

                SendMessage location = sendMessage(update.getMessage(), "Введите количество объявлений, но не больше 30:");
                mainKeyboard(location);

            } else if (incomingText.contains("Только в радиусе 3км")) {

                mode = Command.NEAREST;

                SendMessage location = sendMessage(update.getMessage(), "Поиск ближайщих объявлений в радиусе 3км.");
                mainKeyboard(location);

            } else if (incomingText.contains("Ввести свой город и улицу")) {

                mode = Command.CITY_INPUT;

                SendMessage cityInput = sendMessage(update.getMessage(), "Пожалуйста, введите свой адрес в формате: "
                        + "Москва, ул. Студенческая или Санкт-Петербург метро Лесная");
                locationKeyboard(cityInput);

            } else if (incomingText.contains("Настройки геолокации")) {

                SendMessage settings = sendMessage(update.getMessage(), "В настройках можно указать геолокацию для поиска релевантных объявлений.");
                locationKeyboard(settings);

            } else if (incomingText.contains("Назад")) {

                SendMessage location = sendMessage(update.getMessage(), "Возврат в главное меню.");
                mainKeyboard(location);

                mode = Command.LAST_FIVE;

            } else if (incomingText.contains("Очистить")) {

                assert user != null;
                user.setLocation(null);
                try {
                    userService.merge(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SendMessage location = sendMessage(update.getMessage(), "Геолокация удалена.");
                locationKeyboard(location);

            } else if (mode.equals(Command.LAST_FIVE)) {

                onCommandLastFive(update);

            } else if (mode.equals(Command.COUNT_INPUT)) {

                assert user != null;
                if (user.getLocation() == null)
                    mode = Command.CITY_INPUT;
                else
                    onCommandLastCount(update, user.getLocation());

            } else if (mode.equals(Command.NEAREST)) {

                assert user != null;
                if (user.getLocation() == null)
                    mode = Command.CITY_INPUT;
                else
                    onCommandNearest(update, user.getLocation());

            } else if (mode.equals(Command.CITY_INPUT)) {

                assert user != null;
                user.setLocation(incomingText);
                try {
                    userService.merge(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SendMessage location = sendMessage(update.getMessage(), "Ура, теперь можно искать объявления неподалеку.");
                mainKeyboard(location);

            } else {
                LOGGER.log("incoming_update", sender.getId().toString(), "message", "none");
            }

        } else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            User sender = update.getCallbackQuery().getFrom();

            /* manage callbacks */
            if (callbackData.startsWith(Callback.FOO)) {

                onCallbackFoo(update);
            }
        }
    }

    /* command handlers */
    private void onCommandStart(Update update) {
        Message incomingMessage = update.getMessage();
        User sender = incomingMessage.getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "message", "onCommandStart");

        SendMessage greetings = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("Здравствуйте, " + sender.getUserName()
                        + ". Для поиска продуктов нам очень важно знать ваше местоположение!");

        locationKeyboard(greetings);
    }

    private void onCommandLastFive(Update update) {
        Message incomingMessage = update.getMessage();
        User sender = incomingMessage.getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "message", "onCommandLastTen");

        SendMessage replyMessage = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("*Последние 5 объявлений:*\n")
                .enableMarkdown(true);

        List<Item> items = postService.findLatestPosts();

        try {
            execute(replyMessage);
            int counter = 0;
            StringBuilder post = new StringBuilder();
            for (Item item : items) {
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
                    .setChatId(incomingMessage.getChatId())
                    .setText(post.toString())
                    .enableHtml(true);
            execute(postInfo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void onCommandLastCount(Update update, String location) {
        Message incomingMessage = update.getMessage();
        User sender = incomingMessage.getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "message", "onCommandLastCount");

        SendMessage replyMessage = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("*Последние объявления " + incomingMessage.getText() + "*:\n")
                .enableMarkdown(true);

        List<Item> items = postService.findPostsByGeo(incomingMessage.getText(), location, "5000");

        try {
            execute(replyMessage);
            int counter = 0;
            StringBuilder post = new StringBuilder();
            if (items != null || !items.isEmpty()) {
                for (Item item : items) {
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
                        .setChatId(incomingMessage.getChatId())
                        .setText(post.toString())
                        .enableHtml(true);
                execute(postInfo);
            } else {
                SendMessage noPosts = new SendMessage()
                        .setChatId(incomingMessage.getChatId())
                        .setText("Пока объявлений по запросу нет.")
                        .enableHtml(true);
                execute(noPosts);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void onCommandNearest(Update update, String location) {
        Message incomingMessage = update.getMessage();
        User sender = incomingMessage.getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "message", "onCommandNearest");

        SendMessage replyMessage = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("*Последние объявления*:\n")
                .enableMarkdown(true);

        List<Item> items = postService.findNearestPosts(location, "3000");

        try {
            execute(replyMessage);
            int counter = 0;
            StringBuilder post = new StringBuilder();
            if (items == null) return;
            for (Item item : items) {
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

                SendMessage postInfo = new SendMessage()
                        .setChatId(incomingMessage.getChatId())
                        .setText(post.toString())
                        .enableHtml(true);
                execute(postInfo);
            }
        } catch (
                TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void onCommandHelp(Update update) {
        Message incomingMessage = update.getMessage();
        User sender = incomingMessage.getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "message", "onCommandStart");

        SendMessage help = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("*По количеству объявлений в радиусе 5км*"
                        + "- Запрашивается количество объявления для выборки близких геолокаций раздачи еды"
                        + "\n*Последние 5 объявлений* "
                        + "- Все последние 5 объявлений без привязки к геолокации"
                        + "\n*Только в радиусе 3км*"
                        + "- Объявления близкие к указанной локации в Настройках"
                        + "\n*Настройки геолокации*"
                        + "- Меню настройки геолокации")
                .enableMarkdown(true);

        mainKeyboard(help);
    }

    /* callback handlers */
    private void onCallbackFoo(Update update) {

        Message originalMessage = update.getCallbackQuery().getMessage();
        String callbackData = update.getCallbackQuery().getData();
        User sender = update.getCallbackQuery().getFrom();

        LOGGER.log("incoming_update", sender.getId().toString(), "callback", "onCallbackFoo");

        /* this will edit the message from which the callback came from */
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup()
                .setChatId(String.valueOf(sender.getId()))
                .setMessageId(originalMessage.getMessageId());

        SendMessage replyMessage = new SendMessage()
                .setChatId(originalMessage.getChatId());

        //TODO: do stuff

        try {
            execute(editMessageReplyMarkup);
            execute(replyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    //main keyboard
    private void mainKeyboard(SendMessage message) {
        ReplyKeyboardMarkup mainReplyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> mainKeyboard = new ArrayList<>();

        KeyboardRow mainRow = new KeyboardRow();
        mainRow.add("По количеству объявлений в радиусе 5км");
        mainKeyboard.add(mainRow);

        KeyboardRow mainRow1 = new KeyboardRow();
        mainRow1.add("Последние 5 объявлений");
        mainRow1.add("Только в радиусе 3км");
        mainKeyboard.add(mainRow1);

        KeyboardRow mainRow2 = new KeyboardRow();
        mainRow2.add("Настройки геолокации");
        mainRow2.add("Помощь");
        mainKeyboard.add(mainRow2);

        mainReplyKeyboardMarkup.setKeyboard(mainKeyboard)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);

        message.setReplyMarkup(mainReplyKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //location keyboard
    private void locationKeyboard(SendMessage message) {
        ReplyKeyboardMarkup locationMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> locationKeyboard = new ArrayList<>();

        KeyboardRow mainRow = new KeyboardRow();
        mainRow.add(new KeyboardButton("Поделиться геолокацией")
                .setRequestLocation(true));
        mainRow.add(new KeyboardButton("Ввести свой город и улицу"));
        locationKeyboard.add(mainRow);

        KeyboardRow mainRow2 = new KeyboardRow();
        mainRow2.add(new KeyboardButton("Проверить геолокацию"));
        mainRow2.add(new KeyboardButton("Очистить"));
        mainRow2.add(new KeyboardButton("Назад"));
        locationKeyboard.add(mainRow2);

        locationMarkup.setKeyboard(locationKeyboard)
                .setOneTimeKeyboard(true)
                .setResizeKeyboard(true);

        message.setReplyMarkup(locationMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage sendMessage(Message message, String text) {
        return new SendMessage()
                .enableMarkdown(true)
                .setChatId(message.getChatId().toString())
                .setText(text);
    }

    /* operations to be executed not in response to an update */

    public void sendNotification() {
        //TODO: do stuff for example send a notification to some user
    }
}
