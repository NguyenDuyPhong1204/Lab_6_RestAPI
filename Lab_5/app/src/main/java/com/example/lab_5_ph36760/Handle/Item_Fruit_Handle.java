package com.example.lab_5_ph36760.Handle;

import com.example.lab_5_ph36760.Model.Distributor;
import com.example.lab_5_ph36760.Model.Fruit;

public interface Item_Fruit_Handle {
    public void Delete(String id);
    public void Update(String id, Fruit fruit);
}
