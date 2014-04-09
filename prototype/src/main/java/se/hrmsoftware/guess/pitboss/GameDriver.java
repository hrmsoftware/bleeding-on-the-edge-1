package se.hrmsoftware.guess.pitboss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.hrmsoftware.guess.model.Game;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Drives a game.
 */
public class GameDriver {

	public interface Callback {
		void postRequest(GuessRequest newRequest);
		void onCorrectGuess(String gameId, String player);
		void onGuess(String gameId, String player);
	}
	private final Callback callback;
	private final Logger LOG = LoggerFactory.getLogger(GameDriver.class);
	private final GameSetup gameSetup;
	private final ConcurrentHashMap<String, GuessRequest> outstandingRequests = new ConcurrentHashMap<String, GuessRequest>();

	public GameDriver(GameSetup setup, Callback callback) {
		this.gameSetup = setup;
		this.callback = callback;
	}

	public void sendInitialRequest() {
		String requestId = newRequestId();
		GuessRequest request = new GuessRequest(gameSetup.getGame(), requestId);
		outstandingRequests.putIfAbsent(requestId, request);
		callback.postRequest(request);
	}

	private String newRequestId() {
		return UUID.randomUUID().toString();
	}

	public void onResponse(GuessResponse response) {
		if (!response.getRequest().getGame().getGameId().equals(gameSetup.getGame().getGameId())) {
			LOG.warn("Received response for unknown game (!). My game: {}, That game: {}",
					gameSetup.getGame().getGameId(),
					response.getRequest().getGame().getGameId());
		} else {
			GuessRequest request = outstandingRequests.remove(response.getRequest().getRequestId());
			if (request == null) {
				LOG.warn("Dropping unexpected response: {}", response);
			} else {
				callback.onGuess(gameSetup.getGame().getGameId(), response.getPlayer());
				if (gameSetup.getCorrectGuess() == response.getGuess()) {
					LOG.info("{} We have a winner: {}", gameSetup.getGame().getGameId(), response.getPlayer());
					callback.onCorrectGuess(gameSetup.getGame().getGameId(),
							response.getPlayer());
				}
				else {
					// Guess is not correct. Correct the range, and send out a new request.
					Range newRange = createNewRange(request.getGame().getRange(), response.getGuess(), gameSetup.getCorrectGuess());
					String requestId = newRequestId();
					GuessRequest newRequest = new GuessRequest(new Game(gameSetup.getGame().getGameId(), newRange), requestId);
					// Add to outstanding
					outstandingRequests.putIfAbsent(requestId, newRequest);
					callback.postRequest(newRequest);
				}
			}
		}
	}

	public static Range createNewRange(Range range, int guess, int correctGuess) {

		int lower = range.getLowerBound();
		int high = range.getUpperBound();

		if (guess < correctGuess) {
			lower = guess + 1;
		} else if (guess > correctGuess) {
			high = guess - 1;
		} else {
			throw new IllegalArgumentException("wtf?!");
		}

		if (lower == high) {
			// Ouch - we've converged down to a 0 range. Create the range with size 1 and include the right result.
			if (lower == range.getLowerBound()) {
				high = lower + 1;
			} else {
				lower = high - 1;
			}
		}

		return new Range(lower, high);
	}


}
