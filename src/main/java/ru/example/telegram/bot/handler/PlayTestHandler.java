package ru.example.telegram.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.Question;
import ru.example.telegram.model.Result;
import ru.example.telegram.model.Test;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaTestRepository;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ru.example.telegram.util.TelegramUtil.createInlineKeyboardButton;
import static ru.example.telegram.util.TelegramUtil.createMessageTemplate;

@Component
public class PlayTestHandler implements Handler {
    public static final String PLAY_TEST = "/play_test";
    public static final String ANSWER_TEST = "/answer_test";
    public static final String BACK_TO_TEST_MENU = "/back_to_test_menu";
    private static final List<String> OPTIONS = List.of("*0 баллов* — совсем не похоже на тебя",
            "*1 балл* — не очень похоже на тебя",
            "*2 балла* — отчасти похоже на тебя",
            "*3 балла* — похоже на тебя в значительной степени",
            "*4 балла* — описывает тебя совершенно точно");
    private final JpaUserRepository userRepository;
    private final JpaTestRepository testRepository;

    public PlayTestHandler(JpaUserRepository userRepository, JpaTestRepository testRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        int currentTestQuestion = user.getCurrentTestQuestion();
        Test test = testRepository.findById(user.getCurrentTest()).get();
        List<Question> questions = test.getQuestions();

        if (!user.getBotState().equals(State.PLAY_TEST)){
            user.setBotState(State.PLAY_TEST);
            userRepository.save(user);
        }

        //Если ивент содержал в себе ответ на тест. Увеличиваем текущий счет пользователя
        if (message.startsWith(ANSWER_TEST)) {
            Integer currentScore = user.getCurrentTestScore();
            Integer lastAnswer = Integer.valueOf(message.replace(ANSWER_TEST,""));
            user.setCurrentTestScore(currentScore + lastAnswer);
            userRepository.save(user);
        }
        System.out.println("questions.size() "+questions.size()+ " currentTestQuestion "+ currentTestQuestion);
        if (questions.size() > currentTestQuestion) {
            user.setCurrentTestQuestion(currentTestQuestion + 1);
            userRepository.save(user);

            return showQuestion(user, questions, currentTestQuestion);
        }
        return showResult(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showQuestion(User user,  List<Question> questions, int currentTestQuestion){

        Question question = questions.get(currentTestQuestion);
        List<String> options = new ArrayList<>(List.of( question.getOptionOne(), question.getOptionTwo(), question.getOptionThree(),  question.getOptionFour(),  question.getOptionFive()));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Рисуем строки кнопок
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowThree = new ArrayList<>();

        //Делаем красивенький текст
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('*')
                .append(question.getQuestion())
                .append("*\n\n");

        // Определяем внешний вид кнопок
        for (int i = 0; i < options.size(); i++) {
            InlineKeyboardButton button = createInlineKeyboardButton(options.get(i), ANSWER_TEST + options.get(i));

            if (i < 2) {
                inlineKeyboardButtonsRowOne.add(button);
            } else if (i < 4) {
                inlineKeyboardButtonsRowTwo.add(button);
            } else {
                inlineKeyboardButtonsRowThree.add(button);
            }

            stringBuilder.append(OPTIONS.get(i))
                    .append("\n");
        }

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne, inlineKeyboardButtonsRowTwo, inlineKeyboardButtonsRowThree));
        SendMessage sendMessage = createMessageTemplate(user);
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return  List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> showResult(User user) {
        List<Result> possibleResults = testRepository.findById(user.getCurrentTest()).get().getResults();
        int userScore = user.getCurrentTestScore();
        System.out.println(userScore);
        String userResultText = "";
        for (Result result : possibleResults) {
            if (userScore >= result.getFromScore() && userScore <= result.getToScore()){
                System.out.println("i was here");
                userResultText = "Твой результат: " + userScore + ". Это значит, что " +  result.getDescription();
                user.setCurrentTest(0);
                user.setCurrentTestQuestion(0);
                user.setCurrentTestScore(0);
                user.setBotState(State.SELECT_TEST);
                userRepository.save(user);
                break;
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        SendMessage resultMessage = createMessageTemplate(user);
        resultMessage.setText(userResultText);
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(createInlineKeyboardButton("Понял", BACK_TO_TEST_MENU));

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));
        resultMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(resultMessage);
    }

    @Override
    public State operatedBotState() {
        return State.PLAY_TEST;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(PLAY_TEST,ANSWER_TEST);
    }
}
