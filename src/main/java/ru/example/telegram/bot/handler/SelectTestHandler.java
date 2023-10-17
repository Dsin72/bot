package ru.example.telegram.bot.handler;

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
import java.util.Optional;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class SelectTestHandler implements Handler {

    public static final String SELECT_TEST = "/select_test";
    public static final String TEST_SELECTED = "/test_selected";
    public static final String PLAY_TEST = "/play_test";
    public static final String BACK_TO_TEST_MENU = "/back_to_test_menu";
    public static final String BACK_TO_FUN_MENU = "/back_to_fun_menu";
    private final JpaUserRepository userRepository;
    private final JpaTestRepository testRepository;

    public SelectTestHandler(JpaUserRepository userRepository, JpaTestRepository testRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Проверяем тип полученного события
        if (message.startsWith(TEST_SELECTED)) {
            int testId = Integer.valueOf(message.replace(TEST_SELECTED,""));
            return showDescription(user,testId);
        } else if (message.equalsIgnoreCase(SELECT_TEST) || message.equalsIgnoreCase(BACK_TO_TEST_MENU)) {
            return select(user);
        }
        return select(user);

    }

    private List<PartialBotApiMethod<? extends Serializable>> showDescription(User user, int testId) {
        user.setCurrentTest(testId);
        Test test = testRepository.findById(testId).get();

        userRepository.save(user);

        // Делаем две кнопки "Поехали" и "Вернуться"
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("Поехали", PLAY_TEST),createInlineKeyboardButton("Передумал", BACK_TO_TEST_MENU));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText(test.getDescription());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> select(User user) {

        // Предлагаем выбрать тест
        SendMessage chooseTestMessage = createMessageTemplate(user);
        chooseTestMessage.setText("Тыкай на название, которое нравится больше, чтобы прочитать описание");

        // Подгружаем доступные тесты
        List<Test> tests = testRepository.findAll();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowThree = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowFour = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowFive = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowSix = new ArrayList<>();
        int index = 0;

        for (Test test : tests) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(test.getLabel());
            button.setCallbackData(TEST_SELECTED + test.getId().toString());

            if (index == 0) {
                inlineKeyboardButtonsRowOne.add(button);
            } else if (index == 1) {
                inlineKeyboardButtonsRowTwo.add(button);
            } else if (index == 2){
                inlineKeyboardButtonsRowThree.add(button);
            } else if (index == 3){
                inlineKeyboardButtonsRowFour.add(button);
            } else if (index == 4){
                inlineKeyboardButtonsRowFive.add(button);
            } else if (index == 5){
                inlineKeyboardButtonsRowSix.add(button);
            }
            index++;
        }

        List<InlineKeyboardButton> inlineKeyboardButtonsRowSeven = List.of(createInlineKeyboardButton("Вернуться в меню", BACK_TO_FUN_MENU));
        // Инициализируем кнопочки
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne,inlineKeyboardButtonsRowTwo,inlineKeyboardButtonsRowThree,
                inlineKeyboardButtonsRowFour,inlineKeyboardButtonsRowFive,inlineKeyboardButtonsRowSix,inlineKeyboardButtonsRowSeven));

        chooseTestMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(chooseTestMessage);
    }

    @Override
    public State operatedBotState() {
        return State.SELECT_TEST;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(TEST_SELECTED, BACK_TO_TEST_MENU, SELECT_TEST);
    }
}
