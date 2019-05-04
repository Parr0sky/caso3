package Cliente;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneradorCS {
	private LoadGenerator generator;
	int numberOfTasks=1000;
	int gapBetweenTaks=5;

	public GeneradorCS()
	{
		Task work=createTask();
		generator=new LoadGenerator("Client - Server Load Test",numberOfTasks,work,gapBetweenTaks);
		generator.generate();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Task createTask()
	{
		return new ClienteServerTaskSS();
	}


	public static void main (String ... args)
	{
		@SuppressWarnings("unused")
		GeneradorSS gen = new GeneradorSS();
	}
}
