package com.example.mazegenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class Astar {
    private final Cell[][] cells;
    public Astar(Cell[][] cells){
        this.cells = cells;
    }

    private class AstarNode{
        public AstarNode prevNode;
        public Position pos;
        public int gCost, hCost, fCost;
        public AstarNode(AstarNode prevNode, Position pos, int gCost, int hCost){
            this.prevNode = prevNode;
            this.pos = pos;
            this.gCost = gCost;
            this.hCost = hCost;
            fCost = this.gCost + this.hCost;
        }
    }
    private AstarNode lastAstarNode;

    public ArrayList<Position> PathFinding(Cell player, Cell goal){
        ArrayList<Position> path = new ArrayList<>();
        boolean isGoal = false;
        HashMap<Position, AstarNode> openMap = new HashMap<>();
        HashSet<Position> closeSet = new HashSet<>();
        Position goalPos = new Position(goal.x, goal.y);

        openMap.put(new Position(player.x,player.y), new AstarNode(null, new Position(player.x,player.y), 0, 0));
        while(openMap.size() > 0 && !isGoal){
            ArrayList<Position> sortPos = new ArrayList<>(openMap.keySet());
            Position openPos = sortPos.stream().min(Comparator.comparingInt(o -> openMap.get(o).fCost)).orElse(null);

            for(int y = -1; y < 2; y++){
                for(int x = (y == 0 ? -1 : 0); x < (y == 0 ? 2 : 1); x++){
                    Position currentPos = new Position(openPos.x() + x, openPos.y() + y);

                    if(currentPos.x() >= cells.length || currentPos.x() < 0 || // 범위 초과 제외
                    currentPos.y() >= cells[0].length || currentPos.y() < 0 || // 상동
                    closeSet.contains(currentPos) || currentPos.equals(openPos) || // 닫힌 목록 및 현재 위치 제외
                    (y <= 0 && x<=0 && (x == 0 ? cells[currentPos.x()][currentPos.y()].bottomWall : cells[currentPos.x()][currentPos.y()].rightWall)) ||
                    (y >= 0 && x>=0 && (x == 0 ? cells[openPos.x()][openPos.y()].bottomWall : cells[openPos.x()][openPos.y()].rightWall)) ||
                    (openMap.containsKey(currentPos) && openMap.get(currentPos).fCost <
                    openMap.get(openPos).gCost + Math.abs(currentPos.x() - goal.x) + Math.abs(currentPos.y() - goal.y))) continue;

                    AstarNode currentAstarNode = new AstarNode(
                            openMap.get(openPos), currentPos, //이전 노드 주소와 현재 위치 저장
                            openMap.get(openPos).gCost + 1, // gCost 계산
                            Math.abs(currentPos.x() - goal.x) + Math.abs(currentPos.y() - goal.y)); // hCost 계산

                    openMap.put(currentPos, currentAstarNode);
                    if(currentPos.equals(goalPos)){
                        lastAstarNode = currentAstarNode;
                        isGoal = true;
                        y = 2;
                        break;
                    }
                }
            }
            closeSet.add(openPos);
            openMap.remove(openPos);
        }

        while(lastAstarNode != null){
            path.add(lastAstarNode.pos);
            lastAstarNode = lastAstarNode.prevNode;
        }
        return path;
    }
}
