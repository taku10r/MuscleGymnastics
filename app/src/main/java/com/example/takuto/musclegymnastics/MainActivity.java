package com.example.takuto.musclegymnastics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //腹筋説明編へ
        Button abdominalButton = findViewById(R.id.abdominalexplanation_button);
        abdominalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), AbdominalExplanationActivity.class);
                startActivity(intent);
            }
        });

    }
}
