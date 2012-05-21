package org.ecocean.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.external.ExternalTestContainerFactory;
import org.ecocean.data.Encounter;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author mmcbride
 */
public class EncountersIT extends JerseyTest {

  public EncountersIT() throws Exception {
    super(new WebAppDescriptor.Builder().
            servletClass(com.sun.jersey.spi.container.servlet.ServletContainer.class).
            contextPath("/shepherd").
            servletPath("/api").
            initParam(JSONConfiguration.FEATURE_POJO_MAPPING, "true").
            build());
  }

  @Override
  protected Client getClient(TestContainer tc, AppDescriptor ad) {
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    return Client.create(clientConfig);
  }

  @Test
  public void testPost() throws IOException {
    WebResource webResource = resource();
    BufferedReader reader = new BufferedReader(new FileReader(new File("foo.json")));
    String line;
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }
    Encounter responseMsg = webResource.path("/encounters.json").type(MediaType.APPLICATION_JSON).post(Encounter.class, stringBuilder.toString());
    assertNotNull(responseMsg);
  }

  @Override
  protected TestContainerFactory getTestContainerFactory() {
    ExternalTestContainerFactory etcf = new ExternalTestContainerFactory();
    return etcf;
  }

  @Override
  protected int getPort(int defaultPort) {
    return 9090;
  }


}
