package me.ChristopherW.core;

public interface ILogic {
    void init() throws Exception;
    void input(MouseInput input, double deltaTime, int frame);
    void update(float inteveral, MouseInput input);
    void render() throws Exception;
    void cleanup();
}
