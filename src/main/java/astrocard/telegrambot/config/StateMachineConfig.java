package astrocard.telegrambot.config;

import astrocard.telegrambot.eventstate.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.*;
import org.springframework.statemachine.config.builders.*;
import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<BotState, BotEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<BotState, BotEvent> states) throws Exception {
        states.withStates().initial(BotState.CHOOSE_ACTION).states(EnumSet.allOf(BotState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BotState, BotEvent> transitions) throws Exception {
        for (BotState state : BotState.values()) {
            edge(transitions, state, BotState.CHOOSE_ACTION, BotEvent.START_COMMAND);
            edge(transitions, state, BotState.CHOOSE_ACTION, BotEvent.BACK_TO_START);
            edge(transitions, state, BotState.NATAL_RESULT, BotEvent.SHOW_SAVED_CHART);
        }
        edge(transitions, BotState.CHOOSE_ACTION, BotState.NATAL_COUNTRY, BotEvent.SELECT_NATAL_CHART);
        edge(transitions, BotState.NATAL_RESULT, BotState.NATAL_COUNTRY, BotEvent.SELECT_NATAL_CHART);
        edge(transitions, BotState.FORECAST_CARD, BotState.NATAL_COUNTRY, BotEvent.SELECT_NATAL_CHART);
        edge(transitions, BotState.CHOOSE_ACTION, BotState.FORECAST_CARD, BotEvent.SELECT_FORECAST);
        edge(transitions, BotState.NATAL_RESULT, BotState.FORECAST_CARD, BotEvent.SELECT_FORECAST);
        edge(transitions, BotState.NATAL_COUNTRY, BotState.NATAL_CITY, BotEvent.ENTERED_COUNTRY);
        edge(transitions, BotState.NATAL_CITY, BotState.NATAL_DATE, BotEvent.ENTERED_CITY);
        edge(transitions, BotState.NATAL_DATE, BotState.NATAL_TIME, BotEvent.ENTERED_DATE);
        edge(transitions, BotState.NATAL_TIME, BotState.NATAL_RESULT, BotEvent.ENTERED_TIME);
        edge(transitions, BotState.NATAL_RESULT, BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU, BotEvent.SELECT_DESCRIPTION_PLANET_IN_SIGN_MENU);
        edge(transitions, BotState.NATAL_RESULT, BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU, BotEvent.SELECT_DESCRIPTION_PLANET_IN_HOUSE_MENU);
        edge(transitions, BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU, BotState.SHOW_PLANET_IN_SIGN, BotEvent.SELECT_PLANET);
        edge(transitions, BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU, BotState.SHOW_PLANET_IN_HOUSE, BotEvent.SELECT_PLANET);
        edge(transitions, BotState.SHOW_PLANET_IN_SIGN, BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU, BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET);
        edge(transitions, BotState.SHOW_PLANET_IN_HOUSE, BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU, BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET);
        for (BotState state : EnumSet.of(BotState.FORECAST_CARD, BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU,
                BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU, BotState.SHOW_PLANET_IN_SIGN, BotState.SHOW_PLANET_IN_HOUSE)) {
            edge(transitions, state, BotState.NATAL_RESULT, BotEvent.BACK_TO_NATAL_RESULT);
        }
    }

    private void edge(StateMachineTransitionConfigurer<BotState, BotEvent> config,
                      BotState source, BotState target, BotEvent event) throws Exception {
        config.withExternal().source(source).target(target).event(event);
    }
}
