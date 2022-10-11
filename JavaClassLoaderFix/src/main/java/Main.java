
import game.GameManager;
import game.ScriptManager;

/**
 * 
 * @author WinkeyZhao
 *
 * 
 */
public class Main {

    public static void main(String[] args) {
	try {
	    // 初始化脚本
	    ScriptManager.getInstance().initFromJavaSourcePath("./scripts", "./scriptBin");
	    GameManager manager = new GameManager();
	    int count = 0;
	    while (true) {
		manager.actionA();
		manager.actionB();
		//测试,每执行5次就重载,在这期间的修改都会在重载后生效
		if (++count % 5 == 0) {
		    ScriptManager.getInstance().reloadScript();
		}
		Thread.sleep(10000);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
