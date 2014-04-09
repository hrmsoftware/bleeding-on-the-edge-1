package se.hrmsoftware.guess.pitboss;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import se.hrmsoftware.guess.model.Game;
import se.hrmsoftware.guess.model.GuessRequest;
import se.hrmsoftware.guess.model.GuessResponse;
import se.hrmsoftware.guess.model.Range;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class GameDriverTest {

	private void assertRange(GuessRequest request, int lower, int upper) {
		Assert.assertEquals(lower, request.getGame().getRange().getLowerBound());
		Assert.assertEquals(upper, request.getGame().getRange().getUpperBound());
	}

	@Test
	public void testScenario() {
		GameDriver.Callback callback = mock(GameDriver.Callback.class);
		final GameSetup gameSetup = new GameSetup(new Game("foo", new Range(10, 20)), 15);

		GameDriver driver = new GameDriver(gameSetup, callback);

		driver.sendInitialRequest();
		ArgumentCaptor<GuessRequest> requestCaptor = ArgumentCaptor.forClass(GuessRequest.class);
		verify(callback).postRequest(requestCaptor.capture());
		reset(callback);
		GuessResponse response = requestCaptor.getValue().createResponse("p1", 12);
		driver.onResponse(response);

		verify(callback).onGuess(eq("foo"), eq("p1"));
		verify(callback).postRequest(requestCaptor.capture()); // 13, 20
		assertRange(requestCaptor.getValue(), 13, 20);
		reset(callback);

		GuessResponse response2 = requestCaptor.getValue().createResponse("p2", 18);
		driver.onResponse(response2);

		verify(callback).onGuess(eq("foo"), eq("p2"));
		verify(callback).postRequest(requestCaptor.capture()); // 13, 17
		assertRange(requestCaptor.getValue(), 13, 17);
		reset(callback);

		GuessResponse response3 = requestCaptor.getValue().createResponse("p4", 17);
		driver.onResponse(response3);

		verify(callback).onGuess(eq("foo"), eq("p4"));
		verify(callback).postRequest(requestCaptor.capture());
		assertRange(requestCaptor.getValue(), 13, 16);
		reset(callback);

		GuessResponse response4 = requestCaptor.getValue().createResponse("p3", 15);
		driver.onResponse(response4);

		verify(callback).onGuess(eq("foo"), eq("p3"));
		verify(callback).onCorrectGuess(eq("foo"), eq("p3"));
	}


	@Test
	public void testCornerCase1() {
		Range range = new Range(172, 174);
		Range newRange = GameDriver.createNewRange(range, 173, 172);
		Assert.assertEquals(172, newRange.getLowerBound());
		Assert.assertEquals(173, newRange.getUpperBound());
	}

	@Test
	public void testCornerCase2() {
		Range range = new Range(172, 174);
		Range newRange = GameDriver.createNewRange(range, 173, 174);
		Assert.assertEquals(173, newRange.getLowerBound());
		Assert.assertEquals(174, newRange.getUpperBound());
	}
}
