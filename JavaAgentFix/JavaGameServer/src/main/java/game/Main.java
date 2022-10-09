package game;

/**
 * 
 * @author WinkeyZhao
 *
 *         模拟游戏服启动
 */
public class Main {

    /**
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
	GameLogic g = new GameLogic();
	while (true) {
	    Thread.sleep(10000);
	    g.action();
	}
    }
}
