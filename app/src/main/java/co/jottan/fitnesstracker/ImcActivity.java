package co.jottan.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ImcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        Button imcBtn = findViewById(R.id.IMC_button);
        imcBtn.setOnClickListener((view) -> {

            EditText weight = findViewById(R.id.IMC_weight);
            EditText height = findViewById(R.id.IMC_height);

            String sWeight = weight.getText().toString();
            String sHeight = height.getText().toString();

            if (!validateInputs(sWeight, sHeight)) {
                Toast.makeText(this,
                        "Os campos precisam ser preenchidos",
                        Toast.LENGTH_SHORT)
                            .show();
                return;
            } else {
                double imcResult = calculateImc(Integer.parseInt(sWeight), Integer.parseInt(sHeight));

                int imcMessage = getStringResult(imcResult);

                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.IMC_response, imcResult))
                        .setMessage(imcMessage)
                        .setPositiveButton(android.R.string.ok, (dialog1, which) -> {

                        })
                        .setNegativeButton(R.string.GEN_save, (dialog1, which) -> {

                            new Thread(() -> {
                                long id = SqlHelper.getInstance(ImcActivity.this).addItem("IMC", imcResult);
                                // this will run in graphic thread
                                runOnUiThread(() -> {
                                    if (id > 0)
                                        Toast.makeText(this,
                                                "Registro salvo com sucesso!",
                                                Toast.LENGTH_SHORT)
                                                .show();

                                        openResultScreen();

                                });

                            }).start();

                        });
                dialog.show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weight.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(height.getWindowToken(), 0);
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
        Intent intent = new Intent(ImcActivity.this, ResultScreen.class);
        intent.putExtra("type", "IMC");
        startActivity(intent);
    }

    public boolean validateInputs(String sWeight, String sHeight) {
        return !sWeight.isEmpty()
                && !sWeight.startsWith("0")
                && !sHeight.isEmpty()
                && !sHeight.startsWith("0");
    }

    public double calculateImc(int weight, int height) {
        return weight / (((double) height / 100) * ((double) height / 100));
    }

    public int getStringResult(double imcResult) {
        if (imcResult < 18.5)
            return R.string.IMC_under_weight;
        else if (imcResult > 18.5 && imcResult < 24.9)
            return R.string.IMC_normal_weight;
        else if (imcResult > 25 && imcResult < 29.9)
            return R.string.IMC_overweight;
        else if (imcResult > 30 && imcResult < 34.9)
            return R.string.IMC_obesity_grade_1;
        else if (imcResult > 35 && imcResult < 39.9)
            return R.string.IMC_obesity_grade_2;
        else
            return R.string.IMC_obesity_grade_3;
    }

}