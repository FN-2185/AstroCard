package astrocard.telegrambot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class PersisterConfig {
    @Bean
    public StateMachinePersister<BotState, BotEvent, Long> persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<>() {
            private final Map<Long, StateMachineContext<BotState, BotEvent>> storage = new ConcurrentHashMap<>();

            @Override
            public void write(StateMachineContext<BotState, BotEvent> context, Long contextObj) {
                storage.put(contextObj, context);
            }

            @Override
            public StateMachineContext<BotState, BotEvent> read(Long contextObj) {
                return storage.get(contextObj);
            }
        });
    }
}
