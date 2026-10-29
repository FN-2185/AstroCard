package astrocard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {
    @Bean
    @ConditionalOnProperty(name = "bot.enabled", havingValue = "true", matchIfMissing = true)
    public TelegramClient telegramClient(@Value("${bot.token:}") String token) {
        if (token.isBlank()) throw new IllegalStateException("Set TELEGRAM_BOT_TOKEN before starting the bot");
        return new OkHttpTelegramClient(token);
    }
}
