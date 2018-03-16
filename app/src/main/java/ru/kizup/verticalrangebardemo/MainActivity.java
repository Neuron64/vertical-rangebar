package ru.kizup.verticalrangebardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ru.kizup.verticalrangebar.HorizontalRangeBar;
import ru.kizup.verticalrangebar.OnRangeChangeListener;
import ru.kizup.verticalrangebar.VerticalRangeBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HorizontalRangeBar verticalRangeBar = findViewById(R.id.range_bar);
        verticalRangeBar.setRange(10000, 100000);
        verticalRangeBar.setOnRangeChangeListener(new OnRangeChangeListener() {
            @Override
            public void onStartProgressChange(int progress) {
                Log.d("VerticalRangeBar", "start real value: -> " + verticalRangeBar.getRealStartValue());
            }

            @Override
            public void onEndProgressChange(int progress) {
                Log.d("VerticalRangeBar", "end real value: -> " + verticalRangeBar.getRealEndValue());
            }
        });

        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRangeBar.setStartValue(verticalRangeBar.getStartValue() + 1);
            }
        });
        findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRangeBar.setStartValue(verticalRangeBar.getStartValue() - 1);
            }
        });
        findViewById(R.id.plus_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRangeBar.setEndValue(verticalRangeBar.getEndValue() + 1);
            }
        });
        findViewById(R.id.minus_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRangeBar.setEndValue(verticalRangeBar.getEndValue() - 1);
            }
        });

    }
}
