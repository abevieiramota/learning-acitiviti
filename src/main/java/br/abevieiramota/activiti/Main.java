package main.java.br.abevieiramota.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;

public class Main {
	public static void main(String[] args) {
		ProcessEngine pe = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repSrvc = pe.getRepositoryService();
		RuntimeService runSrvc = pe.getRuntimeService();
		repSrvc.createDeployment().addClasspathResource("process.bpmn20.xml").deploy();
		ProcessInstance pi = runSrvc.startProcessInstanceByKey("financialReport");

		HistoryService histSrvc = pe.getHistoryService();
		HistoricProcessInstance histPI = histSrvc.createHistoricProcessInstanceQuery().processInstanceId(pi.getId())
				.singleResult();

		System.out.println("Process instance start time: " + histPI.getStartTime());
	}
}
