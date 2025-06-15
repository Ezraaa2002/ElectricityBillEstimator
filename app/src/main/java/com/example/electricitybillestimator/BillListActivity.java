package com.example.electricitybillestimator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class BillListActivity extends AppCompatActivity {

    ListView billListView;
    Button deleteButton, selectAllButton, deselectAllButton;
    TextView emptyMessageText;
    List<BillRecord> bills;
    ArrayAdapter<BillRecord> adapter;
    boolean isEditMode = false;
    List<BillRecord> selectedBills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        setTitle("Saved Bills");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind Views
        billListView = findViewById(R.id.billListView);
        deleteButton = findViewById(R.id.deleteButton);
        selectAllButton = findViewById(R.id.selectAllButton);
        deselectAllButton = findViewById(R.id.deselectAllButton);
        emptyMessageText = findViewById(R.id.emptyMessageText);

        bills = BillDatabase.getInstance(this).billDao().getAllBills();

        // Set empty state
        if (bills.isEmpty()) {
            emptyMessageText.setVisibility(View.VISIBLE);
            billListView.setVisibility(View.GONE);
        } else {
            emptyMessageText.setVisibility(View.GONE);
            billListView.setVisibility(View.VISIBLE);
        }

        // Adapter
        adapter = new ArrayAdapter<BillRecord>(this, R.layout.bill_list_item, R.id.monthText, bills) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = LayoutInflater.from(getContext()).inflate(R.layout.bill_list_item, parent, false);

                TextView monthText = row.findViewById(R.id.monthText);
                TextView finalCostText = row.findViewById(R.id.finalCostText);
                CheckBox checkBox = row.findViewById(R.id.billCheckBox);

                BillRecord bill = getItem(position);
                monthText.setText(bill.month);
                finalCostText.setText("Final Cost: RM " + String.format("%.2f", bill.finalCost));

                checkBox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
                checkBox.setChecked(selectedBills.contains(bill));

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked && !selectedBills.contains(bill)) {
                        selectedBills.add(bill);
                    } else if (!isChecked) {
                        selectedBills.remove(bill);
                    }
                });

                row.setOnClickListener(v -> {
                    if (isEditMode) {
                        checkBox.setChecked(!checkBox.isChecked());
                    } else {
                        Intent intent = new Intent(BillListActivity.this, BillDetailActivity.class);
                        intent.putExtra("month", bill.month);
                        intent.putExtra("units", bill.unitsUsed);
                        intent.putExtra("total", bill.totalCharges);
                        intent.putExtra("rebate", bill.rebatePercent);
                        intent.putExtra("final", bill.finalCost);
                        startActivity(intent);
                    }
                });

                return row;
            }
        };

        billListView.setAdapter(adapter);

        // Delete Button
        deleteButton.setOnClickListener(v -> {
            if (selectedBills.isEmpty()) {
                Toast.makeText(this, "Please select at least one item to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = selectedBills.size();
            String message = "Delete " + count + " selected bill" + (count > 1 ? "s" : "") + "?";

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage(message)
                    .setPositiveButton("Yes", (d, which) -> {
                        for (BillRecord bill : selectedBills) {
                            BillDatabase.getInstance(this).billDao().deleteBill(bill);
                            bills.remove(bill);
                        }
                        selectedBills.clear();
                        isEditMode = false;
                        toggleButtonsVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                        // Refresh empty state
                        if (bills.isEmpty()) {
                            emptyMessageText.setVisibility(View.VISIBLE);
                            billListView.setVisibility(View.GONE);
                        }

                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.setOnShowListener(d -> {
                Button yesButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                yesButton.setTextColor(Color.WHITE);
                cancelButton.setTextColor(Color.WHITE);
            });

            dialog.show();
        });

        // Select All
        selectAllButton.setOnClickListener(v -> {
            selectedBills.clear();
            selectedBills.addAll(bills);
            adapter.notifyDataSetChanged();
        });

        // Deselect All
        deselectAllButton.setOnClickListener(v -> {
            selectedBills.clear();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bill_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            toggleEditMode();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        selectedBills.clear();
        toggleButtonsVisibility(isEditMode ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void toggleButtonsVisibility(int visibility) {
        deleteButton.setVisibility(visibility);
        selectAllButton.setVisibility(visibility);
        deselectAllButton.setVisibility(visibility);
    }
}
