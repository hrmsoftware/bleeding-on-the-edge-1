package se.hrmsoftware.guess.model;

import java.io.Serializable;

public class GuessResponse implements Serializable {
	private GuessRequest request;
	private String player;
	private int guess;

	public GuessResponse() {
	}

	public GuessResponse(GuessRequest request, String player, int guess) {
		this.request = request;
		this.player = player;
		this.guess = guess;
	}

	public int getGuess() {
		return guess;
	}

	public void setGuess(int guess) {
		this.guess = guess;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public GuessRequest getRequest() {
		return request;
	}

	public void setRequest(GuessRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GuessResponse{");
		sb.append("request=").append(request);
		sb.append(", player='").append(player).append('\'');
		sb.append(", guess=").append(guess);
		sb.append('}');
		return sb.toString();
	}
}
