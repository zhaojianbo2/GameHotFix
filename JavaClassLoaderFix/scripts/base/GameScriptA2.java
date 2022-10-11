package base;

import game.scriptface.IGameScriptAFace;

public class GameScriptA2 implements IGameScriptAFace{

    @Override
    public int getId() {
	return 1;
    }

    @Override
    public void printA() {
	System.out.println("printA====a+1");
    }

}
