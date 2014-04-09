package se.hrmsoftware.guess.leaderboard.internal;

import org.apache.camel.Handler;

/**
 * Receives events and build generates a new 'Snapshot' that is written to file.
 */
public class SnapshotBuilder {
	@Handler
	public Snapshot build(Object event) {
		return null;
	}
}
