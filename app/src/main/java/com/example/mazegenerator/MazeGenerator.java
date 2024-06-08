package com.example.mazegenerator;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.IntStream;

public class MazeGenerator {
    //private record Position(int x, int y){ }
    Cell[][] cell;
    ArrayList<HashSet<Position>> node;

    public MazeGenerator(Cell[][] cell){
        this.cell = cell;
        node = new ArrayList<>();
        Eller();
    }

    private void Eller(){
        Random rand = new Random();
        for(int y = 0; y < cell[0].length; y++){
            // 빈 방 할당하기
            for(int x = 0; x < cell.length; x++){
                Position currentPos = new Position(x, y);
                if(node.stream().noneMatch(i -> i.contains(currentPos)))
                    node.add(new HashSet<>(List.of(currentPos)));
            }

            // 좌우 무작위 병합
            for (int x = 0; x < cell.length - 1; x++) {
                Position currentPos = new Position(x, y);
                int presetIndex = IntStream.range(0, node.size()).
                        filter(t -> node.get(t).contains(currentPos)).
                        findFirst().orElse(-1);

                Position nextPos = new Position(x + 1, y);
                if (!node.get(presetIndex).contains(nextPos) && rand.nextInt(6) >= 2) {
                    cell[x][y].rightWall = false;
                    int nextIndex = IntStream.range(0, node.size()). // 다음 셀의 인덱스 구하기
                            filter(s -> node.get(s).contains(nextPos)).
                            findFirst().orElse(-1);

                    if (rand.nextInt(2) == 1) {
                        node.get(presetIndex).addAll(node.get(nextIndex));
                        node.remove(nextIndex);
                    } else {
                        node.get(nextIndex).addAll(node.get(presetIndex));
                        node.remove(presetIndex);
                    }
                }
            }
            
            // 아랫벽 허물기
            if(y < cell[0].length - 1){
                int finalY = y, presetIndex = IntStream.range(0, node.size())
                        .filter(t -> node.get(t).contains(new Position(0, finalY)))
                        .findFirst().orElse(-1);
                ArrayList<Position> unblockList = new ArrayList<>(List.of(new Position(0, finalY)));
                for(int x = 0; x < cell.length; x++){
                    Position nextPos = new Position(x + 1, y);
                    if(!node.get(presetIndex).contains(nextPos)){
                        int randCount = rand.nextInt(unblockList.size());
                        do{
                            int randIndex = rand.nextInt(unblockList.size());
                            cell[unblockList.get(randIndex).x()][unblockList.get(randIndex).y()].bottomWall = false;
                            node.get(presetIndex).add(new Position(unblockList.get(randIndex).x(), unblockList.get(randIndex).y()+1));
                            unblockList.remove(randIndex);
                            randCount--;
                        }while(randCount > 1);

                        presetIndex = IntStream.range(0, node.size()).filter(t -> node.get(t).contains(nextPos)).findFirst().orElse(-1);
                        unblockList.clear();
                    }
                    unblockList.add(nextPos);
                }
            } /*마무리*/ else{
                int presetIndex = IntStream.range(0, node.size()).
                        filter(t -> node.get(t).contains(new Position(0, cell[0].length - 1))).
                        findFirst().orElse(-1);
                for(int x = 1; x < cell.length; x++){
                    Position currentPos = new Position(x, cell[0].length - 1);
                    if(!node.get(presetIndex).contains(currentPos)){
                        cell[currentPos.x() - 1][currentPos.y()].rightWall = false;
                        int nextIndex = IntStream.range(0, node.size()). // 포함되지 않은 셀의 인덱스 구하기
                                filter(s -> node.get(s).contains(currentPos)).
                                findFirst().orElse(-1);
                        node.get(presetIndex).addAll(node.get(nextIndex));
                    }
                }
            }
        }
    }
}