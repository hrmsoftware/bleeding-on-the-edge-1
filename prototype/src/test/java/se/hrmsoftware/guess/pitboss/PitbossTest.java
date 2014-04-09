package se.hrmsoftware.guess.pitboss;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.Range;
import se.hrmsoftware.guess.model.events.GameStartedEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class PitbossTest {

	@Test
	public void testScenario() {
		String requestEndpoint = "seda:queue";
		String notifyEndpoint = "seda:notify";
		ProducerTemplate template = Mockito.mock(ProducerTemplate.class);
		Pitboss pitboss = new Pitboss(template, requestEndpoint, notifyEndpoint) {
			@Override
			protected int createCorrectGuess(Range range) {
				return 50;
			}

			@Override
			protected String newGameId() {
				return "foo";
			}
		};

		pitboss.startNewGame(1, 100);

		// Check that a game was started.
		ArgumentCaptor<GameStartedEvent> startedEventCaptor = ArgumentCaptor.forClass(GameStartedEvent.class);
		ArgumentCaptor<GuessRequest> guessRequestCaptor = ArgumentCaptor.forClass(GuessRequest.class);
		verify(template).sendBody(eq(requestEndpoint), guessRequestCaptor.capture());
		verify(template).sendBody(eq(notifyEndpoint), startedEventCaptor.capture());

		Assert.assertEquals("foo", startedEventCaptor.getValue().getGameId());

		pitboss.handleEvent(guessRequestCaptor.getValue().createResponse("f_1", 101));


	}
}
