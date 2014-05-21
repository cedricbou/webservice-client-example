package fr.foop.ws.uat;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.addRequestProcessingDelay;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.xml.ws.WebServiceException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.predic8.wsdl.crm.crmservice._1.CRMServicePT;

import fr.foop.ws.example.CRMClient;

public class UsageUATest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args
																// constructor
																// defaults to
																// port 8080

	@Test
	public void defaultConfigurationTest() throws IOException {
		stubFor(post(urlEqualTo("/crm/CustomerService")).withHeader(
				"SOAPAction", equalTo("\"getAll_action\"")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/xml")
						.withBodyFile("soap-bodies/getall.xml")));

		final CRMClient client = CRMClient.builder()
				.withEndpoint("http://{{server}}/crm/CustomerService")
				.withServers("localhost:8089").build(CRMClient.class);

		client.service().getAll();

		verify(2,
				postRequestedFor(urlMatching("/crm/CustomerService"))
						.withRequestBody(containing(":getAll")).withHeader(
								"Content-Type", containing(("text/xml"))));
	}

	@Test
	public void defaultReceiveTimeoutTest() throws IOException {
		stubFor(post(urlEqualTo("/crm/CustomerService")).withHeader(
				"SOAPAction", equalTo("\"getAll_action\"")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/xml")
						.withBodyFile("soap-bodies/getall.xml")
						.withFixedDelay(3500))); // Default timeout for receive
													// is about 3 seconds.

		final CRMClient client = CRMClient.builder()
				.withEndpoint("http://localhost:8089/crm/CustomerService")
				.withConnectionTimeout(99000) // Disable connection timeout
				.build(CRMClient.class);

		try {
			client.service().getAll();
			fail("should have timed  out after 3 seconds");
		} catch (WebServiceException wse) {
			assertTrue(wse.getCause() instanceof SocketTimeoutException);
		}

		verify(1,
				postRequestedFor(urlMatching("/crm/CustomerService"))
						.withRequestBody(containing(":getAll")).withHeader(
								"Content-Type", containing(("text/xml"))));
	}

	@Test
	@Ignore("connection timeout looks like it is not working ? FIXIT")
	public void defaultConnectionTimeoutTest() throws IOException {
		addRequestProcessingDelay(1500);
		stubFor(post(urlEqualTo("/crm/CustomerService")).withHeader(
				"SOAPAction", equalTo("\"getAll_action\"")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/xml")
						.withBodyFile("soap-bodies/getall.xml")));

		final CRMClient client = CRMClient.builder()
				.withEndpoint("http://localhost:8089/crm/CustomerService")
				.withReceiveTimeout(99000) // Disable receive timeout
				.build(CRMClient.class);

		try {
			client.service().getAll();
			fail("should have timed  out after 1 seconds");
		} catch (WebServiceException wse) {
			assertTrue(wse.getCause() instanceof SocketTimeoutException);
		}

		verify(1,
				postRequestedFor(urlMatching("/crm/CustomerService"))
						.withRequestBody(containing(":getAll")).withHeader(
								"Content-Type", containing(("text/xml"))));
	}

	@Test
	public void withWsseCredentialsTest() throws IOException {
		stubFor(post(urlEqualTo("/crm/CustomerService")).withHeader(
				"SOAPAction", equalTo("\"getAll_action\"")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/xml")
						.withBodyFile("soap-bodies/getall.xml")));

		final CRMClient client = CRMClient.builder()
				.withEndpoint("http://localhost:8089/crm/CustomerService")
				.withWsseCredentials("johndoe", "password!")
				.build(CRMClient.class);

		client.service().getAll();
		verify(1,
				postRequestedFor(urlMatching("/crm/CustomerService"))
						.withRequestBody(containing("password!"))
						.withRequestBody(containing("johndoe"))
						.withRequestBody(containing("wsse:"))
						.withRequestBody(containing("#PasswordText"))
						.withHeader(
								"Content-Type", containing(("text/xml"))));
	}

	
	@Test
	public void withPartialPropertiesTest() throws IOException {
		final CRMClient client = CRMClient.builder()
				.withProperties("partial.ws.props", ResourceUtils.props("prop-partial.properties"))
				.build(CRMClient.class);

		assertEquals("http://localhost:8089/crm/CustomerService", client.config().endpoint.get());
		assertEquals("fr.foop.wsLogger", client.config().inLogger.get().getName());
		assertFalse(client.config().outLogger.isPresent());
		assertEquals(1000, client.config().connectionTimeout);
		assertEquals(3000, client.config().receiveTimeout);	
		assertEquals(0, client.config().servers.size());
		assertFalse(client.config().wsseUser.isPresent());
		assertFalse(client.config().wssePwd.isPresent());
	}

	@Test
	public void withFullPropertiesTest() throws IOException {
		final CRMClient client = CRMClient.builder()
				.withProperties("full.ws.props.", ResourceUtils.props("prop-full.properties"))
				.build(CRMClient.class);

		assertFalse(client.config().endpoint.isPresent());
		assertFalse(client.config().inLogger.isPresent());
		assertEquals("fr.foop.wsLogger", client.config().outLogger.get().getName());
		assertEquals(9000, client.config().connectionTimeout);
		assertEquals(12000, client.config().receiveTimeout);	
		assertEquals(3, client.config().servers.size());
		assertEquals("johndoe", client.config().wsseUser.get());
		assertEquals("password!", client.config().wssePwd.get());
		assertEquals(true, client.config().useMock);
		assertTrue(client.config().mockedPort.get() instanceof ClientServiceMock);
	}
	
	@Test
	public void withMockedPort() throws IOException {
		final CRMClient client = CRMClient.builder()
				.withProperties("full.ws.props.", ResourceUtils.props("prop-full.properties"))
				.build(CRMClient.class);

		final CRMServicePT port = client.service();
		
		assertTrue(port instanceof ClientServiceMock);
	}

	
}
