package main.java.br.abevieiramota.activiti;

import java.util.List;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class Main {
	public static void main(String[] args) {
		// get engine
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();

		// deploy process
		engine.getRepositoryService().createDeployment().addClasspathResource("process.bpmn20.xml").deploy();

		// start instance
		ProcessInstance pi = engine.getRuntimeService().startProcessInstanceByKey("autorizarHoraExtra");

		// get process instance historic
		HistoricProcessInstance piHistoric = engine.getHistoryService().createHistoricProcessInstanceQuery()
				.processInstanceId(pi.getId()).singleResult();

		System.out.println("Process instance start time: " + piHistoric.getStartTime());

		// get taskService
		List<Task> tasksDoServidor = engine.getTaskService().createTaskQuery().taskCandidateGroup("servidor").list();

		// por grupo 'servidor'
		System.out.println();
		System.out.println("Tasks do grupo servidor:");
		for (Task taskDoServidor : tasksDoServidor) {
			System.out.println(taskDoServidor.getProcessDefinitionId());
		}

		// adiciona usuário
		User usrAbe = engine.getIdentityService().newUser("abevieiramota");
		usrAbe.setFirstName("Abelardo");
		usrAbe.setEmail("abevieiramota@gmail.com");

		engine.getIdentityService().saveUser(usrAbe);

		// adiciona grupo
		Group groupServidor = engine.getIdentityService().newGroup("servidor");
		groupServidor.setName("Servidores da UFC");

		engine.getIdentityService().saveGroup(groupServidor);

		// adiciona abevieiramota ao grupo servidor
		engine.getIdentityService().createMembership("abevieiramota", "servidor");

		Task task1 = engine.getTaskService().createTaskQuery().taskCandidateUser("abevieiramota")
				.singleResult();

		System.out.println();
		System.out.println("Tasks que o abzão pode pegar:");
		System.out.println(task1.getDescription());

		// clain task
		engine.getTaskService().claim(task1.getId(), "abevieiramota");

		List<Task> tasksAssignedToAbe = engine.getTaskService().createTaskQuery().taskAssignee("abevieiramota").list();

		System.out.println();
		System.out.println("Tasks atribuídas ao abzão:");
		for (Task taskAssignedAbe : tasksAssignedToAbe) {
			System.out.println(taskAssignedAbe.getDescription());
		}

		// tenta atribuir task já atribuída
		try {
			engine.getTaskService().claim(task1.getId(), "oi");
		} catch (ActivitiTaskAlreadyClaimedException ex) {
			System.out.println("A task é minha!!!!!! - abzão");
		}

		engine.getTaskService().complete(task1.getId());

		// refresh task
		HistoricTaskInstance task1Historic = engine.getHistoryService().createHistoricTaskInstanceQuery()
				.taskId(task1.getId()).singleResult();

		System.out.println();
		System.out.println("Comecei em: " + task1Historic.getStartTime());
		System.out.println("Terminei em: " + task1Historic.getEndTime());

		List<Task> demaisTasks = engine.getTaskService().createTaskQuery()
				.processDefinitionId(pi.getProcessDefinitionId()).list();

		for (Task resto : demaisTasks) {
			engine.getTaskService().claim(resto.getId(), "diretor");
			engine.getTaskService().complete(resto.getId());
		}

		piHistoric = engine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(pi.getId())
				.singleResult();

		System.out.println();
		System.out.println("Fim do processo: " + piHistoric.getEndTime());

	}
}
