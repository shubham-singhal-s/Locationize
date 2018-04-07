package ado.fun.code.locationize;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InputDays extends AppCompatActivity implements View.OnClickListener {

    TextView day;
    CheckBox first, second;
    Spinner from, to, from_sec, to_sec;
    String f=null, t=null, f_sec=null, t_sec=null;
    int k=0;
    String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    DBHelper db;
    ArrayList<String> d, first_to, first_from, second_from, second_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_days);

        findViewById(R.id.next).setOnClickListener(this);
        d=new ArrayList<>();
        first_to=new ArrayList<>();
        first_from=new ArrayList<>();
        second_from=new ArrayList<>();
        second_to=new ArrayList<>();
        db=new DBHelper(getApplicationContext());
        db.dropTab();
        day= findViewById(R.id.day);
        first=findViewById(R.id.first);
        second=findViewById(R.id.second);
        first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    updateUIf();
                }
                else{
                    removeUIf();
                }
            }
        });
        second.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    updateUIs();
                }
                else{
                    removeUIs();
                }
            }
        });
        day.setText(days[k]);
    }

    private void removeUIf(){
        from= findViewById(R.id.from);
        from.setVisibility(View.GONE);
        to= findViewById(R.id.to);
        to.setVisibility(View.GONE);
    }

    private void removeUIs(){
        from_sec= findViewById(R.id.from_sec);
        from_sec.setVisibility(View.GONE);
        to_sec= findViewById(R.id.to_sec);
        to_sec.setVisibility(View.GONE);
    }


    private void updateUIf() {
        String[] fr={"8:00", "8:30", "9:00", "10:30", "11:30"};
        String[] t_o={"9:00", "10:00", "11:30", "12:30"};
        ArrayAdapter<String> ad=new ArrayAdapter<String>(InputDays.this, android.R.layout.simple_dropdown_item_1line, fr);
        from= findViewById(R.id.from);
        from.setVisibility(View.VISIBLE);
        from.setAdapter(ad);
        ad=new ArrayAdapter<String>(InputDays.this, android.R.layout.simple_dropdown_item_1line, t_o);
        to= findViewById(R.id.to);
        to.setVisibility(View.VISIBLE);
        to.setAdapter(ad);
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                f=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                t=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUIs() {
        String[] fr={"13:00", "14:00", "15:30", "16:30"};
        String[] t_o={"14:00", "15:00", "16:30", "17:00", "17:30"};
        ArrayAdapter<String> ad=new ArrayAdapter<String>(InputDays.this, android.R.layout.simple_dropdown_item_1line, fr);
        from_sec= findViewById(R.id.from_sec);
        from_sec.setVisibility(View.VISIBLE);
        from_sec.setAdapter(ad);
        to_sec= findViewById(R.id.to_sec);
        to_sec.setVisibility(View.VISIBLE);
        ad=new ArrayAdapter<String>(InputDays.this, android.R.layout.simple_dropdown_item_1line, t_o);
        to_sec.setAdapter(ad);
        from_sec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                f_sec=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        to_sec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                t_sec=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.next:
                String temp=days[k];
                d.add(days[k++]);
                first_from.add(f);
                first_to.add(t);
                second_from.add(f_sec);
                second_to.add(t_sec);
                Log.d("Day: ", temp + f + t + f_sec + t_sec);
                f=null;
                t=null;
                f_sec=null;
                t_sec=null;
                first.setChecked(false);
                second.setChecked(false);
                if(k<6) {
                    day.setText(days[k]);
                }
                else{
                    db.createTab();
                    boolean c=db.addToDB(d, first_from, first_to, second_from, second_to);
                    if(c){
                        Toast.makeText(getApplicationContext(), "Timetable added succesfully!", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else{
                        k=0;
                        Toast.makeText(getApplicationContext(), "Error try again", Toast.LENGTH_LONG).show();
                        day.setText(days[k]);
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public  void onBackPressed(){

    }

}
