package se.hrmsoftware.guess;

import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;
import se.hrmsoftware.guess.model.events.GameEndedEvent;
import se.hrmsoftware.guess.pitboss.Pitboss;

import java.util.HashMap;
import java.util.Random;

public class MainTest extends CamelTestSupport {

	@Test
	public void testSimpleRound() throws InterruptedException {

		ProducerTemplate producerTemplate = context().createProducerTemplate();
		producerTemplate.sendBodyAndHeaders("direct:new_games", "", new HashMap<String, Object>(){{
			put("range.lower", "100");
			put("range.upper", "200");
		}});

		Thread.sleep(15000L);

	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {
		return new RouteBuilder[]{new PitbossRoutes(), new MonitorRoutes(),
				new PlayerRoutes("P_1"),
				new PlayerRoutes("P_2"),
				new PlayerRoutes("P_3")
		};
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext theContext = super.createCamelContext();
		theContext.setTracing(false);
		return theContext;
	}


	public class PitbossRoutes extends RouteBuilder {

		private Pitboss pitboss;

		public PitbossRoutes() {
			pitboss = new Pitboss(context().createProducerTemplate(), "seda:request_queue", "seda:notify_topic");
		}

		public void onNewGame(@Header("range.lower") int lower, @Header("range.upper") int higher) {
			pitboss.startNewGame(lower, higher);
		}

		public void onResponse(@Body GuessResponse response) {
			pitboss.handleEvent(response);
		}

		@Override
		public void configure() throws Exception {
			// Receive GameSetups from a channel.
			from("direct:new_games")
					.bean(this, "onNewGame");

			// Listen for responses
			from("seda:response_queue")
					.bean(this, "onResponse");
		}

		@Override
		public String toString() {
			return "pitboss";
		}
	}

	public class MonitorRoutes extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			// Simply log events from the notification stream
			from("seda:notify_topic").log(LoggingLevel.INFO, "Event: ${body}")
					.filter(body().isInstanceOf(GameEndedEvent.class))
					.setHeader("range.lower", constant("10")).setHeader("range.upper", constant("100"))
					.log(LoggingLevel.INFO, "Game Finished - Launching another game!")
					.to("direct:new_games");
		}

		@Override
		public String toString() {
			return "monitor";
		}
	}

	public class PlayerRoutes extends RouteBuilder {

		private final String name;

		public PlayerRoutes(String name) {
			this.name = name;
		}

		@Handler
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
			return request.createResponse(name, guess);
		}

		@Override
		public void configure() throws Exception {
			// Take from request-queue - make a guess - put on response-queue.
			from("seda:request_queue?multipleConsumers=true")
					.bean(this)
					.delay(500L)
					.log(LoggingLevel.INFO, "Making a guess: ${body}")
					.to("seda:response_queue");
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
