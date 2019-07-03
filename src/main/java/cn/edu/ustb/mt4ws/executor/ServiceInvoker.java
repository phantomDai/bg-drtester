package cn.edu.ustb.mt4ws.executor;

public abstract class ServiceInvoker implements Invoker{
	
	public ServiceInvoker(){
		
	}
	
	public abstract Object invoke(Object request);

}
