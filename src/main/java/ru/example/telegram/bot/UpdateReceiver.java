package ru.example.telegram.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.example.telegram.bot.handler.Handler;
import ru.example.telegram.model.User;
import ru.example.telegram.repository.JpaUserRepository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class UpdateReceiver {
    // Храним доступные хендлеры в списке
    private final List<Handler> handlers;

    // Доступ в базу пользователей
    private final JpaUserRepository userRepository;

    public UpdateReceiver(List<Handler> handlers, JpaUserRepository userRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
    }

    // Обрабатываем полученный Update
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        // try-catch, чтобы при несуществующей команде просто возвращать пустой список
        try {
            // Проверяем, если Update - сообщение с текстом
            if (isMessageWithText(update)) {
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final long chatId = message.getFrom().getId();
                final String userName = message.getFrom().getUserName();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                // Как раз на случай нового пользователя мы и сделали конструктор с одним параметром в классе User
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId, userName)));

                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByState(user.getBotState()).handle(user, message.getText());

                // Аналогично для CallBackQuery
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getFrom().getId();
                final String userName = callbackQuery.getFrom().getUserName();
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId, userName)));

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(State state) {
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        System.out.println(query);
        Handler handler = handlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
        System.out.println(handler);
        return handler;
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}