package main.java.br.abevieiramota.activiti;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.task.Task;

public class Process2 {

	public static void main(String[] args) throws InterruptedException {
		// get engine
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

		// deploy process
		engine.getRepositoryService().createDeployment().addClasspathResource("process2.bpmn20.xml").deploy();
		
		// start instance
		engine.getRuntimeService().startProcessInstanceByKey("papocaTimer");
		
		List<Task> tarefas = null;
		while((tarefas = engine.getTaskService().createTaskQuery().taskCandidateGroup("pessoa").list()).isEmpty()) {
			Thread.currentThread().sleep(1000l);
			System.out.println("Ainda não chegou...");
		}
		
		System.out.println("Chegou!!!!!! " + tarefas.get(0));
	}

}
