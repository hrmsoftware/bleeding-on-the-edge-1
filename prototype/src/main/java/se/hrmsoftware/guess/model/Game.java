package se.hrmsoftware.guess.model;

import java.io.Serializable;

public class Game implements Serializable {
	private String gameId;
	private Range range;

	public Game() {
	}

	public Game(String gameId, Range range) {
		this.gameId = gameId;
		this.range = range;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
}
