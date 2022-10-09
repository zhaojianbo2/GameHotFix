package agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;


/**
 * 
 * @author WinkeyZhao
 *
 *         JVM启动后的java探针执行的方法agentmain
 */
public class JavaAgentMain {

    /**
     * 
     * @param args [class文件全路径,className全类名] 拼接
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String args, Instrumentation inst) throws Exception {
	System.out.println("agentmain called!!!");
	// args用逗号拼接的字符串,第一个是class文件路劲,用于读取class文件类容,第二个是class全类名(程序中可能会出现重复的classsimpleName,
	// 所以必须用全类名查找)
	String[] argAry = args.split(",");
	String fullFilePath = argAry[0];// class文件路劲
	System.out.println("fullFilePath:" + fullFilePath);
	String fullClassName = argAry[1];// class 全类名
	System.out.println("fixed fullClassName:" + fullClassName);
	// 获取所有已经加载的class
	Class<?>[] allClass = inst.getAllLoadedClasses();
	boolean success = false;
	for (Class<?> c : allClass) {
	    if (c.getName().equals(fullClassName)) {
		File file = new File(fullFilePath);
		try {
		    byte[] bytes = fileToBytes(file);
		    System.out.println("fileSize:" + bytes.length);
		    ClassDefinition classDefinition = new ClassDefinition(c, bytes);
		    inst.redefineClasses(classDefinition);
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("hot fix exception:" + e);
		    success = false;
		}
		System.out.println("finded class and fixing!!!");
		success = true;
	    }
	}
	if (success) {
	    System.out.println("hot fix success!!!");
	} else {
	    System.out.println("hot fix fail!!!");
	}

    }

    public static byte[] fileToBytes(File file) throws IOException {
	FileInputStream in = new FileInputStream(file);
	byte[] bytes = new byte[in.available()];
	in.read(bytes);
	in.close();
	return bytes;
    }
}
