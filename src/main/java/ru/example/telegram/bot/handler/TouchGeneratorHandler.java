package ru.example.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.Touch;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaTouchRepository;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class TouchGeneratorHandler implements Handler {
    public static final String SHOW_TOUCH_GENERATOR = "/show_touch_generator";
    public static final String TOUCH_SELECTED = "/touch_selected";
    public static final String TOUCH_SELECTED_SOFT = "/touch_selected_soft";
    public static final String TOUCH_SELECTED_HARD = "/touch_selected_hard";

    public static final String BACK_TO_FUN_MENU = "/back_to_fun_menu";

    private final JpaUserRepository userRepository;
    private final JpaTouchRepository touchRepository;

    public TouchGeneratorHandler(JpaUserRepository userRepository, JpaTouchRepository touchRepository) {
        this.userRepository = userRepository;
        this.touchRepository = touchRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (message.startsWith(TOUCH_SELECTED)) {
            return showTouch(user,message);
        }
        return showDescription(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showDescription(User user) {
        user.setBotState(State.PLAY_TOUCH_GENERATOR);
        userRepository.save(user);

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText("Тут должно быть внятное описание игры, но я разработчик, а не писатель. Ну или потом как-нибудь добавлю");
        sendMessage.setReplyMarkup(prepareGameButtons());

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showTouch(User user, String message) {
        Touch touch = new Touch();
        if (message.equalsIgnoreCase(TOUCH_SELECTED_SOFT)) {
            touch = touchRepository.getRandomSoftTouch();
        } else if (message.equalsIgnoreCase(TOUCH_SELECTED_HARD)) {
            touch = touchRepository.getRandomHardTouch();
        }

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText(touch.getTouch());
        sendMessage.setReplyMarkup(prepareGameButtons());

        return List.of(sendMessage);
    }

    private InlineKeyboardMarkup prepareGameButtons() {

        // Делаем кнопки "Нежнее", "Жестче" и "Вернуться в меню"
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Нежнее", TOUCH_SELECTED_SOFT),
                createInlineKeyboardButton("Жестче", TOUCH_SELECTED_HARD));
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = List.of(
                createInlineKeyboardButton("Вернуться в меню", BACK_TO_FUN_MENU));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne,inlineKeyboardButtonsRowTwo));

        return inlineKeyboardMarkup;
    }

    @Override
    public State operatedBotState() {
        return State.PLAY_TOUCH_GENERATOR;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(SHOW_TOUCH_GENERATOR, TOUCH_SELECTED_SOFT, TOUCH_SELECTED_HARD);
    }
}
