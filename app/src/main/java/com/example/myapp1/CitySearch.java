package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CitySearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        // Кнопка для возвращения с результатом
        final Button button = findViewById(R.id.id_citySearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText city = findViewById(R.id.id_cityEdit);
                String citySearched = city.getText().toString();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", citySearched);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        // Кнопка для перехода на MainActivity2
        Button goToMainActivity2Button = findViewById(R.id.id_goToMainActivity2); // Сделайте sure, что эта кнопка есть в layout
        goToMainActivity2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход на MainActivity2
                Intent intent = new Intent(CitySearch.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }
}
