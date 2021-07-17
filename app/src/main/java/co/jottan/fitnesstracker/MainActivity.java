package co.jottan.fitnesstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainActivityRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityRV = findViewById(R.id.main_activity_RV);


        List<IconButton> iconList = new ArrayList<>();

        mainActivityRV.setLayoutManager(new GridLayoutManager(this, 2));
        MainAdapter adapter = new MainAdapter(iconList);
        adapter.setListener((id) -> {
            switch (id) {
                case 1:
                    startActivity(new Intent(MainActivity.this, ImcActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.this, TmbActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(MainActivity.this, BpmActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(MainActivity.this, FatActivity.class));
                    break;
            }
        });

        mainActivityRV.setAdapter(adapter);

        iconList.add(new IconButton(1, "IMC", R.drawable.strong));
        iconList.add(new IconButton(2, "TMB", R.drawable.weight));
        iconList.add(new IconButton(3, "Batimentos", R.drawable.heart));
        // iconList.add(new IconButton(4, "% gordura", R.drawable.fire));
    }

    private class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        List<IconButton> iconLists;
        OnIconClickListener listener;

        public MainAdapter(List<IconButton> iconList) {
            this.iconLists = iconList;
        }

        public void setListener(OnIconClickListener listener) {
            this.listener = listener;
        }


        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(getLayoutInflater().inflate(R.layout.icon_button, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.MainAdapter.MainViewHolder holder, int position) {
            IconButton currentIcon = iconLists.get(position);
            holder.bind(currentIcon);
        }

        @Override
        public int getItemCount() {
            return iconLists.size();
        }

        private class MainViewHolder extends RecyclerView.ViewHolder {

            // constructor
            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bind(IconButton currentIcon) {
                ImageView iconImage = itemView.findViewById(R.id.icon_button_img);
                TextView iconText = itemView.findViewById(R.id.icon_button_text);
                LinearLayout iconButtonButton = itemView.findViewById(R.id.icon_button_container);

                iconImage.setImageResource(currentIcon.getIcon());
                iconText.setText(currentIcon.getTitle());

                iconButtonButton.setOnClickListener(view -> {
                    listener.onClick(currentIcon.getId());
                });
            }
        }
    }


}