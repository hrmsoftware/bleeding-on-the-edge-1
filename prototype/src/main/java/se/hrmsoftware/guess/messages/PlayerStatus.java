package se.hrmsoftware.guess.messages;

public class PlayerStatus {

	private final String playerUid;
	private final int numberOfGuesses;
	private final int numberOfWins;
	private final long lastResponseTime;


	public PlayerStatus(String playerUid, int numberOfGuesses, int numberOfWins, long lastResponseTime) {
		this.playerUid = playerUid;
		this.numberOfGuesses = numberOfGuesses;
		this.numberOfWins = numberOfWins;
		this.lastResponseTime = lastResponseTime;
	}

	public String getPlayerUid() {
		return playerUid;
	}

	public int getNumberOfGuesses() {
		return numberOfGuesses;
	}

	public int getNumberOfWins() {
		return numberOfWins;
	}

	public long getLastResponseTime() {
		return lastResponseTime;
	}
}
