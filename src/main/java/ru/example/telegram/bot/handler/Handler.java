package ru.example.telegram.bot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ru.example.telegram.bot.State;
import ru.example.telegram.model.User;

import java.io.Serializable;
import java.util.List;

public interface Handler {
    // основной метод, который будет обрабатывать действия пользователя
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    // метод, который позволяет узнать, можем ли мы обработать текущий State у пользователя
    State operatedBotState();

    // метод, который позволяет узнать, какие команды CallBackQuery мы можем обработать в этом классе
    List<String> operatedCallBackQuery();
}
