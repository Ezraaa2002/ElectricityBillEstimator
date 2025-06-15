package com.example.electricitybillestimator;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BillDao {

    @Insert
    void insertBill(BillRecord bill);

    @Query("SELECT * FROM BillRecord ORDER BY id DESC")
    List<BillRecord> getAllBills();

    // ✅ Add this method to enable deleting a bill
    @Delete
    void deleteBill(BillRecord bill);
}
