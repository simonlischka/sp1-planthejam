package de.jetty_jearsey.rest_v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/best") //root path
public class Test {
	  public static final String CLICHED_MESSAGE = "Hello World!!!!!!!!!!!!!!!!";
	  
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	      public String getHello() {
	          return CLICHED_MESSAGE;
	      }
}
