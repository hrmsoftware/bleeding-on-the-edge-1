package se.hrmsoftware.guess.player.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import org.apache.camel.builder.RouteBuilder;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;

import java.util.Map;
import java.util.Random;

@Component(configurationPolicy = ConfigurationPolicy.require, provide = {RouteBuilder.class}, designate = PlayerRoute.Config.class)
public class PlayerRoute extends RouteBuilder {

	private Config configuration;

	public GuessResponse guess(GuessRequest request) {
		Range range = request.getGame().getRange();
		int guess = -1;
		int diff = range.getUpperBound() - range.getLowerBound();
		if (diff == 1) {
			if (new Random(System.currentTimeMillis()).nextBoolean()) {
				guess = range.getUpperBound();
			} else {
				guess = range.getLowerBound();
			}
		} else {
			int half = diff / 2;
			guess = range.getLowerBound() + half;
		}
		return request.createResponse("player_dummy", guess);
	}

	@Activate
	void activate(Map<String, Object> cfg) {
		configuration = Configurable.createConfigurable(Config.class, cfg);
	}

	@Override
	public void configure() throws Exception {
		from(configuration.requestQueue())
				.bean(this, "guess")
				.delay(configuration.delay() * 1000)
				.to(configuration.responseQueue());
	}

	@Override
	public String toString() {
		return "player";
	}

	@Meta.OCD(name = "Player Configuration")
	public interface Config {
		@Meta.AD(name = "Request Queue endpoint", description = "Camel endpoint where GuessRequests are published")
		String requestQueue();
		@Meta.AD(name = "Response Queue endpoint", description = "Camel endpoint from where GuessResponses are written")
		String responseQueue();
		@Meta.AD(name = "Guess delay", description = "Seconds to wait before submitting a guess", deflt = "2", required = false)
		long delay();
	}
}
