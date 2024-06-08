package com.example.mazegenerator;

public class Cell {
    int x, y;
    boolean rightWall = true, bottomWall = true;
    public Cell(int col, int row){
        this.x = col;
        this.y = row;
    }
}
