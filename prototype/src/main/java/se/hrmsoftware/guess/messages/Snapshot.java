package se.hrmsoftware.guess.messages;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {
	private final int gamesInProgress;
	private final int gamesFinished;
	private final List<PlayerStatus> playerStatus = new ArrayList<PlayerStatus>();


	public Snapshot(int gamesInProgress, int gamesFinished, List<PlayerStatus> playerStatus) {
		this.gamesInProgress = gamesInProgress;
		this.gamesFinished = gamesFinished;
		this.playerStatus.addAll(playerStatus);
	}

	public int getGamesInProgress() {
		return gamesInProgress;
	}

	public int getGamesFinished() {
		return gamesFinished;
	}

	public List<PlayerStatus> getPlayerStatus() {
		return playerStatus;
	}
}
