package com.example.electricitybillestimator;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BillRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String month;
    public int unitsUsed;
    public double totalCharges;
    public double rebatePercent;
    public double finalCost;

    public BillRecord(String month, int unitsUsed, double totalCharges, double rebatePercent, double finalCost) {
        this.month = month;
        this.unitsUsed = unitsUsed;
        this.totalCharges = totalCharges;
        this.rebatePercent = rebatePercent;
        this.finalCost = finalCost;
    }
}
