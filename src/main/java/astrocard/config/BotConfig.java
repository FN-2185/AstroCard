package astrocard.config;

import astrocard.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@PropertySource("file:C:\\Users\\Niko\\Desktop\\Projects\\AstroCard\\src\\main\\resources\\application.properties")
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;

    public BotConfig() {}

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
