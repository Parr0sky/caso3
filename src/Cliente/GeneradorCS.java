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
	}

	private Task createTask()
	{
		return new ClienteServerTaskCS();
	}


	public static void main (String ... args)
	{
		@SuppressWarnings("unused")
		GeneradorCS gen = new GeneradorCS();
	}
}
