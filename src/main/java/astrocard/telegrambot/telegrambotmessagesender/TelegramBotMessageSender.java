package astrocard.telegrambot.telegrambotmessagesender;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;

@Component
@ConditionalOnProperty(name = "bot.enabled", havingValue = "true", matchIfMissing = true)
public class TelegramBotMessageSender {
    private final TelegramClient client;
    public TelegramBotMessageSender(TelegramClient client) { this.client = client; }

    public void acknowledge(String callbackId) throws TelegramApiException {
        client.execute(AnswerCallbackQuery.builder().callbackQueryId(callbackId).build());
    }

    public void send(long chatId, String text, boolean html, List<InlineKeyboardRow> keyboard) throws TelegramApiException {
        if (text == null || text.isBlank()) return;
        if (html && text.length() > 4096) throw new IllegalArgumentException("Chart exceeds Telegram message limit");
        int offset = 0;
        while (offset < text.length()) {
            int end = Math.min(offset + 4096, text.length());
            if (end < text.length() && Character.isHighSurrogate(text.charAt(end - 1))) end--;
            var message = SendMessage.builder().chatId(chatId).text(text.substring(offset, end)).build();
            if (html) message.setParseMode("HTML");
            if (end == text.length() && !keyboard.isEmpty()) message.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
            client.execute(message);
            offset = end;
        }
    }
}
