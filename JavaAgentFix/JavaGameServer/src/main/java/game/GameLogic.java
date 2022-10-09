package game;

import java.lang.management.ManagementFactory;

/**
 * 
 * @author WinkeyZhao
 *
 * 模拟游戏的执行逻辑(即正在运行的游戏app)
 */
public class GameLogic {

    public void action() {
	String name = ManagementFactory.getRuntimeMXBean().getName();
	//这里为了方便测试，打印出来进程id
	String pid = name.split("@")[0];
	System.out.println("===do action 2===");
	System.out.println("进程Id：" + pid);
	
    }
}
