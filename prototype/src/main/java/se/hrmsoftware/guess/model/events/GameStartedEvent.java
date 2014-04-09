package se.hrmsoftware.guess.model.events;

import java.io.Serializable;

public class GameStartedEvent implements Serializable {
	private String gameId;

	public GameStartedEvent() {
	}

	public GameStartedEvent(String gameId) {
		this.gameId = gameId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
}
