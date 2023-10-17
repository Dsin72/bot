package ru.example.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class ShowFunHandler implements Handler{

    public static final String SHOW_FUN = "/show_fun";
    public static final String SELECT_TEST = "/select_test";
    public static final String SHOW_TOUCH_GENERATOR = "/show_touch_generator";
    public static final String SHOW_CONVERSATION_GENERATOR = "/show_conversation_generator";
    public static final String BACK_TO_FUN_MENU = "/back_to_fun_menu";
    private final JpaUserRepository userRepository;

    public ShowFunHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        //Актуализируем состояние бота, на всякий пожарный
        if (!user.getBotState().equals(State.SHOW_FUN)){
            user.setBotState(State.SHOW_FUN);
            userRepository.save(user);
        }

        // Делаем две кнопки "Тестики" и "Генератор прикосновений"
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Тестики", SELECT_TEST)
                );
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(
                createInlineKeyboardButton("Генератор прикосновений", SHOW_TOUCH_GENERATOR),
                createInlineKeyboardButton("Поговорим?", SHOW_CONVERSATION_GENERATOR)
        );

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne,inlineKeyboardButtonsRowTwo));

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText("Что хочешь попробовать?");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.SHOW_FUN;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(SHOW_FUN,BACK_TO_FUN_MENU);
    }
}
