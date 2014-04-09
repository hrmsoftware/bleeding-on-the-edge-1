package se.hrmsoftware.guess.pitboss;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import se.hrmsoftware.guess.model.Game;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class GameDriverTest {

	@Test
	public void testScenario() {
		GameDriver.Callback callback = mock(GameDriver.Callback.class);
		final GameSetup gameSetup = new GameSetup(new Game("foo", new Range(10, 20)), 15);

		GameDriver driver = new GameDriver(gameSetup, callback);

		GuessRequest request = new GuessRequest(gameSetup.getGame(), "req_1");
		GuessResponse response = request.createResponse("p1", 12);
		driver.onResponse(response);

		verify(callback).onGuess(eq("foo"), eq("p1"));
		verify(callback).postRequest(argThat(new GuessRequestMatcher(13, 20)));
		reset(callback);

		GuessRequest request2 = new GuessRequest(new Game("foo", new Range(11, 20)), "req_2");
		GuessResponse response2 = request2.createResponse("p2", 18);
		driver.onResponse(response2);

		verify(callback).onGuess(eq("foo"), eq("p2"));
		verify(callback).postRequest(argThat(new GuessRequestMatcher(13, 17)));
		reset(callback);

		GuessRequest request3 = new GuessRequest(new Game("foo", new Range(13, 17)), "req_3");
		GuessResponse response3 = request3.createResponse("p3", 15);
		driver.onResponse(response3);

		verify(callback).onGuess(eq("foo"), eq("p3"));
		verify(callback).onCorrectGuess(eq("foo"), eq("p3"));
	}

	public static class GuessRequestMatcher extends ArgumentMatcher<GuessRequest> {
		private final int lower;
		private final int upper;

		public GuessRequestMatcher(int lower, int upper) {
			this.lower = lower;
			this.upper = upper;
		}


		@Override
		public boolean matches(Object argument) {
			GuessRequest req = (GuessRequest) argument;
			return req.getGame().getRange().getLowerBound() == lower &&
					req.getGame().getRange().getUpperBound() == upper;
		}
	}

}
