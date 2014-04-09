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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GameStartedEvent{");
		sb.append("gameId='").append(gameId).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
