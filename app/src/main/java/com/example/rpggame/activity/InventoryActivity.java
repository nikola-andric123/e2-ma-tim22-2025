package com.example.rpggame.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rpggame.InventoryAdapter;
import com.example.rpggame.R;
import com.example.rpggame.domain.InventoryItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recycler_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InventoryAdapter(this, inventoryList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadInventory();
    }

    private void loadInventory() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("inventory")
                .get()
                .addOnSuccessListener(query -> {
                    inventoryList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        InventoryItem item = new InventoryItem();
                        String name = doc.getString("name");
                        String category = doc.getString("category");
                        item.setCategory(category);
                        item.setName(name);
                        if(category.equals("clothes")){
                            item.setDurability(Objects.requireNonNull(doc.getDouble("durability")).longValue());
                        }
                        inventoryList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
