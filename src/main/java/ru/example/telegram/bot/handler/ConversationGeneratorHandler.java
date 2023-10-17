package ru.example.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.Conversation;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaConversationRepository;
import ru.example.telegram.repository.JpaTouchRepository;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.List;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class ConversationGeneratorHandler implements Handler {

    public static final String SHOW_CONVERSATION_GENERATOR = "/show_conversation_generator";
    public static final String CONVERSATION_SELECTED = "/conversation_selected";

    public static final String BACK_TO_FUN_MENU = "/back_to_fun_menu";

    private final JpaUserRepository userRepository;
    private final JpaConversationRepository conversationRepository;

    public ConversationGeneratorHandler(JpaUserRepository userRepository, JpaTouchRepository touchRepository, JpaConversationRepository conversationRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (message.startsWith(CONVERSATION_SELECTED)) {
            return showTouch(user,message);
        }
        return showDescription(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showDescription(User user) {
        user.setBotState(State.PLAY_CONVERSATION_GENERATOR);
        userRepository.save(user);

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText("Тут должно быть внятное описание игры, но я разработчик, а не писатель. Ну или потом как-нибудь добавлю");
        sendMessage.setReplyMarkup(prepareGameButtons());

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showTouch(User user, String message) {
        Conversation question = conversationRepository.getRandomQuestion();

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText(question.getQuestion());
        sendMessage.setReplyMarkup(prepareGameButtons());

        return List.of(sendMessage);
    }

    private InlineKeyboardMarkup prepareGameButtons() {

        // Делаем кнопки
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Получить вопрос", CONVERSATION_SELECTED),
                createInlineKeyboardButton("Вернуться в меню", BACK_TO_FUN_MENU));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        return inlineKeyboardMarkup;
    }

    @Override
    public State operatedBotState() {
        return State.PLAY_CONVERSATION_GENERATOR;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(SHOW_CONVERSATION_GENERATOR,CONVERSATION_SELECTED);
    }
}
