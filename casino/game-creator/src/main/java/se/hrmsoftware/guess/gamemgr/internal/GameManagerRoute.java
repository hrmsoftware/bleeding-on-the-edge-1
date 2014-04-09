package se.hrmsoftware.guess.gamemgr.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import se.hrmsoftware.guess.model.events.GameEndedEvent;
import se.hrmsoftware.guess.model.events.GameStartedEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Listens for events on the event-notification channel, and
 * creates new games according to the configuration.
 */
@Component(configurationPolicy = ConfigurationPolicy.require, enabled = false, designate = GameManagerRoute.Config.class, provide = {RouteBuilder.class})
public class GameManagerRoute extends RouteBuilder {

	private final Set<String> startedGames = Collections.synchronizedSet(new HashSet<String>());
	private Config configuration;
	private ProducerTemplate producerTemplate;

	@Reference
	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@Activate
	void activate(Map<String, Object> cfg) {
		this.configuration = Configurable.createConfigurable(Config.class, cfg);
		// Create the first set of events.
		for (int i = 0; i < configuration.numberOfGames(); i++) {
			createNewGame();
		}
	}

	private void createNewGame() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("range.lower", "1");
		headers.put("range.upper", "1000"); // TODO: Maybe make configurable.
		producerTemplate.sendBodyAndHeaders(configuration.newGamesEndpoint(), "", headers);
	}

	@Override
	public void configure() throws Exception {
		from(configuration.eventsEndpoint())
				.routeId("hrm.casino.gamecreator")
				.bean(this);
	}

	@Handler
	public synchronized void onEvent(CamelContext camelContext, @Body Object event) {
		if (event instanceof GameEndedEvent) {
			GameEndedEvent e = (GameEndedEvent) event;
			startedGames.remove(e.getGameId());
			int gamesToCreate = configuration.numberOfGames() - startedGames.size();
			if (gamesToCreate > 0) {
				for(int i = 0; i < gamesToCreate; i++) {
					createNewGame();
				}
			}
		} else if (event instanceof GameStartedEvent) {
			startedGames.add(GameStartedEvent.class.cast(event).getGameId());
		}
	}

	@Override
	public String toString() {
		return "...";
	}

	@Meta.OCD(name = "Game Creator config")
	public interface Config {
		@Meta.AD(name = "New Games endpoint", description = "The endpoint to use to publish new games")
		String newGamesEndpoint();
		@Meta.AD(name = "Events endpoint", description = "Camel endpoint from where to read events")
		String eventsEndpoint();
		@Meta.AD(name = "Number of games. Default: 5", deflt = "5", required = false)
		int numberOfGames();
	}
}
