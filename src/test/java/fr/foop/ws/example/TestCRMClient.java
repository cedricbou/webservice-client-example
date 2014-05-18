package fr.foop.ws.example;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.foop.ws.example.CRMClient;

public class TestCRMClient {

	public TestCRMClient() {
	}

	@Test
	public void testGetAll() {
		final CRMClient client = CRMClient.builder()
			.withConnectionTimeout(2000)
			.withReceiveTimeout(5000).build(CRMClient.class);
		
		assertTrue(client.service() != null);
		assertTrue(client.service().getAll().size() > 0);
	}


}
