package se.hrmsoftware.guess.leaderboard.internal;

/**
 * Takes a snapshot and transforms it into readable text.
 */
public class SnapshotFormatter {
	public static String format(final Snapshot snapshot) {
        StringBuilder s = new StringBuilder();
        s.append(String.format("+-----------------------------------------------+\n"));
        s.append(String.format("| Current: %-37d|\n", snapshot.getCurrentGames()));
        s.append(String.format("| Games played: %-32d|\n", snapshot.getPlayedGames()));
        s.append(String.format("+-----------------------------------------------+\n"));
        s.append(String.format("| Player    | Wins      | Guesses    | Ratio    |\n"));
        s.append(String.format("+-----------------------------------------------+\n"));
        for(Snapshot.PlayerStats stats : snapshot.getPlayerStats()) {
            s.append(String.format("| %-10s| %-10d| %-11d| %-9.2f|\n", stats.getPlayer(), stats.getWins(), stats.getGuesses(), ((double)stats.getWins() / stats.getGuesses())*100));
        }
        s.append(String.format("+-----------------------------------------------+\n"));
        return s.toString();
	}
}
