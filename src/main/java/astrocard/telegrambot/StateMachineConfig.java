package astrocard.telegrambot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig  extends StateMachineConfigurerAdapter<BotState, BotEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<BotState, BotEvent> states) throws Exception {
        states.withStates()
                .initial(BotState.START)
                .state(BotState.CHOOSE_ACTION)
                .state(BotState.NATAL_COUNTRY)
                .state(BotState.NATAL_CITY)
                .state(BotState.NATAL_DATE)
                .state(BotState.NATAL_TIME)
                //.state(BotState.NATAL_RESULT)
                .state(BotState.FORECAST_CHECK_CARD)
                .state(BotState.FORECAST_UNAVAILABLE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BotState, BotEvent> transitions) throws Exception {
        transitions
                .withExternal().source(BotState.START).target(BotState.CHOOSE_ACTION).event(BotEvent.START_COMMAND).and()
                .withExternal().source(BotState.CHOOSE_ACTION).target(BotState.NATAL_COUNTRY).event(BotEvent.SELECT_NATAL_CHART).and()
                .withExternal().source(BotState.CHOOSE_ACTION).target(BotState.FORECAST_CHECK_CARD).event(BotEvent.SELECT_FORECAST).and()

                .withExternal().source(BotState.NATAL_COUNTRY).target(BotState.NATAL_CITY).event(BotEvent.ENTERED_COUNTRY).and()
                .withExternal().source(BotState.NATAL_CITY).target(BotState.NATAL_DATE).event(BotEvent.ENTERED_CITY).and()
                .withExternal().source(BotState.NATAL_DATE).target(BotState.NATAL_TIME).event(BotEvent.ENTERED_DATE).and()
                .withExternal().source(BotState.NATAL_TIME).target(BotState.CHOOSE_ACTION).event(BotEvent.ENTERED_TIME).and()

                .withExternal().source(BotState.FORECAST_CHECK_CARD).target(BotState.NATAL_COUNTRY).event(BotEvent.USER_HAS_NO_CARD).and()
                .withExternal().source(BotState.FORECAST_CHECK_CARD).target(BotState.FORECAST_UNAVAILABLE).event(BotEvent.USER_HAS_CARD);

        // Возврат к началу из любого состояния
        for (BotState state : BotState.values()) {
            transitions
                    .withExternal().source(state).target(BotState.CHOOSE_ACTION).event(BotEvent.BACK_TO_START);
        }
    }
}
