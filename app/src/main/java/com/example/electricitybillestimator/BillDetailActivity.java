package com.example.electricitybillestimator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BillDetailActivity extends AppCompatActivity {

    TextView detailMonth, detailUnits, detailTotal, detailRebate, detailFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        setTitle("Bill Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailMonth = findViewById(R.id.detailMonth);
        detailUnits = findViewById(R.id.detailUnits);
        detailTotal = findViewById(R.id.detailTotal);
        detailRebate = findViewById(R.id.detailRebate);
        detailFinal = findViewById(R.id.detailFinal);

        String month = getIntent().getStringExtra("month");
        int units = getIntent().getIntExtra("units", 0);
        double total = getIntent().getDoubleExtra("total", 0);
        double rebate = getIntent().getDoubleExtra("rebate", 0);
        double finalCost = getIntent().getDoubleExtra("final", 0);

        detailMonth.setText("Month: " + month);
        detailUnits.setText("Units Used: " + units + " kWh");
        detailTotal.setText("Total Charges: RM " + String.format("%.2f", total));
        detailRebate.setText("Rebate: " + rebate + " %");
        detailFinal.setText("Final Cost: RM " + String.format("%.2f", finalCost));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
