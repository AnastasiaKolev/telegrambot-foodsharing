package hackit.bot;

import hackit.commons.Callback;
import hackit.commons.Command;
import hackit.dto.Item;
import hackit.service.UserService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import hackit.service.PostService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import hackit.persistence.PersistenceService;
import hackit.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    /* resource bundles to retrieve configurations and authentications for
     * Telegram API and any external hackit.service you may use */
    private static ResourceBundle authBundle = ResourceBundle.getBundle("auth/bot-config");

    /* a helper to log events */
    private static Logger LOGGER = new Logger("log/log-messages");

    /* services your hackit.bot may use */
    /* TODO: instantiate the hackit.service you actually implemented */
    private static PersistenceService persistenceService;

    /* ---------------------------------------------------- */

    public String getBotUsername() {
        return authBundle.getString("bot-username");
    }

    public String getBotToken() {
        return authBundle.getString("bot-token");
    }

    private PostService postService = new PostService();

    private UserService userService = new UserService();

    /* basically this is the only method your hackit.bot will call.
     * The design proposed below turns it into a simple switcher, just recognizing
     * the meaning of the update and delegating its handling to specific methods
     */
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String incomingText = update.getMessage().getText();
            User sender = update.getMessage().getFrom();

            final long chatId = update.getMessage().getChatId();

            @Nullable hackit.model.User user = userService.findByChatId(chatId);

            if (user == null) {
                user = new hackit.model.User(chatId);
                userService.persist(user);
            }

            /* manage commands */

            if (incomingText.startsWith(Command.START)) {

                onCommandStart(update);
                mainKeyboard();

            } else if (incomingText.contains("Поделиться геолокацией")) {

                locationKeyboard();

            } else if (incomingText.startsWith(Command.HELP)) {

                onCommandHelp(update);

            } else if (incomingText.startsWith(Command.LAST_TEN)) {

                onCommandHelp(update);

            } else {

                /* do nothing... or do something else */
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

        SendMessage replyMessage = new SendMessage()
                .setChatId(incomingMessage.getChatId())
                .setText("Здравствуйте, " + sender.getUserName()
                        + ". Для поиска продуктов нам очень важно знать ваше местоположение!\nПоследние доступные объявления:\n");

        List<Item> items = postService.findLatestPosts();

        //TODO: do stuff

        try {
            execute(replyMessage);
            int counter = 0;
            for (Item item : items) {
                StringBuilder post = new StringBuilder();
                int itemSize = item.getText().length();
                if (itemSize < 150) {
                    post
                            .append(++counter)
                            .append(". ")
                            .append(item.getText(), 0, itemSize - 1)
                            .append("\n");
                } else {
                    post
                            .append(++counter)
                            .append(". ")
                            .append(item.getText(), 0, 149)
                            .append("\n");
                }
                SendMessage postInfo = new SendMessage()
                        .setChatId(incomingMessage.getChatId())
                        .setText(post.toString());
                execute(postInfo);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void onCommandHelp(Update update) {
        //TODO: do stuff
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
    private void mainKeyboard() {
        ReplyKeyboardMarkup mainReplyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> mainKeyboard = new ArrayList<>();

        KeyboardRow mainRow1 = new KeyboardRow();
        mainRow1.add("Последние 10 постов");
        mainRow1.add("Только за сегодня");
        mainRow1.add("За 3 дня");
        mainKeyboard.add(mainRow1);

        KeyboardRow mainRow2 = new KeyboardRow();
        mainRow2.add("Поделиться геолокацией");
        //mainRow2.add("Ввести геолокацию(город, улица)");
        mainRow2.add("Помощь");
        mainKeyboard.add(mainRow2);

        mainReplyKeyboardMarkup.setKeyboard(mainKeyboard)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);
    }

    //location keyboard
    private void locationKeyboard() {
        ReplyKeyboardMarkup locationMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> locationKeyboard = new ArrayList<>();

        KeyboardRow mainRow = new KeyboardRow();
        mainRow.add(new KeyboardButton("Поделиться геолокацией")
                .setRequestLocation(true));
        mainRow.add(new KeyboardButton("Отмена"));
        locationKeyboard.add(mainRow);

        locationMarkup.setKeyboard(locationKeyboard)
                .setOneTimeKeyboard(true)
                .setResizeKeyboard(true);
    }


    /* response to plain text */

    private void onInsertData(Update update) {
        //TODO: do stuff
    }

    private void onInsertOtherData(Update update) {
        //TODO: do stuff
    }

    /* operations to be executed not in response to an update */

    public void sendNotification() {
        //TODO: do stuff for example send a notification to some user
    }

}
