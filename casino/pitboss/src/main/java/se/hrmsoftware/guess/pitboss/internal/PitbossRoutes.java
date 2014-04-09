package se.hrmsoftware.guess.pitboss.internal;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import org.apache.camel.builder.RouteBuilder;

@Component(provide = {RouteBuilder.class}, configurationPolicy = ConfigurationPolicy.require, designate = PitbossRoutes.Config.class)
public class PitbossRoutes extends RouteBuilder {
	@Override
	public void configure() throws Exception {

	}

	public interface Config {

	}
}
