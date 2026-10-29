package astrocard.telegrambot;

import astrocard.service.ConversationService;
import astrocard.telegrambot.concurrent.ChatUpdateDispatcher;
import astrocard.telegrambot.telegrambotmessagesender.TelegramBotMessageSender;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

@Component
@ConditionalOnProperty(name = "bot.enabled", havingValue = "true", matchIfMissing = true)
public class TelegramBot implements SpringLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private final String token;
    private final ChatUpdateDispatcher dispatcher;
    private final ConversationService conversations;
    private final BotReplyRenderer renderer;
    private final TelegramBotMessageSender sender;

    public TelegramBot(@Value("${bot.token}") String token, ChatUpdateDispatcher dispatcher,
                       ConversationService conversations, BotReplyRenderer renderer, TelegramBotMessageSender sender) {
        this.token = token; this.dispatcher = dispatcher; this.conversations = conversations;
        this.renderer = renderer; this.sender = sender;
    }
    @Override public String getBotToken() { return token; }
    @Override public LongPollingUpdateConsumer getUpdatesConsumer() { return this::consume; }

    public void consume(List<Update> updates) {
        for (Update update : updates) {
            Long chatId = extractChatId(update);
            if (chatId != null) dispatcher.dispatch(chatId, () -> process(chatId, update));
        }
    }

    private void process(long chatId, Update update) {
        if (update.hasCallbackQuery()) {
            try { sender.acknowledge(update.getCallbackQuery().getId()); }
            catch (Exception exception) { log.warn("Callback acknowledgement failed for update {}", update.getUpdateId(), exception); }
        }
        try {
            String callback = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : null;
            String text = update.hasMessage() ? update.getMessage().getText() : null;
            BotReply reply = conversations.handle(chatId, update.getUpdateId(), text, callback);
            if (reply.view() == BotReply.View.NONE) return;
            var message = renderer.render(reply);
            sender.send(chatId, message.text(), message.html(), message.keyboard());
        } catch (Exception exception) {
            log.error("Cannot handle update {} for chat {}", update.getUpdateId(), chatId, exception);
            try {
                sender.send(chatId, "Не удалось выполнить действие. Повторите ввод или используйте /start.",
                        false, List.of());
            } catch (Exception deliveryException) {
                log.error("Cannot deliver error response to chat {}", chatId, deliveryException);
            }
        }
    }

    public static Long extractChatId(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage() != null
                && update.getCallbackQuery().getData() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        if (update.hasMessage() && update.getMessage().hasText()) return update.getMessage().getChatId();
        return null;
    }
}
