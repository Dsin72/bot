package ru.example.telegram.bot.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.Test;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaTestRepository;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class StartHandler implements Handler {

    @Value("${bot.name}")
    private String botUsername;

    public static final String TEST_SELECTED = "/test_selected";
    public static final String SHOW_FUN = "/show_fun";
    private final JpaUserRepository userRepository;


    public StartHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {

        // Приветствуем пользователя
        SendMessage welcomeMessage = createMessageTemplate(user);
        welcomeMessage.setText(String.format(
                "Приветики! Я *%s*%nИ я умею всякие клевые штуки", botUsername
        ));

        //Показываем кнопку "Покажи"
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtons = List.of(createInlineKeyboardButton("Покажи", SHOW_FUN));
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtons));
        welcomeMessage.setReplyMarkup(inlineKeyboardMarkup);


        // Изменяем состояние на "выбирает тест"
        user.setBotState(State.SHOW_FUN);
        userRepository.save(user);

        return List.of(welcomeMessage);
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
