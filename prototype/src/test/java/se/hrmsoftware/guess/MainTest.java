package se.hrmsoftware.guess;

import org.apache.camel.CamelContext;
import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import se.hrmsoftware.guess.messages.GameSignal;
import se.hrmsoftware.guess.messages.GuessRequest;
import se.hrmsoftware.guess.messages.GuessResponse;
import se.hrmsoftware.guess.messages.PlayerStatus;
import se.hrmsoftware.guess.messages.Snapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainTest extends CamelTestSupport {

	@Test
	public void testSimpleRound() throws InterruptedException {
		ProducerTemplate producer = context().createProducerTemplate();
		producer.sendBody("seda:guess", new GameSignal(10, 90));
		Thread.sleep(10000L);
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {
		return new RouteBuilder[]{
				new PlayerRoute(),
				new PitbossRoute(),
				new LeaderboardRoute()
		};
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext theContext = super.createCamelContext();
		theContext.setTracing(false);
		return theContext;
	}

	public static class PlayerRoute extends RouteBuilder {
		@Override
		public void configure() throws Exception {
			from("seda:game").bean(new Player("foo")).to("seda:guess");
		}
	}

	public static class PitbossRoute extends RouteBuilder {
		@Override
		public void configure() throws Exception {
			from("seda:guess")
					.split().method(new Pitboss(), "react")
					.choice()
						.when(body().isInstanceOf(GuessRequest.class)).to("seda:game")
						.when(body().isInstanceOf(Snapshot.class)).to("seda:status")
						.otherwise().log(LoggingLevel.WARN, "No matching choice! ${body}")
					.endChoice();
		}
	}

	public static class LeaderboardRoute extends RouteBuilder {
		@Override
		public void configure() throws Exception {
			from("seda:status").log(LoggingLevel.INFO, "STATUS: ${body}");
		}
	}

	public static class Pitboss {

		private Snapshot lastSnapshot = null;
		private GameSignal gameSignal = null;
		private int correctGuess;
		private String gameUid;

		public List<Object> react(Object input) {
			System.out.println("Getting input: " + input);
			if (input instanceof GameSignal) {
				return createGame(GameSignal.class.cast(input));
			} else if (input instanceof GuessResponse) {
				return evalGuess(GuessResponse.class.cast(input));
			} else {
				return Collections.emptyList();
			}
		}

		private List<Object> evalGuess(GuessResponse guessResponse) {
			List<Object> result = new ArrayList<Object>();
			PlayerStatus ps = new PlayerStatus(guessResponse.getPlayerUid(), 1, 1, 0L);
			if (guessResponse.getGuess() == correctGuess) {
				lastSnapshot = new Snapshot(lastSnapshot.getGamesInProgress() - 1, lastSnapshot.getGamesFinished() + 1, Arrays.asList(ps));
			} else {
				if (guessResponse.getGuess() < correctGuess) {
					result.add(new GuessRequest(gameUid, guessResponse.getGuess(), gameSignal.getUpperBoundary()));
				} else {
					result.add(new GuessRequest(gameUid, gameSignal.getLowerBoundary(), guessResponse.getGuess()));
				}
				lastSnapshot = new Snapshot(lastSnapshot.getGamesInProgress(), lastSnapshot.getGamesFinished(), Arrays.asList(ps));
			}
			result.add(lastSnapshot);
			return result;
		}

		private List<Object> createGame(GameSignal gameSignal) {
			this.gameSignal = gameSignal;
			correctGuess = gameSignal.getLowerBoundary() + (gameSignal.getUpperBoundary() - gameSignal.getLowerBoundary() / 2);
			gameUid = UUID.randomUUID().toString();
			GuessRequest request = new GuessRequest(gameUid, gameSignal.getLowerBoundary(), gameSignal.getUpperBoundary());
			lastSnapshot = new Snapshot(1, 0, Collections.<PlayerStatus>emptyList());
			return Arrays.asList(request, lastSnapshot);
		}
	}

	public static class Player {
		private final String uuid;

		public Player(String uuid) {
			this.uuid = uuid;
		}

		@Handler
		public GuessResponse guess(GuessRequest request) {
			return request.guess(uuid, request.getUpperBoundary() - 1);
		}
	}

}
