package astrocard.telegrambot;

import astrocard.telegrambot.eventstate.BotEvent;
import astrocard.telegrambot.eventstate.BotState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

@Service
public class StateMachineService {
    @Autowired
    private StateMachineFactory<BotState, BotEvent> factory;

    @Autowired
    private StateMachinePersister<BotState, BotEvent, Long> persister;

    public StateMachine<BotState, BotEvent> restoreStateMachine(Long chatId) throws Exception {
        StateMachine<BotState, BotEvent> stateMachine = factory.getStateMachine(chatId.toString());
        return persister.restore(stateMachine, chatId); // <= вот здесь должно быть НЕ null
    }

    public void persistStateMachine(StateMachine<BotState, BotEvent> sm, Long chatId) throws Exception {
        persister.persist(sm, chatId);
    }
}
