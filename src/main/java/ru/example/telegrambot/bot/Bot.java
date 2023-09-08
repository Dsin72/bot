package ru.example.telegrambot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// Аннотация @Component необходима, чтобы наш класс распознавался Spring, как полноправный Bean
@Component
// Наследуемся от TelegramLongPollingBot - абстрактного класса Telegram API
public class Bot extends TelegramLongPollingBot {
	// Аннотация @Value позволяет задавать значение полю путем считывания из application.properties
	@Value("${bot.name}")
	private String botUsername;

	@Value("${bot.token}")
	private String botToken;

	/* Перегружаем метод интерфейса LongPollingBot
    Теперь при получении сообщения наш бот будет отвечать сообщением Hi!
     */
	@Override
	public void onUpdateReceived(Update update) {
		try {
			SendMessage sm = new SendMessage();
			sm.setChatId(update.getMessage().getChatId());
			sm.setText("Hi!");

			execute(sm);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// Геттеры, которые необходимы для наследования от TelegramLongPollingBot
	public String getBotUsername() {
		return botUsername;
	}

	public String getBotToken() {
		return botToken;
	}
}