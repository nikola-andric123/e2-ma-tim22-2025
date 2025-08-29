package com.example.rpggame;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EquipmentSelectionActivity extends AppCompatActivity {

    private Button btnContinue;
    private String userUID;


    private LinearLayout layoutWeapons, layoutPotions, layoutClothes;
    private FirebaseUser currentUser;
    private UserProfile currentUserProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    CollectionReference addedPowersRef;
    CollectionReference inventoryRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_selection);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userUID = currentUser.getUid();
        layoutWeapons = findViewById(R.id.layout_weapons);
        layoutPotions = findViewById(R.id.layout_potions);
        layoutClothes = findViewById(R.id.layout_clothes);
        btnContinue = findViewById(R.id.btn_continue_to_battle);

        db.collection("users").document(userUID).get()
                .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                currentUserProfile = doc.toObject(UserProfile.class);
                            }
                            addedPowersRef = db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("addedPowers");
                            inventoryRef = db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("inventory");
                    loadWeapons();
                    loadPotions();
                    loadClothes();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading user doc", e);
                });;




    }

    private void loadWeapons() {
        inventoryRef.whereEqualTo("category", "weapon")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String weaponId = doc.getId();
                        String weaponName = doc.getString("name");
                        Number coinsIncreasePercent;
                        Number powerIncreasePercent;
                        if(weaponName.equals("bow_and_arrow")){
                            powerIncreasePercent = null;
                            coinsIncreasePercent = (Number) doc.get("coinsIncreasePercent");
                        }else{
                            coinsIncreasePercent = null;
                            powerIncreasePercent = (Number) doc.get("powerIncreasePercent");
                        }


                        Button btn = new Button(this);
                        btn.setText(weaponName);
                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            Map<String, Object> weapon = new HashMap<>();
                            weapon.put("name", weaponName);
                            weapon.put("category", "weapon");
                            if(coinsIncreasePercent != null){
                                weapon.put("coinsIncreasePercent", coinsIncreasePercent.doubleValue());

                            }else{
                                weapon.put("powerIncreasePercent", powerIncreasePercent.doubleValue());
                            }
                            weapon.put("timestamp", FieldValue.serverTimestamp());

                            addedPowersRef.add(weapon)
                                    .addOnSuccessListener(docRef -> {
                                        Toast.makeText(this,"Potion bought!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add potion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            Toast.makeText(this, weaponName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutWeapons.addView(btn);
                    }
                });
    }

    private void loadPotions() {
        inventoryRef.whereEqualTo("category", "potion")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String potionId = doc.getId();
                        String potionName = doc.getString("name");
                        String potionDurability = doc.getString("durability");
                        Long powerBoost = doc.getLong("powerBoost");

                        Button btn = new Button(this);
                        btn.setText(potionName);
                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            Map<String, Object> potion = new HashMap<>();
                            potion.put("name", potionName);
                            potion.put("category", "potion");

                            potion.put("powerBoost", powerBoost);
                            addedPowersRef.add(potion)
                                    .addOnSuccessListener(docRef -> {
                                        Toast.makeText(this,"Potion bought!", Toast.LENGTH_SHORT).show();
                                        if(potionDurability.equals("oneTime")){

                                            inventoryRef.document(potionId)
                                                    .delete();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add potion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });

                            Toast.makeText(this, potionName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutWeapons.addView(btn);
                    }
                });
    }

    private void loadClothes() {
        inventoryRef.whereEqualTo("category", "clothes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String clothesId = doc.getId();
                        String clothesName = doc.getString("name");
                        Number powerBoost = null;
                        Number hitSuccessIncrease = null;
                        Number oneExtraHitChance = null;
                        if (clothesName.equals("gloves")) {
                            powerBoost = (Number) doc.getDouble("powerBoost");
                        } else if (clothesName.equals("shield")) {
                            hitSuccessIncrease = (Number) doc.getDouble("hitSuccessIncrease");
                            powerBoost = null;
                        } else {
                            oneExtraHitChance = (Number) doc.getDouble("oneExtraHitChance");
                            powerBoost = null;
                        }


                        Button btn = new Button(this);
                        btn.setText(clothesName);
                        Number finalPowerBoost = powerBoost;
                        Number finalOneExtraHitChance = oneExtraHitChance;
                        Number finalHitSuccessIncrease = hitSuccessIncrease;

                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            Map<String, Object> clothes = new HashMap<>();
                            clothes.put("name", clothesName);
                            clothes.put("category", "clothes");
                            if (clothesName.equals("gloves")) {
                                clothes.put("powerBoost", finalPowerBoost);
                            } else if (clothesName.equals("shield")) {
                                clothes.put("hitSuccessIncrease", finalHitSuccessIncrease);
                            } else {
                                clothes.put("oneExtraHitChance", finalOneExtraHitChance);
                            }

                            addedPowersRef.add(clothes)
                                    .addOnSuccessListener(docRef -> {
                                        Toast.makeText(this,"Potion bought!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add potion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            Toast.makeText(this, clothesName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutWeapons.addView(btn);
                    }
                });
    }
}