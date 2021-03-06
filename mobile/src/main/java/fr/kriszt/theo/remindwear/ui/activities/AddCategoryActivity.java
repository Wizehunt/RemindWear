package fr.kriszt.theo.remindwear.ui.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

import java.util.ArrayList;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class AddCategoryActivity extends AppCompatActivity {

    LayoutInflater inflator;
    ArrayList<Integer> listIcons = new ArrayList<>();

    private EditText title;
    private Spinner spinner;
    private ColorPickerView colorPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        listIcons.add(R.drawable.ic_base_0);
        listIcons.add(R.drawable.ic_base_1);
        listIcons.add(R.drawable.ic_base_2);
        listIcons.add(R.drawable.ic_base_3);
        listIcons.add(R.drawable.ic_base_4);
        listIcons.add(R.drawable.ic_base_5);
        listIcons.add(R.drawable.ic_base_6);
        listIcons.add(R.drawable.ic_base_7);
        listIcons.add(R.drawable.ic_base_8);
        listIcons.add(R.drawable.ic_base_9);
        listIcons.add(R.drawable.ic_base_10);
        listIcons.add(R.drawable.ic_base_11);
        listIcons.add(R.drawable.ic_base_12);
        listIcons.add(R.drawable.ic_base_13);
        listIcons.add(R.drawable.ic_base_14);
        listIcons.add(R.drawable.ic_base_15);
        listIcons.add(R.drawable.baseline_directions_run_24);
        listIcons.add(R.drawable.ic_base_17);
        listIcons.add(R.drawable.ic_base_18);
        listIcons.add(R.drawable.ic_base_20);


        title = findViewById(R.id.name);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new NewAdapter(listIcons));
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                ImageView colorView = findViewById(R.id.colorView);
                colorView.setBackgroundColor(colorEnvelope.getColor());
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubmit();
            }
        });


    }

    private void actionSubmit() {
        if (title.getText().toString().equals("")) {
            Toast.makeText(this, "Inscrire le nom de la catégorie", Toast.LENGTH_SHORT).show();
        } else {
            String name = title.getText().toString();
            int color = colorPickerView.getColor();
            int icon = listIcons.get(spinner.getSelectedItemPosition());
            Category newCategory = new Category(name, icon, color);
            Tasker.getInstance(getApplicationContext()).addCategory(newCategory);
            Tasker.getInstance(getApplicationContext()).serializeLists();

            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class NewAdapter extends BaseAdapter {

        private ArrayList<Integer> items;

        NewAdapter(ArrayList<Integer> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.content_icon_spinner, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int ic = (Integer) getItem(position);

            viewHolder.itemIcon.setImageResource(ic);
            return convertView;
        }


    }

    private class ViewHolder {
        ImageView itemIcon;

        ViewHolder(@NonNull View view) {
            itemIcon = view.findViewById(R.id.icon);
        }
    }


}
