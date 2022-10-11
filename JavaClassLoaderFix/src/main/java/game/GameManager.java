package game;

import game.scriptface.IGameScriptAFace;
import game.scriptface.IGameScriptBFace;

/**
 * 
 * @author WinkeyZhao
 *
 *         游戏管理器
 */
public class GameManager {

    public void actionA() {
	IGameScriptAFace scriptA = ScriptManager.getInstance().getScript(1);
	scriptA.printA();
    }

    public void actionB() {
	IGameScriptBFace scriptB = ScriptManager.getInstance().getScript(2);
	scriptB.printB();
    }
}
