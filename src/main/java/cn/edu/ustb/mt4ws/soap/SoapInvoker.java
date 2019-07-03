package cn.edu.ustb.mt4ws.soap;

import cn.edu.ustb.mt4ws.executor.ServiceInvoker;

import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.Request.SubmitException;

public class SoapInvoker extends ServiceInvoker {

	public SoapInvoker() {
		super();
	}

	public Response invoke(WsdlRequest request) throws SubmitException {
		WsdlSubmit submit = (WsdlSubmit) request.submit(new WsdlSubmitContext(
				request), false);

		// wait for the response
		Response response = submit.getResponse();
		return response;
	}

	@Override
	public Object invoke(Object request) {
		if (request instanceof WsdlRequest) {
			try {
				WsdlRequest wsdlRequest = (WsdlRequest) request;
				return invoke(wsdlRequest);
			} catch (SubmitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}

}
