package se.hrmsoftware.guess;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MainTest extends CamelTestSupport {

	@Test
	public void testSimpleRound() throws InterruptedException {
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {
		return null;
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext theContext = super.createCamelContext();
		theContext.setTracing(false);
		return theContext;
	}


}
