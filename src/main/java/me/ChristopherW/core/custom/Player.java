package me.ChristopherW.core.custom;

public class Player {
    private int lives;
    private int money;

    public Player() {
        this.lives = 100;
        this.money = 6000;
    }

    public int getLives() {
        return lives;
    }
    public void removeLives(int x) {
        this.lives -= x;
        this.lives = Math.max(lives, 0);
    }
    public void setLives(int x) {
        this.lives = x;
        this.lives = Math.max(lives, 0);
    }

    public int getMoney() {
        return money;
    }
    public void setMoney(int x) {
        this.money = x;
        this.money = Math.max(money, 0);
    }
    public void addMoney(int x) {
        this.money += x;
    }
    public void removeMoney(int x) {
        this.money -= x;
        this.money = Math.max(money, 0);
    }
}
