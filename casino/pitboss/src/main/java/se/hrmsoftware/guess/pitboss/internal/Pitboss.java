package se.hrmsoftware.guess.pitboss.internal;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.hrmsoftware.guess.model.Game;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;
import se.hrmsoftware.guess.model.events.GameEndedEvent;
import se.hrmsoftware.guess.model.events.GameStartedEvent;
import se.hrmsoftware.guess.model.events.GuessMadeEvent;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Pitboss implements GameDriver.Callback {

	private static final Logger LOG = LoggerFactory.getLogger(Pitboss.class);
	private final Random random = new SecureRandom();
	private final ConcurrentHashMap<String, GameDriver> gameDrivers = new ConcurrentHashMap<String, GameDriver>();
	private final ProducerTemplate producerTemplate;
	private final String requestEndpoint;
	private final String notifyEndpoint;

	public Pitboss(ProducerTemplate producerTemplate, String requestEndpoint, String notifyEndpoint) {
		this.producerTemplate = producerTemplate;
		this.requestEndpoint = requestEndpoint;
		this.notifyEndpoint = notifyEndpoint;
	}

	/**
	 * Can be started by the notification driver.
	 */
	public void startNewGame(int lower, int upper) {
		Range range = new Range(lower, upper); // Hard-coded for now!
		int correctGuess = createCorrectGuess(range);
		if (!range.isIncluded(correctGuess)) {
			throw new IllegalStateException("Wtf! wtf!");
		}

		Game game = new Game(newGameId(), range);
		GameSetup setup = new GameSetup(game, correctGuess);

		GameDriver driver = new GameDriver(setup, this);
		gameDrivers.put(game.getGameId(), driver);
		// Kick-off the game.
		sendNotify(new GameStartedEvent(game.getGameId()));
		driver.sendInitialRequest();
	}

	protected int createCorrectGuess(Range range) {
		int diff = range.getUpperBound() - range.getLowerBound();
		return random.nextInt(diff + 1) + range.getLowerBound();
	}
	protected String newGameId() {
		return UUID.randomUUID().toString();
	}

	public void handleEvent(GuessResponse response) {
		GameDriver driver = gameDrivers.get(response.getRequest().getGame().getGameId());
		if (driver != null) {
			driver.onResponse(response);
		} else {
			LOG.debug("Dropping event from unknown game: {}", response);
		}
	}

	private void sendNotify(Object event) {
		LOG.debug("Sending notification: " + event);
		producerTemplate.sendBody(notifyEndpoint, event);
	}

	private void enqueue(GuessRequest request) {
		LOG.debug("Sending request: " + request);
		producerTemplate.sendBody(requestEndpoint, request);
	}

	@Override
	public void postRequest(GuessRequest newRequest) {
		enqueue(newRequest);
	}

	@Override
	public void onCorrectGuess(String gameId, String player) {
		if (gameDrivers.remove(gameId) != null) {
			sendNotify(new GameEndedEvent(gameId, player));
		}
	}

	@Override
	public void onGuess(String gameId, String player) {
		sendNotify(new GuessMadeEvent(player, gameId));
	}
}
