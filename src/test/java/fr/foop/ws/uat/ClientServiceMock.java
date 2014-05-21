package fr.foop.ws.uat;

import java.util.List;

import com.predic8.crm._1.CustomerType;
import com.predic8.wsdl.crm.crmservice._1.CRMServicePT;

public class ClientServiceMock implements CRMServicePT {

	@Override
	public List<CustomerType> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(CustomerType customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CustomerType get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
