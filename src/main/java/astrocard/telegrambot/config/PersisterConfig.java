package astrocard.telegrambot.config;

import astrocard.entity.UserData;
import astrocard.repository.UserDataRepository;
import astrocard.telegrambot.eventstate.BotEvent;
import astrocard.telegrambot.eventstate.BotState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class PersisterConfig {

    private final UserDataRepository repository;

    public PersisterConfig(UserDataRepository repository) {
        this.repository = repository;
    }

    @Bean
    public StateMachinePersister<BotState, BotEvent, Long> persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<>() {
            @Override
            public void write(StateMachineContext<BotState, BotEvent> context, Long chatId) {
                Optional<UserData> optionalUserData = repository.findUserDataByChatID(chatId);
                if (optionalUserData.isPresent()) {
                    UserData userData = optionalUserData.get();
                    userData.setFsm(context.getState()); // обновляем только fsm
                    repository.save(userData); // сохраняем обновлённый объект
                }
                else {
                    repository.save(new UserData(chatId, context.getState()));
                }
            }

            @Override
            public StateMachineContext<BotState, BotEvent> read(Long chatId) {
                return repository.findById(chatId)
                        .map(entity -> new DefaultStateMachineContext<BotState, BotEvent>(entity.getFsm(), null, null, null))
                        .orElse(null);
            }
        });
    }
}
