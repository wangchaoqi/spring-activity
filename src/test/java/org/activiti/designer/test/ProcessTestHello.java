package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestHello {

	private String filename = "F:\\sound-code\\activiti\\src\\main\\resources\\diagrams\\Hello.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

//	@Test
//	public void startProcess() throws Exception {
//		RepositoryService repositoryService = activitiRule.getRepositoryService();
//		repositoryService.createDeployment().addInputStream("hello.bpmn20.xml",
//				new FileInputStream(filename)).deploy();
//		RuntimeService runtimeService = activitiRule.getRuntimeService();
//		Map<String, Object> variableMap = new HashMap<String, Object>();
//		variableMap.put("name", "Activiti");
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hello", variableMap);
//		assertNotNull(processInstance.getId());
//		System.out.println("id " + processInstance.getId() + " "
//				+ processInstance.getProcessDefinitionId());
//	}
	
	@Test  
	public void queryProcdef(){  
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	    RepositoryService repositoryService = processEngine.getRepositoryService();  
	    //创建查询对象  
	    ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();  
	    //添加查询条件  
	    query.processDefinitionKey("员工请假");//通过key获取  
	        // .processDefinitionName("员工请假")//通过name获取  
	        // .orderByProcessDefinitionId()//根据ID排序  
	    //执行查询获取流程定义明细  
	    List<ProcessDefinition> pds = query.list();  
	    for (ProcessDefinition pd : pds) {  
	        System.out.println("ID:"+pd.getId()+",NAME:"+pd.getName()+",KEY:"+pd.getKey()+",VERSION:"+pd.getVersion()+",RESOURCE_NAME:"+pd.getResourceName()+",DGRM_RESOURCE_NAME:"+pd.getDiagramResourceName());  
	    }  
	}
}