package se.hrmsoftware.guess.leaderboard.internal;

import se.hrmsoftware.guess.model.events.GameEndedEvent;
import se.hrmsoftware.guess.model.events.GameStartedEvent;
import se.hrmsoftware.guess.model.events.GuessMadeEvent;

import java.util.*;

/**
 * Represents the current leader-board.
 */
public class Snapshot {
    private final static Comparator<PlayerStats> PLAYER_STATS_COMPARATOR = new Comparator<PlayerStats>() {
        @Override
        public int compare(PlayerStats o1, PlayerStats o2) {
            return Integer.valueOf(o2.getWins()).compareTo(o1.getWins());
        }
    };

    private final int currentGames;
    private final int playedGames;

    private final Map<String, PlayerStats> playerStats;

    public Snapshot() {
        this(0, 0, new HashMap<String, PlayerStats>());
    }

    private Snapshot(int currentGames,
                    int playedGames,
                    Map<String, PlayerStats> playerStats) {
        this.currentGames = currentGames;
        this.playedGames = playedGames;
        this.playerStats = playerStats;
    }

    public Snapshot create(Object event) {
        if(event instanceof GameStartedEvent) {
            return gameStarted((GameStartedEvent)event);
        } else if(event instanceof GuessMadeEvent) {
            return guessMade((GuessMadeEvent)event);
        } else if(event instanceof GameEndedEvent) {
            return gameEnded((GameEndedEvent)event);
        }

        return this;
    }

    private Snapshot gameEnded(GameEndedEvent event) {
        PlayerStats player = playerStats.get(event.getWinner());
        if(player == null) {
            player = new PlayerStats(event.getWinner());
        }
        playerStats.put(player.getPlayer(), player.gameWon());
        return new Snapshot(currentGames - 1, playedGames + 1, playerStats);
    }

    private Snapshot guessMade(GuessMadeEvent event) {
        PlayerStats player = playerStats.get(event.getPlayer());
        if(player == null) {
            player = new PlayerStats(event.getPlayer());
        }
        playerStats.put(player.getPlayer(), player.guessMade());

        return new Snapshot(currentGames, playedGames, playerStats);
    }

    private Snapshot gameStarted(GameStartedEvent event) {
        return new Snapshot(currentGames + 1, playedGames, playerStats);
    }

    public int getCurrentGames() {
        return currentGames;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public List<PlayerStats> getPlayerStats() {
        List<PlayerStats> stats = new ArrayList<PlayerStats>(playerStats.values());
        Collections.sort(stats, PLAYER_STATS_COMPARATOR);
        return Collections.unmodifiableList(stats);
    }

    public static class PlayerStats {
        private final String player;
        private final int wins;
        private final int guesses;

        public PlayerStats(String player) {
            this(player, 0, 0);
        }

        private PlayerStats(String player, int wins, int guesses) {
            this.player = player;
            this.wins = wins;
            this.guesses = guesses;
        }

        public String getPlayer() {
            return player;
        }

        public int getWins() {
            return wins;
        }

        public int getGuesses() {
            return guesses;
        }

        public PlayerStats guessMade() {
            return new PlayerStats(player, wins, guesses + 1);
        }

        public PlayerStats gameWon() {
            return new PlayerStats(player, wins + 1, guesses);
        }
    }
}
