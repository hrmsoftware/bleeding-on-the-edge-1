package se.hrmsoftware.guess.model.events;

import java.io.Serializable;

public class GuessMadeEvent implements Serializable {
	private String player;
	private String gameId;

	public GuessMadeEvent() {
	}

	public GuessMadeEvent(String player, String gameId) {
		this.player = player;
		this.gameId = gameId;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
}
