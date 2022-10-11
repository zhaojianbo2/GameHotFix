package base;

import game.scriptface.IGameScriptBFace;

public class GameScriptB2 implements IGameScriptBFace {

    //value的值在重载后 不会被缓存
    private int value = 0;

    @Override
    public int getId() {
	return 2;
    }

    @Override
    public void printB() {
	value++;
	System.out.println("printB====b value:" + value);
    }

}
