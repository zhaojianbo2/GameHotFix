package hotfixmain;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * 
 * @author WinkeyZhao
 *
 *         开始执行hotFix 
 *         注意事项:maven中对tools.jar的引用,windows和linux的tools.jar不能共用,注意不同系统依赖不同的tools.jar
 */
public class FixMain {

    /**
     * 
     * @param args 固定5个参数如下注释
     * @throws AgentInitializationException
     * @throws AgentLoadException
     * @throws IOException
     * @throws AttachNotSupportedException
     */
    public static void main(String[] args)
	    throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
	if (args == null || args.length < 5) {
	    System.out.println("args not enough please check it");
	    return;
	}
	String PidOrSname = args[0];// pid或者serverName
	String fullClassPath = args[1];// class 文件全路径
	String fullClassName = args[2];// class 全类名
	String JavaAgentJarPath = args[3];// JavaAgentMain 打包好的jar所存放的全路径
	String type = args[4];// -P pId方式 -S serverName方式
	String osName = System.getProperty("os.name");
	if (osName.startsWith("Windows")) {
	    // windows分隔符替换
	    fullClassPath = fullClassPath.replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
	    JavaAgentJarPath = JavaAgentJarPath.replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
	}
	String JavaAgentMainargs = fullClassPath + "," + fullClassName;// 拼接传递给agentmain的参数

	// pid方式查找
	if (type.equalsIgnoreCase("-p")) {
	    fixByPid(PidOrSname, JavaAgentJarPath, JavaAgentMainargs);
	} else if (type.equalsIgnoreCase("-s")) {
	    // vmName方式查找,最终还是使用pId进行attach，不建议使用这种方式
	    fixByServerName(PidOrSname, JavaAgentJarPath, JavaAgentMainargs);
	}
    }

    /**
     * 
     * @param pid               游戏进程pId
     * @param JavaAgentJarPath  打包好的 JavaAgentMain jar全路径
     * @param JavaAgentMainargs JavaAgentMain 中agentmain()方法中args参数
     * @throws AttachNotSupportedException
     * @throws IOException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    private static void fixByPid(String pid, String JavaAgentJarPath, String JavaAgentMainargs)
	    throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
	VirtualMachine vm = VirtualMachine.attach(pid);
	loadAgent(vm, JavaAgentJarPath, JavaAgentMainargs);

    }

    private static void fixByServerName(String serverName, String JavaAgentJarPath, String JavaAgentMainargs)
	    throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
	List<VirtualMachineDescriptor> list = VirtualMachine.list();
	for (VirtualMachineDescriptor vDescriptor : list) {
	    String vmName = vDescriptor.displayName();
	    System.out.println("vmName:" + vmName);
	    if (serverName.equals(vmName)) {
		System.out.println("find vmName:" + vmName);
		fixByPid(vDescriptor.id(), JavaAgentJarPath, JavaAgentMainargs);
	    }
	}
    }

    private static void loadAgent(VirtualMachine vm, String JavaAgentJarPath, String JavaAgentMainargs)
	    throws AgentLoadException, AgentInitializationException, IOException {
	System.out.println("=====begin attach=====");
	vm.loadAgent(JavaAgentJarPath, JavaAgentMainargs);
	vm.detach();
	System.out.println("=====end detach=====");
    }
}
