package game;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


/**
 * 
 * @author WinkeyZhao
 *
 *         编译java文件
 */
public class JavaScriptCompiler {

    /**
     * 编译Java脚本需要的路径
     */
    private String compileClassPath;

    /**
     * 编译后输出的路径
     */
    private String compileOutputPath;

    /**
     * 编译选项
     */
    private List<String> compileOption;

    public JavaScriptCompiler(String javaFilePath, String outputPath) throws Exception {
	this.compileClassPath = javaFilePath;
	this.compileOutputPath = outputPath;
	// 准备编译路径
	// 当前类本身使用的classLoader,从jar包运行，默认为URLClassLoader(AppClassLoader)
	URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
	// 从当前类的编译环境中解析出编译需要的路径
	StringBuilder builder = new StringBuilder();
	for (URL url : classLoader.getURLs()) {
	    String p = url.getFile();
	    builder.append(p).append(File.pathSeparator);
	}
	this.compileClassPath = builder.toString();

	// 路径检查,IDE开发警告
	if (this.compileClassPath.contains(outputPath)) {
	    System.out.println("compileClassPath:" + compileClassPath);
	    System.out.println("outputPath:" + outputPath);
	    throw new Exception("appclassloader路径中包含了脚本编译路径");
	}

	if (this.compileClassPath.contains(javaFilePath)) {
	    System.out.println("compileClassPath:" + compileClassPath);
	    System.out.println("javaFilePath:" + javaFilePath);
	    throw new Exception("appclassloader路径中包含了脚本类路径");
	}

	Class<?> loadCheckClass = null;
	try {
	    loadCheckClass = classLoader.loadClass("LoadCheckScript");
	} catch (ClassNotFoundException e) {
	    System.out.println("appclassloader情况正常.");
	}

	if (loadCheckClass != null) {
	    throw new Exception("appClassLoader中出现了脚本类, class:" + loadCheckClass.getName() + "\n 当前系统的类加载路径:"
		    + this.compileClassPath);
	}

	// 编译后输出路径
	this.compileOutputPath = outputPath;
	_prepareScriptPath(this.compileOutputPath);

	// 准备编译选项
	this.compileOption = new ArrayList<>();
	this.compileOption.add("-encoding");
	this.compileOption.add("UTF-8");
	this.compileOption.add("-classpath");
	this.compileOption.add(this.compileClassPath);
	this.compileOption.add("-d");
	this.compileOption.add(this.compileOutputPath);
    }

    /**
     * 用于编译java源码的内部结构
     */
    private class JavaSourceFromString extends SimpleJavaFileObject {
	private final String m_code;

	/**
	 * Construct a JavaSourceFromString of the given name and with the given code.
	 */
	protected JavaSourceFromString(String name, String code) {
	    super(URI.create("string:///" + name), Kind.SOURCE);
	    this.m_code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
	    return m_code;
	}
    }

    /**
     * 
     * @param javaFile
     * @param bytes
     * @return
     * @throws Exception
     */
    public Class<?> buildScript(File javaFile, byte[] bytes) throws Exception {
	Class<?> clazz = javaCodeToObject(javaFile,new String(bytes, "UTF-8"));
	if (clazz != null) {
	    return clazz;
	} else {
	    throw new Exception("clazz == null , javaFile:" + javaFile);
	}
    }

    /**
     * 
     * @param javaFile
     * @param code
     * @return
     * @throws Exception
     */
    private Class<?> javaCodeToObject(File javaFile,String code) throws Exception {
	// 准备编译器
	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
	StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null);
	List<JavaFileObject> jFiles = new ArrayList<>();
	String path = javaFile.getPath();
	int index = path.indexOf(File.separator, 2);
	path = path.substring(index+1);
	path = path.replaceAll("\\\\", "/");
	jFiles.add(new JavaSourceFromString(path, code));

	// 开始编译
	Class<?> result = null;
	JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, compileOption,
		null, jFiles);
	if (!task.call()) {
	    String error = "";
	    for (Diagnostic<?> diagnostic : diagnosticCollector.getDiagnostics()) {
		error = error + _CompileErrorString(diagnostic);
	    }

	    throw new Exception("类编译错误:" + error);
	} else {
	    // 成功编译成.class文件，通过classLoader加载class
	    try {
		String classFullName = path.replace(".java", "").replace("/", ".");
		result = new JavaScriptLoader(this.compileOutputPath).loadClass(classFullName);
	    } catch (Exception e) {
		throw new Exception("类加载错误:" + e);
	    }
	}

	fileManager.close();
	return result;
    }

    /**
     * 格式化编译错误信息
     *
     * @param diagnostic
     * @return
     */
    private String _CompileErrorString(Diagnostic<?> diagnostic) {
	StringBuilder res = new StringBuilder();
	res.append("Code:[").append(diagnostic.getCode()).append("]\n");
	res.append("Kind:[").append(diagnostic.getKind()).append("]\n");
	res.append("Position:[").append(diagnostic.getPosition()).append("]\n");
	res.append("Start Position:[").append(diagnostic.getStartPosition()).append("]\n");
	res.append("End Position:[").append(diagnostic.getEndPosition()).append("]\n");
	res.append("Source:[").append(diagnostic.getSource()).append("]\n");
	res.append("Message:[").append(diagnostic.getMessage(null)).append("]\n");
	res.append("LineNumber:[").append(diagnostic.getLineNumber()).append("]\n");
	res.append("ColumnNumber:[").append(diagnostic.getColumnNumber()).append("]\n");
	return res.toString();
    }

    /**
     * 脚本路径初始化
     */
    private void _prepareScriptPath(String outputPath) {
	File file = new File(outputPath);
	if (file.exists()) {
	    _deleteScriptPath(file);
	}
	file.mkdirs();
    }

    private void _deleteScriptPath(File file) {
	if (file.isFile()) {
	    file.delete();
	} else if (file.isDirectory()) {
	    File[] files = file.listFiles();
	    for (int a = 0; a < files.length; ++a) {
		_deleteScriptPath(files[a]);
	    }
	}
    }

}
