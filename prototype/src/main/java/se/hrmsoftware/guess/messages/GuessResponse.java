package se.hrmsoftware.guess.messages;

public class GuessResponse {
	private final GuessRequest request;
	private final String playerUid;
	private final int guess;

	public GuessResponse(GuessRequest request, String playerUid, int guess) {
		this.request = request;
		this.playerUid = playerUid;
		this.guess = guess;
	}

	public GuessRequest getRequest() {
		return request;
	}

	public String getPlayerUid() {
		return playerUid;
	}

	public int getGuess() {
		return guess;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GuessResponse{");
		sb.append("request=").append(request);
		sb.append(", playerUid='").append(playerUid).append('\'');
		sb.append(", guess=").append(guess);
		sb.append('}');
		return sb.toString();
	}
}
