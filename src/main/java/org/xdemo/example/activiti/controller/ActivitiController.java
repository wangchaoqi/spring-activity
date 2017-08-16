package org.xdemo.example.activiti.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDiagramCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/process")
public class ActivitiController {

	@Resource
	ProcessEngine engine;

	  @RequestMapping(value = "/test1")
	    @ResponseBody
	    public String test(){
	        // 加载配置文件activiti.cfg.xml，创建引擎，如果出现null，可能原因
	        //1.加载路径不是根目录。
	        //2.依赖包不完全
	        // 获取配置文件后，引擎开始创建数据库。
	        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
	        // 获取流程储存服务组件
	        RepositoryService rs = engine.getRepositoryService();
	        // 获取运行时服务组件
	        RuntimeService rse = engine.getRuntimeService();
	        // 获取流程中的任务TASK组件
	        TaskService ts = engine.getTaskService();
	        // 部署流程，只要是符合BPMN2规范的XML文件，理论上都可以被ACTIVITI部署
	        rs.createDeployment().addClasspathResource("diagrams/Hello.bpmn").deploy();
	        // 开启流程，myprocess是流程的ID
	        rse.startProcessInstanceByKey("hello");
	        // 查询历史表中的Task
	        List<Task> task = ts.createTaskQuery().list();
	        Task task1 = task.get(task.size()-1);
	        System.out.println("第一环节："+task1);
	        System.out.println("推动流程到下一环节："+task1);
	        ts.complete(task1.getId());
	        task1 = ts.createTaskQuery().executionId(task1.getExecutionId()).singleResult();
	        System.out.println("第二环节：" + task1);
	        return "test";
	    }
	  
	  
//	  	@Autowired
//	    RepositoryService repositoryService;
//	    @Autowired
//	    RuntimeService runtimeService;
//	    @Autowired
//	    TaskService taskService;
//
//	    @RequestMapping(value = "/test2")
//	    @ResponseBody
//	    public String test2(){
//	        StringBuffer sb = new StringBuffer();
//	        // 部署流程，只要是符合BPMN2规范的XML文件，理论上都可以被ACTIVITI部署
//	        repositoryService.createDeployment().addClasspathResource("diagrams/Hello.bpmn").deploy();
//	        // 开启流程，myprocess是流程的ID
//	        runtimeService.startProcessInstanceByKey("hello");
//	        // 查询历史表中的Task
//	        List<Task> task = taskService.createTaskQuery().list();
//	        Task task1 = task.get(task.size()-1);
//	        sb.append("第一环节："+task1 +"<br/>");
//	        sb.append("推动流程到下一环节："+task1+"<br/>");
//	        taskService.complete(task1.getId());
//	        task1 = taskService.createTaskQuery().executionId(task1.getExecutionId()).singleResult();
//	        sb.append("第二环节：" + task1+"<br/>");
//	        return sb.toString();
//	    }
	  
	/**
	 * 列出所有流程模板
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.addObject("list", Util.list());
		mav.setViewName("process/template");
		return mav;
	}

	/**
	 * 部署流程
	 */
	@RequestMapping("deploy")
	public ModelAndView deploy(String processName, ModelAndView mav) {

		RepositoryService service = engine.getRepositoryService();

		if (null != processName)
			service.createDeployment()
					.addClasspathResource("diagrams/" + processName).deploy();

		List<ProcessDefinition> list = service.createProcessDefinitionQuery()
				.list();

		mav.addObject("list", list);
		mav.setViewName("process/deployed");
		return mav;
	}

	/**
	 * 已部署流程列表
	 */
	@RequestMapping("deployed")
	public ModelAndView deployed(ModelAndView mav) {

		RepositoryService service = engine.getRepositoryService();

		List<ProcessDefinition> list = service.createProcessDefinitionQuery()
				.list();

		mav.addObject("list", list);
		mav.setViewName("process/deployed");

		return mav;
	}

	/**
	 * 启动一个流程实例
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("start")
	public ModelAndView start(String id, ModelAndView mav) {

		RuntimeService service = engine.getRuntimeService();

		service.startProcessInstanceById(id);

		List<ProcessInstance> list = service.createProcessInstanceQuery()
				.list();

		mav.addObject("list", list);
		mav.setViewName("process/started");

		return mav;
	}

	/**
	 * 所有已启动流程实例
	 */
	@RequestMapping("started")
	public ModelAndView started(ModelAndView mav) {

		RuntimeService service = engine.getRuntimeService();

		List<ProcessInstance> list = service.createProcessInstanceQuery()
				.list();

		mav.addObject("list", list);
		mav.setViewName("process/started");

		return mav;
	}
	
	@RequestMapping("task")
	public ModelAndView task(ModelAndView mav){
		TaskService service=engine.getTaskService();
		List<Task> list=service.createTaskQuery().list();
		mav.addObject("list", list);
		mav.setViewName("process/task");
		return mav;
	}
	
	@RequestMapping("complete")
	public ModelAndView complete(ModelAndView mav,String id){
		
		TaskService service=engine.getTaskService();
		
		service.complete(id);
		
		return new ModelAndView("redirect:task");
	}

	/**
	 * 所有已启动流程实例
	 * 
	 * @throws IOException
	 */
	@RequestMapping("graphics")
	public void graphics(String definitionId, String instanceId,
			String taskId, ModelAndView mav, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("image/png");
		Command<InputStream> cmd = null;

		if (definitionId != null) {
			cmd = new GetDeploymentProcessDiagramCmd(definitionId);
		}

		if (instanceId != null) {
			cmd = new ProcessInstanceDiagramCmd(instanceId);
		}

		if (taskId != null) {
			Task task = engine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
			cmd = new ProcessInstanceDiagramCmd(
					task.getProcessInstanceId());
		}

		if (cmd != null) {
			InputStream is = engine.getManagementService().executeCommand(cmd);
			int len = 0;
			byte[] b = new byte[1024];
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		}
	}
}
