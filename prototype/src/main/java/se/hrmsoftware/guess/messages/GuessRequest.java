package se.hrmsoftware.guess.messages;

public class GuessRequest {
	private final String uuid;
	private final int lowerBoundary;
	private final int upperBoundary;

	public GuessRequest(String uuid, int lowerBoundary, int upperBoundary) {
		this.uuid = uuid;
		this.lowerBoundary = lowerBoundary;
		this.upperBoundary = upperBoundary;
	}

	public String getUuid() {
		return uuid;
	}

	public int getLowerBoundary() {
		return lowerBoundary;
	}

	public int getUpperBoundary() {
		return upperBoundary;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GuessRequest{");
		sb.append("uuid='").append(uuid).append('\'');
		sb.append(", lowerBoundary=").append(lowerBoundary);
		sb.append(", upperBoundary=").append(upperBoundary);
		sb.append('}');
		return sb.toString();
	}

	public GuessResponse guess(String playerUid, int guess) {
		return new GuessResponse(this, playerUid, guess);
	}
}
