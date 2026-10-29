package astrocard.telegrambot;

import astrocard.telegrambot.eventstate.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.*;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Optional;

@Service
public class StateMachineService {
    private final StateMachineFactory<BotState, BotEvent> factory;
    public StateMachineService(StateMachineFactory<BotState, BotEvent> factory) { this.factory = factory; }


    public Optional<BotState> transition(BotState source, BotEvent event) {
        var machine = factory.getStateMachine();
        Duration timeout = Duration.ofSeconds(10);
        try {
            machine.getStateMachineAccessor().doWithAllRegions(access ->
                    access.resetStateMachineReactively(new DefaultStateMachineContext<BotState, BotEvent>(
                            source, null, null, null)).block(timeout));
            machine.startReactively().block(timeout);
            var results = machine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build()))
                    .collectList().block(timeout);
            if (results == null || results.isEmpty()) return Optional.empty();
            for (var result : results) result.complete().block(timeout);
            if (results.stream().noneMatch(result -> result.getResultType() == StateMachineEventResult.ResultType.ACCEPTED)) {
                return Optional.empty();
            }
            if (machine.hasStateMachineError()) throw new IllegalStateException("State machine transition failed");
            return Optional.of(machine.getState().getId());
        } finally {
            machine.stopReactively().block(timeout);
        }
    }
}
