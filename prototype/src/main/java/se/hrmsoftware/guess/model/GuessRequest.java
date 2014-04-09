package se.hrmsoftware.guess.model;

import java.io.Serializable;

public class GuessRequest implements Serializable {
	private Game game;
	private String requestId;

	public GuessRequest() {}

	public GuessRequest(Game game, String requestId) {
		this.game = game;
		this.requestId = requestId;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public GuessResponse createResponse(String player, int guess) {
		return new GuessResponse(this, player, guess);
	}

}
