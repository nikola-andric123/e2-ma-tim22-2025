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
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentSelectionActivity extends AppCompatActivity {

    private Button btnContinue;
    private String userUID;
    private List<String> addedPowersIds;


    private LinearLayout layoutWeapons, layoutPotions, layoutClothes;
    private FirebaseUser currentUser;
    private UserProfile currentUserProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    CollectionReference addedPowersRef;
    private double addedPowerAmount;
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
        addedPowersIds = new ArrayList<>();
        addedPowerAmount = 0.0;
        addedPowersRef = db.collection("users")
                .document(currentUser.getUid())
                .collection("addedPowers");
        inventoryRef = db.collection("users")
                .document(currentUser.getUid())
                .collection("inventory");
        db.collection("users").document(userUID).get()
                .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                currentUserProfile = doc.toObject(UserProfile.class);
                            }

                    loadWeapons();
                    loadPotions();
                    loadClothes();
                    btnContinue.setOnClickListener(v -> {
                        db.collection("users").document(userUID)
                                .update("powerPoints", currentUserProfile.getPowerPoints())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "powerPoints updated!");

                                    Intent bossFightActivity = new Intent(EquipmentSelectionActivity.this, BorbaActivity.class);
                                    bossFightActivity.putStringArrayListExtra("addedPowersIds", new ArrayList<>(addedPowersIds));
                                    bossFightActivity.putExtra("addedPowersAmount", addedPowerAmount);
                                    startActivity(bossFightActivity);
                                    finish();


                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error updating powerPoints", e);
                                });
                    });

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
                        String status = doc.contains("status") ? doc.getString("status") : "notUsed";//used or notUsed
                        Number coinsIncreasePercent;
                        Number powerIncreasePercent;
                        if(weaponName.equals("bowAndArrow")){
                            powerIncreasePercent = null;
                            coinsIncreasePercent = (Number) doc.get("coinsIncreasePercent");
                        }else{
                            coinsIncreasePercent = null;
                            powerIncreasePercent = (Number) doc.get("powerIncreasePercent");
                        }


                        Button btn = new Button(this);
                        if(status.equals("used")){
                            btn.setEnabled(false);
                            btn.setText("Used");
                            if(weaponName.equals("BowAndArrow")){
                                Map<String, Object> weapon = new HashMap<>();
                                weapon.put("name", weaponName);
                                weapon.put("category", "weapon");
                                weapon.put("coinsIncreasePercent", coinsIncreasePercent.doubleValue());
                                weapon.put("timestamp", FieldValue.serverTimestamp());
                                addedPowersRef.add(weapon)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this, "Bow equiped!", Toast.LENGTH_SHORT).show();
                                            String newWeaponId = docRef.getId();
                                            addedPowersIds.add(newWeaponId);

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to equip bow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }else{
                            btn.setText(weaponName);
                        }
                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            btn.setEnabled(false);            // disable the button
                            btn.setText("Used");
                            Map<String, Object> weapon = new HashMap<>();
                            weapon.put("name", weaponName);
                            weapon.put("category", "weapon");
                            if(coinsIncreasePercent != null){
                                weapon.put("coinsIncreasePercent", coinsIncreasePercent.doubleValue());

                            }else{
                                weapon.put("powerIncreasePercent", powerIncreasePercent.doubleValue());
                            }
                            weapon.put("timestamp", FieldValue.serverTimestamp());
                            if(weaponName.equals("BowAndArrow")) {
                                addedPowersRef.add(weapon)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this, "Bow equiped!", Toast.LENGTH_SHORT).show();
                                            String newWeaponId = docRef.getId();
                                            addedPowersIds.add(newWeaponId);

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to equip bow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }else{
                                currentUserProfile.setPowerPoints((int) Math.ceil(currentUserProfile.getPowerPoints() * (powerIncreasePercent.doubleValue() / 100)));

                            }
                            inventoryRef.document(weaponId)
                                    .update("status", "used");
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
                        String potionDurability = doc.contains("durability") ? doc.getString("durability") : "oneTime";
                        Double powerBoost = doc.getDouble("powerBoost");
                        String status = doc.contains("status") ? doc.getString("status") : "notUsed";//used or notUsed

                        Button btn = new Button(this);

                        if(status != null && status.equals("used")){
                            btn.setEnabled(false);
                            btn.setText("Used");
                        }else{
                            btn.setText(potionName);
                        }
                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            btn.setEnabled(false);            // disable the button
                            btn.setText("Used");
                            Map<String, Object> potion = new HashMap<>();
                            potion.put("name", potionName);
                            potion.put("category", "potion");

                            potion.put("powerBoost", powerBoost);

                                        if(potionDurability.equals("oneTime")){
                                            addedPowerAmount += currentUserProfile.getPowerPoints() * (powerBoost/100);
                                            inventoryRef.document(potionId)
                                                    .delete();
                                        }else{
                                            currentUserProfile.setPowerPoints((int) Math.ceil(currentUserProfile.getPowerPoints() * powerBoost));
                                            inventoryRef.document(potionId)
                                                    .update("status", "used");
                                        }




                            Toast.makeText(this, potionName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutPotions.addView(btn);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load potions from inventory: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading inventory potions", e);
                });
    }

    private void loadClothes() {
        inventoryRef.whereEqualTo("category", "clothes")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String clothesId = doc.getId();
                        String clothesName = doc.getString("name");
                        String status = doc.contains("status") ? doc.getString("status") : "notUsed";//used or notUsed

                        Number powerBoost = null;
                        Number hitSuccessIncrease = null;
                        Number oneExtraHitChance = null;
                        Number durability = doc.contains("durability") ? doc.getDouble("durability") : Double.valueOf(2.0);
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
                        if(status.equals("used")){
                            btn.setEnabled(false);
                            btn.setText("Used");
                            if (clothesName.equals("gloves")){

                                addedPowerAmount += doc.getDouble("additionalPoints");
                            }else{
                                Map<String, Object> clothes = new HashMap<>();
                                clothes.put("name", clothesName);
                                clothes.put("category", "clothes");
                                if (clothesName.equals("shield")) {
                                    clothes.put("hitSuccessIncrease", hitSuccessIncrease);
                                } else {
                                    clothes.put("oneExtraHitChance", oneExtraHitChance);
                                }
                                addedPowersRef.add(clothes)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this,"Clothes equiped!", Toast.LENGTH_SHORT).show();
                                            String newclothId = docRef.getId();
                                            addedPowersIds.add(newclothId);

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to add potion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                            double newDurability = durability.doubleValue() - 1.0;
                            if(newDurability>0) {
                                inventoryRef.document(clothesId)
                                        .update("durability", newDurability);
                            }else {
                                inventoryRef.document(clothesId)
                                        .delete();
                            }
                        }else{
                            btn.setText(clothesName);
                        }
                        Number finalPowerBoost = powerBoost;
                        Number finalOneExtraHitChance = oneExtraHitChance;
                        Number finalHitSuccessIncrease = hitSuccessIncrease;

                        btn.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            btn.setEnabled(false);            // disable the button
                            btn.setText("Used");
                            Map<String, Object> clothes = new HashMap<>();
                            clothes.put("name", clothesName);
                            clothes.put("category", "clothes");
                            if (clothesName.equals("gloves")) {
                                double additionalPoints = ((double)currentUserProfile.getPowerPoints())*(finalPowerBoost.doubleValue()/100);
                                inventoryRef.document(clothesId)
                                        .update("additionalPoints", additionalPoints);
                                addedPowerAmount += additionalPoints;

                            } else if (clothesName.equals("shield")) {
                                clothes.put("hitSuccessIncrease", finalHitSuccessIncrease);
                            } else {
                                clothes.put("oneExtraHitChance", finalOneExtraHitChance);
                            }
                            inventoryRef.document(clothesId)
                                    .update("status", "used");
                            double newDurability = durability.doubleValue() - 1.0;

                            inventoryRef.document(clothesId)
                                    .update("durability", newDurability);

                            if(!clothesName.equals("gloves")) {
                                addedPowersRef.add(clothes)
                                        .addOnSuccessListener(docRef -> {
                                            Toast.makeText(this, "clothes equiped!", Toast.LENGTH_SHORT).show();
                                            String newclothId = docRef.getId();
                                            addedPowersIds.add(newclothId);

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to equip clothes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                            Toast.makeText(this, clothesName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutClothes.addView(btn);
                    }
                });
    }
}