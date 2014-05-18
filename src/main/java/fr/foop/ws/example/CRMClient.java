package fr.foop.ws.example;

import com.predic8.wsdl.crm.crmservice._1.CRMServicePT;
import com.predic8.wsdl.crm.crmservice._1.CustomerService;

import fr.foop.ws.CxfClient;
import fr.foop.ws.CxfClientBuilder;

public class CRMClient extends CxfClient<CRMServicePT, CustomerService> {

	public static CxfClientBuilder builder() {
		return new CxfClientBuilder();
	}
	
	public CRMClient(final CxfClientBuilder builder) {
		super(builder, CustomerService.class);
		
	}
	
	public CRMServicePT newPort(final CustomerService serviceManager) {
		return serviceManager.getCRMServicePTPort();
	}
	
	@Override
	public void checkIfPortUp(CRMServicePT port) throws Exception {
		port.getAll();
	}
	
	

}