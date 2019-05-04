package Cliente;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneradorSS {
	private LoadGenerator generator;
	int numberOfTasks=1000;
	int gapBetweenTaks=5;

	public GeneradorSS()
	{
		Task work=createTask();
		generator=new LoadGenerator("Client - Server Load Test",numberOfTasks,work,gapBetweenTaks);
		generator.generate();
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
