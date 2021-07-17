package co.jottan.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TmbActivity extends AppCompatActivity {

    Button tmbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmb);

        tmbButton = findViewById(R.id.TMB_button);
        tmbButton.setOnClickListener(v -> {
            Spinner gender = findViewById(R.id.TMB_spinner);
            EditText weight = findViewById(R.id.TMB_weight);
            EditText height = findViewById(R.id.TMB_height);
            EditText age = findViewById(R.id.TMB_age);

            String genderValue = gender.getSelectedItem().toString();
            String weightValue = weight.getText().toString();
            String heightValue = height.getText().toString();
            String ageValue = age.getText().toString();

            if (!validateInputs(genderValue, weightValue, heightValue, ageValue)) {
                Toast.makeText(this, "Todos os campos precisam ser preenchidos e não começar com 0", Toast.LENGTH_SHORT).show();
            } else {
                double formattedWeight = Double.parseDouble(weightValue);
                double formattedHeight = Double.parseDouble(heightValue);
                double formattedAge = Double.parseDouble(ageValue);
                double tmbResult = calculateTmb(genderValue, formattedWeight, formattedHeight, formattedAge);

                Log.i("Result", "TMB: " + tmbResult);
                // create Dialog and fill with result then save in DB

                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.TMB_response, tmbResult))
                        .setPositiveButton(android.R.string.ok, (dialog1, which) -> {

                        })
                        .setNegativeButton(R.string.GEN_save, (dialog2, which) -> {
                            // save on db
                            new Thread(() -> {
                                long id = SqlHelper.getInstance(TmbActivity.this)
                                        .addItem("TMB", tmbResult);
                                runOnUiThread(() -> {
                                    if (id > 0)
                                        Toast.makeText(this,
                                                "Registro salvo com sucesso!",
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    // create a intent and redirect to result screen with extras
                                    openResultScreen();
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

    private void openResultScreen() {
        Intent intent = new Intent(TmbActivity.this, ResultScreen.class);
        intent.putExtra("type", "TMB");
        startActivity(intent);
    }

    public double calculateTmb(String gender, double weight, double height, double age) {
        //  Men = TMB = 66 + (13,8 x weight in kg.) + (5 x height in cm) - (6,8 x age)
        //  Women = 655 + (9,6 x weight in kg.) + (1,8 x height in cm) - (4,7 x age)

        double result = 0;

        if (gender.equals("Mulher")) {
            result = 66 + (13.8 * weight) + (5 * height) - (6.8 * age);
        }

        if (gender.equals("Homem")) {
            result =  655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
        }

        return result;

    }

    public boolean validateInputs(String gender, String weight, String height, String age) {
        return (!gender.equals("") && !gender.isEmpty() && !gender.startsWith("0")
                && !weight.equals("") && !weight.isEmpty() && !weight.startsWith("0")
                && !height.equals("") && !height.isEmpty() && !height.startsWith("0")
                && !age.equals("0") && !age.isEmpty() && !age.startsWith("0"));

    }
}