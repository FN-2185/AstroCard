package astrocard.telegrambot.config;

import astrocard.telegrambot.eventstate.BotEvent;
import astrocard.telegrambot.eventstate.BotState;
import org.springframework.context.annotation.Configuration;

import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.List;

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
                .state(BotState.NATAL_RESULT)
                .state(BotState.FORECAST_CARD)
                .state(BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU)
                .state(BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)
                .state(BotState.CHOOSE_PLANET);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BotState, BotEvent> transitions) throws Exception {
        transitions
                .withExternal().source(BotState.START).target(BotState.CHOOSE_ACTION).event(BotEvent.START_COMMAND).and()
                .withExternal().source(BotState.CHOOSE_ACTION).target(BotState.NATAL_COUNTRY).event(BotEvent.SELECT_NATAL_CHART).and()
                .withExternal().source(BotState.CHOOSE_ACTION).target(BotState.FORECAST_CARD).event(BotEvent.SELECT_FORECAST).and()

                .withExternal().source(BotState.NATAL_COUNTRY).target(BotState.NATAL_CITY).event(BotEvent.ENTERED_COUNTRY).and()
                .withExternal().source(BotState.NATAL_CITY).target(BotState.NATAL_DATE).event(BotEvent.ENTERED_CITY).and()
                .withExternal().source(BotState.NATAL_DATE).target(BotState.NATAL_TIME).event(BotEvent.ENTERED_DATE).and()
                .withExternal().source(BotState.NATAL_TIME).target(BotState.NATAL_RESULT).event(BotEvent.ENTERED_TIME).and()


                .withExternal().source(BotState.NATAL_RESULT).target(BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU).event(BotEvent.SELECT_DESCRIPTION_PLANET_IN_SIGN_MENU).and()
                .withExternal().source(BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU).target(BotState.CHOOSE_PLANET).event(BotEvent.SELECT_PLANET).and()
                .withExternal().source(BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU).target(BotState.NATAL_RESULT).event(BotEvent.BACK_TO_NATAL_RESULT).and()


                .withExternal().source(BotState.CHOOSE_PLANET).target(BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU).event(BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET).and()
                .withExternal().source(BotState.CHOOSE_PLANET).target(BotState.NATAL_RESULT).event(BotEvent.BACK_TO_NATAL_RESULT).and()




                .withExternal().source(BotState.NATAL_RESULT).target(BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU).event(BotEvent.SELECT_DESCRIPTION_PLANET_IN_HOUSE_MENU).and()
                .withExternal().source(BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU).target(BotState.CHOOSE_PLANET).event(BotEvent.SELECT_PLANET).and()
                .withExternal().source(BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU).target(BotState.NATAL_RESULT).event(BotEvent.BACK_TO_NATAL_RESULT).and()
                .withExternal().source(BotState.CHOOSE_PLANET).target(BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU).event(BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET).and()


                .withExternal().source(BotState.NATAL_RESULT).target(BotState.FORECAST_CARD).event(BotEvent.SELECT_FORECAST).and()
                .withExternal().source(BotState.FORECAST_CARD).target(BotState.NATAL_RESULT).event(BotEvent.BACK_TO_NATAL_RESULT);


        // Возврат к началу из любого состояния
        for (BotState state : BotState.values()) {
            transitions
                    .withExternal().source(state).target(BotState.CHOOSE_ACTION).event(BotEvent.BACK_TO_START);
        }
        for(BotState state : List.of(BotState.NATAL_RESULT,
                BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU,
                BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU,
                BotState.CHOOSE_PLANET)){
            transitions
                    .withExternal().source(state).target(BotState.NATAL_RESULT).event(BotEvent.ENTERED_TIME);
        }
    }
}
