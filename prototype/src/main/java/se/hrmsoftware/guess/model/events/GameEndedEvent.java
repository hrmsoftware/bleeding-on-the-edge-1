package se.hrmsoftware.guess.model.events;

import java.io.Serializable;

public class GameEndedEvent implements Serializable {

	private String gameId;
	private String winner;

	public GameEndedEvent() {
	}

	public GameEndedEvent(String gameId, String winner) {
		this.gameId = gameId;
		this.winner = winner;
	}
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
}
