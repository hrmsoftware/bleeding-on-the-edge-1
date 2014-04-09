package se.hrmsoftware.guess.pitboss.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import se.hrmsoftware.guess.model.GuessResponse;

import java.util.Map;

@Component(provide = {RouteBuilder.class}, configurationPolicy = ConfigurationPolicy.require, designate = PitbossRoutes.Config.class)
public class PitbossRoutes extends RouteBuilder {
	private ProducerTemplate producerTemplate;
	private Config configuration;
	private Pitboss pitboss;

	public void onNewGame(@Header("range.lower") int lower, @Header("range.upper") int upper) {
		pitboss.startNewGame(lower, upper);
	}

	public void onResponse(@Body GuessResponse response) {
		pitboss.handleEvent(response);
	}

	@Override
	public void configure() throws Exception {
		// read new games instructions
		from(configuration.newGamesEndpoint())
				.routeId("hrm.casino.pitboss.newgames")
				.bean(this, "onNewGame");

		// read from response-queue
		from(configuration.responseQueue())
				.routeId("hrm.casino.pitboss.responses")
				.bean(this, "onResponse");
	}

	@Reference
	void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@Activate
	void activate(Map<String, Object> cfg) {
		this.configuration = Configurable.createConfigurable(Config.class, cfg);
		pitboss = new Pitboss(producerTemplate, configuration.requestQueue(), configuration.eventsEndpoint());
	}

	@Override
	public String toString() {
		return "pitboss";
	}

	@Meta.OCD(name = "Pitboss Routes config")
	public interface Config {
		@Meta.AD(name = "Events endpoint", description = "Camel endpoint where events are published")
		String eventsEndpoint();
		@Meta.AD(name = "New Games endpoint", description = "The endpoint from where to receive instructions to create new games")
		String newGamesEndpoint();
		@Meta.AD(name = "Request Queue endpoint", description = "Camel endpoint where GuessRequests are published")
		String requestQueue();
		@Meta.AD(name = "Response Queue endpoint", description = "Camel endpoint from where GuessResponses are read")
		String responseQueue();
	}
}
