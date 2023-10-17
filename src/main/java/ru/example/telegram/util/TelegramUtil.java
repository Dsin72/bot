package ru.example.telegram.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.model.User;

public class TelegramUtil {
    public static SendMessage createMessageTemplate(User user) {
        return createMessageTemplate(String.valueOf(user.getChatId()));
    }

    // Creating template of SendMessage with enabled Markdown
    // Markdown - это разметка
    public static SendMessage createMessageTemplate(String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId); //who should get the message? the sender from which we got the message...
        sm.enableMarkdown(true);

        return sm;
    }

    // Creating button
    // Callback button. При нажатии на такую кнопку боту придёт апдейт. С созданием кнопки можно указать параметр,
    // который будет указан в этом апдейте (до 64 байтов). Обычно после нажатий на такие кнопки боты изменяют
    // исходное сообщение или показывают notification или alert.
    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(command);
        return button;
    }
}