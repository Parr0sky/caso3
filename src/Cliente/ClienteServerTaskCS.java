package Cliente;

import uniandes.gload.core.Task;

public class ClienteServerTaskCS extends Task {

	public void execute() {
		try {
			ClienteCS maincs = new ClienteCS();
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
