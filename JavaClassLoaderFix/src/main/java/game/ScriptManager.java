package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author WinkeyZhao
 *
 *         执行脚本管理器
 */
public class ScriptManager {

    private Map<Integer, IScript> scriptMap = new HashMap<Integer, IScript>();
    private JavaScriptCompiler complier;
    private String scriptBasePkg;

    /**
     * 重载脚本
     * @throws Exception
     */
    public void reloadScript() throws Exception {
	List<File> javaFileList = getAllScriptFiles(scriptBasePkg);
	for (File javaFile : javaFileList) {
	    Class<?> clazz = complier.buildScript(javaFile,readJavaSourceFile(javaFile));
	    if (IScript.class.isAssignableFrom(clazz)) {
		IScript scriptInstance = (IScript) clazz.newInstance();
		scriptMap.put(scriptInstance.getId(), scriptInstance);
	    }
	}
    }
    
    /**
     * 
     * @param scriptBasePkg
     * @param outPath
     * @throws Exception
     */
    public void initFromJavaSourcePath(String scriptBasePkg, String outPath) throws Exception {
	this.complier = new JavaScriptCompiler(scriptBasePkg, outPath);
	this.scriptBasePkg = scriptBasePkg;
	List<File> javaFileList = getAllScriptFiles(scriptBasePkg);
	for (File javaFile : javaFileList) {
	    Class<?> clazz = complier.buildScript(javaFile,readJavaSourceFile(javaFile));
	    if (IScript.class.isAssignableFrom(clazz)) {
		IScript scriptInstance = (IScript) clazz.newInstance();
		scriptMap.put(scriptInstance.getId(), scriptInstance);
	    }
	}
    }

    /**
     * 获取所有javaFile
     * 
     * @param scriptBasePkg
     * @return
     */
    private List<File> getAllScriptFiles(String scriptBasePkg) {
	List<File> fileList = new ArrayList<File>();
	File f = new File(scriptBasePkg);
	if (!f.exists() || !f.isDirectory()) {
	    System.out.println("指定的脚本路径不存在！");
	    return fileList;
	}
	searchAllFile(f, fileList);
	return fileList;
    }

    /**
     * 查找所有的脚本
     * 
     * @param file
     * @param fileList
     */
    private void searchAllFile(File file, List<File> fileList) {
	File[] files = file.listFiles();
	if (files == null || files.length <= 0) {
	    return;
	}
	for (File f : files) {
	    String fName = f.getName();
	    if (f.isFile() && fName.endsWith(".java")) {
		fileList.add(f);
	    } else if (f.isDirectory()) {
		searchAllFile(f, fileList);
	    }
	}
    }

    private byte[] readJavaSourceFile(File javaFile) {

	try (InputStream steam = new FileInputStream(javaFile)) {
	    byte[] bytes = new byte[(int) javaFile.length()];
	    steam.read(bytes);
	    return bytes;
	} catch (Exception e) {
	    System.out.println(e);
	}
	return null;
    }

    /**
     * 从javaClass文件加载
     * 
     * @param javaClassPath
     */
    public void initFromJavaClassPath(String javaClassPath) {

    }

    private ScriptManager() {
    };
    
    @SuppressWarnings("unchecked")
    public <T> T getScript(int id) {
	return (T) scriptMap.get(id);
    }
    
    public static ScriptManager getInstance() {
	return InstanceHolder.instance;
    }

    private static class InstanceHolder {
	private static ScriptManager instance = new ScriptManager();
    }

}
