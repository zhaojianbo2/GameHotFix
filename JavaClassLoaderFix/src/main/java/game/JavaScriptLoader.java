package game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author WinkeyZhao
 *
 *         自定义classLoader 加载每一个脚本
 */
public class JavaScriptLoader extends ClassLoader {

    /**
     * 编译后输出的路径
     */
    private String compileOutputPath;

    public JavaScriptLoader(String outPutPath) {
	this.compileOutputPath = outPutPath;
    }

    /**
     * 加载脚本
     *
     * @param classname
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> loadClass(String classname) throws ClassNotFoundException {
	String classFilePath = this.compileOutputPath + "/" + classname.replace('.', '/') + ".class";
	// 是否是脚本,非脚本则委托双亲进行调用
	File file = new File(classFilePath);
	if (!file.isFile()) {
	    return super.loadClass(classname);
	}

	if (!file.canRead()) {
	    System.out.println("class文件路径指向不是一个合法的文件或者该文件当前不可读: " + classFilePath);
	    return null;
	}
	Class<?> _class = this.findLoadedClass(classname);
	if (_class != null) {
	    System.out.println("_class is in this ScriptClassLoader:" + this.toString() + ",name:" + classname);
	    return null;
	}

	byte[] bytes = null;
	try {
	    bytes = loadClassBytes(classname);
	} catch (FileNotFoundException e) {
	    return null;
	} catch (IOException e) {
	    return null;
	}
	Class<?> clazz = this.defineClass(classname, bytes, 0, bytes.length);
	System.out.println("loadClass succes, Obj:" + this.toString() + ", name:" + classname);
	return clazz;
    }

    /**
     * 读取字节码
     *
     * @param name
     * @return
     * @throws IOException
     */
    private byte[] loadClassBytes(String name) throws IOException {
	int readCount = 0;
	String classFileName = this.compileOutputPath + "/" + name.replace('.', '/') + ".class";
	FileInputStream in = null;
	ByteArrayOutputStream buffer = null;
	try {
	    in = new FileInputStream(classFileName);
	    buffer = new ByteArrayOutputStream();
	    while ((readCount = in.read()) != -1) {
		buffer.write(readCount);
	    }
	    return buffer.toByteArray();
	} finally {
	    if (in != null) {
		in.close();
	    }
	    if (buffer != null) {
		buffer.close();
	    }
	}
    }

}
