package com.example.mazegenerator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = (GameView)findViewById(R.id.mazeMap);
        findViewById(R.id.pathFindingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.PathFinding();
            }
        });

    }
}