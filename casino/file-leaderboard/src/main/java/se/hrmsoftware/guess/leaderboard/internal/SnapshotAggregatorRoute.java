package se.hrmsoftware.guess.leaderboard.internal;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import org.apache.camel.builder.RouteBuilder;

import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Reads events from the configured event-stream, updates a Leaderboard instance
 * that is periodically written to disk.
 */
@Component(name = "LeaderboardRoute",
		provide = {RouteBuilder.class},
		designate = SnapshotAggregatorRoute.Config.class,
		configurationPolicy = ConfigurationPolicy.require)
public class SnapshotAggregatorRoute extends RouteBuilder {

	private Config configuration;
	private SnapshotBuilder snapshotBuilder;

	@Activate
	void onActivate(Map<String, Object> configuration) {
		this.configuration = Configurable.createConfigurable(Config.class, configuration);
		snapshotBuilder = new SnapshotBuilder();
	}

	@Override
	public void configure() throws Exception {
		from(configuration.eventsEndpoint())
				.bean(snapshotBuilder)
				.bean(SnapshotFormatter.class)
                .sample(1, SECONDS)
				.to(configuration.outputEndpoint());
	}

	@Meta.OCD(name = "Leaderboard Route configuration")
	public interface Config {
		@Meta.AD(name = "Events endpoint", description = "Camel endpoint from where to read events")
		String eventsEndpoint();
		@Meta.AD(name = "Output endpoint", description = "Camel endpoint where the updated leaderboard will be written")
		String outputEndpoint();
	}

}
