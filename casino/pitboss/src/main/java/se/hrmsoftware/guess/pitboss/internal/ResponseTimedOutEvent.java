package se.hrmsoftware.guess.pitboss.internal;

public class ResponseTimedOutEvent {
	private final String gameId;
	private final String requestId;

	public ResponseTimedOutEvent(String gameId, String requestId) {
		this.gameId = gameId;
		this.requestId = requestId;
	}

	public String getGameId() {
		return gameId;
	}

	public String getRequestId() {
		return requestId;
	}
}
