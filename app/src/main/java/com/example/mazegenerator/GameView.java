package com.example.mazegenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class GameView extends View{
    private enum Direction{NONE, UP, DOWN, LEFT, RIGHT}
    private Cell[][] cells;
    private Cell player, exit;
    private Astar astar;
    private static final int COLS = 40, ROWS = 18;
    private static final float WALL_THICKNESS = 8;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint, pathLine;
    private Path path;
    private MazeGenerator mazeGenerator;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    private void Init(){
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        exitPaint= new Paint();
        exitPaint.setColor(Color.BLUE);

        pathLine = new Paint();
        pathLine.setColor(Color.YELLOW);
        pathLine.setStyle(Paint.Style.STROKE);
        pathLine.setStrokeWidth(10f);

        createMaze();
    }

    private void createMaze(){
        cells = new Cell[COLS][ROWS];
        for(int x=0; x<COLS;x++){
            for(int y=0;y<ROWS;y++){
                cells[x][y] = new Cell(x,y);
            }
        }
        mazeGenerator = new MazeGenerator(cells);
        astar = new Astar(cells);
        path = new Path();
        player = cells[0][0];
        exit = cells[COLS - 1][ROWS - 1];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();

        if(width/height < COLS/ROWS)  cellSize = width / (COLS + 1);
        else cellSize = height/(ROWS + 3);

        hMargin = (width - COLS*cellSize)/2;
        vMargin = (height - ROWS*cellSize)/2;


        canvas.translate(hMargin, vMargin + 50);
        canvas.drawLine(0, 0, cellSize*COLS, 0, wallPaint);
        canvas.drawLine(0, 0, 0, cellSize*ROWS, wallPaint);

        for(int x=0; x<COLS;x++){
            for(int y=0;y<ROWS;y++){
                if(cells[x][y].rightWall)
                    canvas.drawLine(
                            (x+1)*cellSize, y*cellSize,
                            (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                if(cells[x][y].bottomWall)
                    canvas.drawLine(
                            x*cellSize, (y+1)*cellSize,
                            (x+1)*cellSize, (y+1)*cellSize, wallPaint);
            }
        }

        float margin = cellSize / 10;

        canvas.drawRect(
                player.x*cellSize + margin, player.y*cellSize + margin,
                (player.x + 1) * cellSize - margin, (player.y + 1) * cellSize - margin,
                playerPaint);
        canvas.drawRect(
                exit.x*cellSize + margin, exit.y*cellSize + margin,
                (exit.x + 1) * cellSize - margin, (exit.y + 1) * cellSize - margin,
                exitPaint);

        canvas.drawPath(path, pathLine);
    }

    public void PathFinding(){
        ArrayList<Position> mazePath = astar.PathFinding(player, exit);
        path.moveTo(cellSize * (player.x + 0.5f), cellSize * (player.y + 0.5f));
        for(int i = mazePath.size() - 2; i>=0;i--){
            path.lineTo(cellSize * (mazePath.get(i).x() + 0.5f),cellSize * (mazePath.get(i).y() + 0.5f));
        }
        invalidate();
    }


    private void movePlayer(Direction direction){
        switch (direction){
            case UP     -> player = cells[player.x][player.y - 1].bottomWall
                        ? player : cells[player.x][player.y - 1];
            case DOWN   -> player = cells[player.x][player.y].bottomWall
                        ? player : cells[player.x][player.y + 1];
            case LEFT   -> player = cells[player.x - 1][player.y].rightWall
                        ? player : cells[player.x - 1][player.y];
            case RIGHT  -> player = cells[player.x][player.y].rightWall
                        ? player : cells[player.x + 1][player.y];
        }
        if(player == exit) createMaze();
        path = new Path();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) return true;
        if(event.getAction() == MotionEvent.ACTION_MOVE){

            float playerCenterX = hMargin + (player.x + 0.5f) * cellSize;
            float playerCenterY = vMargin + (player.y + 0.5f) * cellSize;

            float dx = event.getX() - playerCenterX;
            float dy = event.getY() - playerCenterY;

            float absDx = Math.abs(dx);
            float absDy = Math.abs(dy);

            if(absDx > cellSize || absDy > cellSize){
                if(absDx > absDy){
                    movePlayer(dx > 0 ?
                            (player.x == COLS - 1 ? Direction.NONE : Direction.RIGHT)
                            : (player.x == 0 ? Direction.NONE : Direction.LEFT));
                }
                else{
                    movePlayer(dy > 0 ?
                            (player.y == ROWS - 1 ? Direction.NONE : Direction.DOWN)
                            : (player.y == 0 ? Direction.NONE : Direction.UP));
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}
