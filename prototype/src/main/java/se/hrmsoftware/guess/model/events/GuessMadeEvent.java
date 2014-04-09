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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GuessMadeEvent{");
		sb.append("player='").append(player).append('\'');
		sb.append(", gameId='").append(gameId).append('\'');
		sb.append('}');
		return sb.toString();
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
