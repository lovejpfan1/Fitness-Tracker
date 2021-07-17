package co.jottan.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.round;

public class ResultScreen extends AppCompatActivity {

    RecyclerView resultScreenRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        resultScreenRV = findViewById(R.id.result_screen_RV);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String type = extras.getString("type");

            new Thread(() -> {
                List<Register> registers = SqlHelper.getInstance(this).getRegisterBy(type);

                runOnUiThread(() -> {
                    ResultAdapter adapter = new ResultAdapter(registers);
                    resultScreenRV.setLayoutManager(new LinearLayoutManager(this));
                    resultScreenRV.setAdapter(adapter);
                });

            }).start();

        }
    }

    public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

        List<Register> registers;

        // constructor ↓
        public ResultAdapter(List<Register> registerList) {
            registers = registerList;
        }

        @NonNull
        @Override
        public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ResultViewHolder(getLayoutInflater().inflate(R.layout.result_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ResultScreen.ResultAdapter.ResultViewHolder holder, int position) {
            Register currentRegister = registers.get(position);
            holder.bind(currentRegister);
        }

        @Override
        public int getItemCount() {
            return registers.size();
        }

        public class ResultViewHolder extends RecyclerView.ViewHolder {


            public ResultViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bind(Register currentRegister) {
                TextView result = itemView.findViewById(R.id.result_view_result);
                TextView created_at = itemView.findViewById(R.id.result_view_created_at);
                TextView type = findViewById(R.id.result_screen_title);

                result.setText(String.format("%.2f", currentRegister.getResponse()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));
                created_at.setText(sdf.format(Timestamp.valueOf(currentRegister.getCreated_at())));
                type.setText(getString(R.string.GRN_result_screen_title, currentRegister.getType()));
            }
        }
    }
}