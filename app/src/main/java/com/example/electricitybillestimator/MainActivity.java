package com.example.electricitybillestimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner monthSpinner;
    EditText unitsEditText, rebateEditText;
    Button calculateButton;
    TextView totalChargesTextView, finalCostTextView;

    String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("ElectricityBillEstimator");

        // Bind Views
        monthSpinner = findViewById(R.id.monthSpinner);
        unitsEditText = findViewById(R.id.unitsEditText);
        rebateEditText = findViewById(R.id.rebateEditText);
        calculateButton = findViewById(R.id.calculateButton);
        totalChargesTextView = findViewById(R.id.totalChargesTextView);
        finalCostTextView = findViewById(R.id.finalCostTextView);

        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BillListActivity.class));
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // custom layout with black text
                months
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        // Calculate Button Click
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBill();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateBill() {
        String unitsStr = unitsEditText.getText().toString();
        String rebateStr = rebateEditText.getText().toString();

        if (unitsStr.isEmpty()) {
            unitsEditText.setError("Please enter units used");
            unitsEditText.requestFocus();
            return;
        }

        int units;
        try {
            units = Integer.parseInt(unitsStr);
            if (units < 0) {
                unitsEditText.setError("Units must be a positive number");
                unitsEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            unitsEditText.setError("Enter a valid number for units");
            unitsEditText.requestFocus();
            return;
        }

        if (rebateStr.isEmpty()) {
            rebateEditText.setError("Please enter rebate percentage");
            rebateEditText.requestFocus();
            return;
        }

        double rebate;
        try {
            rebate = Double.parseDouble(rebateStr);
            if (rebate < 0 || rebate > 5) {
                rebateEditText.setError("Rebate must be between 0 and 5%");
                rebateEditText.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            rebateEditText.setError("Enter a valid number for rebate");
            rebateEditText.requestFocus();
            return;
        }

        double totalCharge = calculateTotalCharge(units);
        double finalCost = totalCharge - (totalCharge * rebate / 100.0);

        totalChargesTextView.setText("Total Charges = RM " + String.format("%.2f", totalCharge));
        finalCostTextView.setText("Final Cost = RM " + String.format("%.2f", finalCost));

        // Save to database
        BillRecord bill = new BillRecord(
                monthSpinner.getSelectedItem().toString(),
                units,
                totalCharge,
                rebate,
                finalCost
        );

        BillDatabase.getInstance(MainActivity.this).billDao().insertBill(bill);
        Toast.makeText(this, "Record saved", Toast.LENGTH_SHORT).show();
    }

    private double calculateTotalCharge(int units) {
        double totalCharge = 0;
        int remaining = units;

        if (remaining > 0) {
            int block = Math.min(200, remaining);
            totalCharge += block * 0.218;
            remaining -= block;
        }

        if (remaining > 0) {
            int block = Math.min(100, remaining);
            totalCharge += block * 0.334;
            remaining -= block;
        }

        if (remaining > 0) {
            int block = Math.min(300, remaining);
            totalCharge += block * 0.516;
            remaining -= block;
        }

        if (remaining > 0) {
            totalCharge += remaining * 0.546;
        }

        return totalCharge;
    }
}