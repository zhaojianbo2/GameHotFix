package game.scriptface;

import game.IScript;

/**
 * 
 * @author WinkeyZhao
 *
 * 让脚本实现此借口,因为GameManager调用脚本的时候,不能强转为实例,脚本实例在脚本classloader当中,会出现强转错误,所以用借口标签接收
 */
public interface IGameScriptAFace extends IScript{

    public void printA();
}
