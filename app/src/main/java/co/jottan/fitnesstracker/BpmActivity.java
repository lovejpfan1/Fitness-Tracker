package co.jottan.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class  BpmActivity extends AppCompatActivity {

    Button bpmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);

        bpmButton = findViewById(R.id.BPM_button);
        bpmButton.setOnClickListener((view) -> {
            Spinner gender = findViewById(R.id.BPM_spinner);
            EditText age = findViewById(R.id.BPM_age);

            //String formattedGender = gender.getSelectedItem().toString();
            //int formattedAge = Integer.parseInt(age.getText().toString());

            if (!validateInput(age.getText().toString())) {
                Toast.makeText(this, "O campo idade precisa ser preenchido e diferente de 0", Toast.LENGTH_SHORT).show();
                return;
            } else {
                int result = calculateBpm(gender.getSelectedItem().toString(), Integer.parseInt(age.getText().toString()));

                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.TMB_response, result))
                        .setPositiveButton(android.R.string.ok, (which, dialog1) -> {

                        })
                        .setNegativeButton(R.string.GEN_save, (which, dialog2) -> {
                            new Thread(() -> {
                                long id = SqlHelper.getInstance(BpmActivity.this).addItem("BPM", result);
                                runOnUiThread(() -> {
                                    if (id > 0)
                                        Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show();
                                });
                            }).start();
                        });

                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.result_menu) {
            openResultScreen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openResultScreen() {
        Intent intent = new Intent(BpmActivity.this, ResultScreen.class);
        intent.putExtra("type", "BPM");
        startActivity(intent);
    }

    public int calculateBpm(String gender, int age) {
        // FCMax = 220 – sua idade para homens e FCMax = 226 – sua idade para mulheres.
        int result = 0;
        switch (gender) {
            case "Mulher":
                result = 226 - age;
                break;
            case "Homem":
                result =  220 - age;
                break;
        }
        return result;
    }

    public boolean validateInput(String age) {
        return !age.isEmpty() && !age.startsWith("0");
    }
}