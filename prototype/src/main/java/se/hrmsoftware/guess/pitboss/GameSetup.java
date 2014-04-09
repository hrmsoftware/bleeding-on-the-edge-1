package se.hrmsoftware.guess.pitboss;

import se.hrmsoftware.guess.model.Game;

public class GameSetup {
	private final Game game;
	private final int correctGuess;

	public GameSetup(Game game, int correctGuess) {
		if (!game.getRange().isIncluded(correctGuess)) {
			throw new IllegalArgumentException("Correct guess not within range");
		}
		this.game = game;
		this.correctGuess = correctGuess;
	}

	public Game getGame() {
		return game;
	}

	public int getCorrectGuess() {
		return correctGuess;
	}
}
