package se.hrmsoftware.guess.pitboss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.hrmsoftware.guess.model.Game;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;

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

	public GameDriver(GameSetup setup, Callback callback) {
		this.gameSetup = setup;
		this.callback = callback;
	}

	public void onResponse(GuessResponse response) {
		if (!response.getRequest().getGame().getGameId().equals(gameSetup.getGame().getGameId())) {
			LOG.warn("Received response for unknown game (!). My game: {}, That game: {}",
					gameSetup.getGame().getGameId(),
					response.getRequest().getGame().getGameId());
		} else {
			callback.onGuess(gameSetup.getGame().getGameId(), response.getPlayer());
			if (gameSetup.getCorrectGuess() == response.getGuess()) {
				LOG.info("{} We have a winner: {}", gameSetup.getGame().getGameId(), response.getPlayer());
				callback.onCorrectGuess(gameSetup.getGame().getGameId(),
						response.getPlayer());
			} else {
				// Guess is not correct. Correct the range, and send out a new request.
				GuessRequest newRequest = createNewRequest(response.getGuess(), response.getRequest().getGame());
				callback.postRequest(newRequest);
			}
		}
	}

	private GuessRequest createNewRequest(int guess, Game game) {
		return null;
	}


}
