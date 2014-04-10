package se.hrmsoftware.guess.leaderboard.internal;

import org.apache.camel.Handler;

/**
 * Receives events and build generates a new 'Snapshot' that is written to file.
 */
public class SnapshotBuilder {
    private Snapshot current = new Snapshot();

	@Handler
	public Snapshot build(Object event) {
        return current = current.create(event);
	}
}
