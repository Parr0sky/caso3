package Cliente;

import uniandes.gload.core.Task;

public class ClienteServerTaskSS extends Task {

	public void execute() {
		try {
			ClienteSS mainss = new ClienteSS();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
	}
	
	public void success()
	{
		System.out.println(Task.OK_MESSAGE);
	}
}