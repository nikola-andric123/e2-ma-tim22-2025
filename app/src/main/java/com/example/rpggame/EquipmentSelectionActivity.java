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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private TextView usersPowerPoints;
    private TextView addedPowerPoints;
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
        addedPowerPoints = findViewById(R.id.added_power_points);
        btnContinue = findViewById(R.id.btn_continue_to_battle);
        usersPowerPoints = findViewById(R.id.users_power_points);
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
                    assert currentUserProfile != null;
                    usersPowerPoints.setText(String.valueOf(currentUserProfile.getPowerPoints()));
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
                        View cardView = getLayoutInflater().inflate(R.layout.item_card, layoutWeapons, false);

                        TextView itemName = cardView.findViewById(R.id.item_name);
                        ImageView itemImage = cardView.findViewById(R.id.item_image);
                        Button itemButton = cardView.findViewById(R.id.item_button);
                        itemName.setText(weaponName);

                        Number coinsIncreasePercent;
                        Number powerIncreasePercent;
                        if(weaponName.equals("bow_and_arrow")){
                            powerIncreasePercent = null;
                            coinsIncreasePercent = (Number) doc.get("coinsIncreasePercent");
                            itemImage.setImageResource(R.drawable.bow_and_arrow);
                        }else{
                            coinsIncreasePercent = null;
                            powerIncreasePercent = (Number) doc.get("powerIncreasePercent");
                            itemImage.setImageResource(R.drawable.sword);
                        }



                        if(status.equals("used")){
                            itemButton.setEnabled(false);
                            itemButton.setText("Used");
                            if(weaponName.equals("bow_and_arrow")){
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
                            itemButton.setText(weaponName);
                        }
                        itemButton.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            itemButton.setEnabled(false);            // disable the button
                            itemButton.setText("Used");
                            Map<String, Object> weapon = new HashMap<>();
                            weapon.put("name", weaponName);
                            weapon.put("category", "weapon");
                            if(coinsIncreasePercent != null){
                                weapon.put("coinsIncreasePercent", coinsIncreasePercent.doubleValue());

                            }else{
                                weapon.put("powerIncreasePercent", powerIncreasePercent.doubleValue());
                            }
                            weapon.put("timestamp", FieldValue.serverTimestamp());
                            if(weaponName.equals("bow_and_arrow")) {
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
                                currentUserProfile.setPowerPoints((int) Math.ceil(currentUserProfile.getPowerPoints() + (double) currentUserProfile.getPowerPoints() * (powerIncreasePercent.doubleValue() / 100)));

                            }
                            inventoryRef.document(weaponId)
                                    .update("status", "used");
                            Toast.makeText(this, weaponName + " selected!", Toast.LENGTH_SHORT).show();
                        });


                        layoutWeapons.addView(cardView);
                    }
                });
    }

    private void loadPotions() {
        inventoryRef.whereEqualTo("category", "potion")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        String potionId = doc.getId();
                        View cardView = getLayoutInflater().inflate(R.layout.item_card, layoutPotions, false);
                        TextView itemName = cardView.findViewById(R.id.item_name);
                        ImageView itemImage = cardView.findViewById(R.id.item_image);
                        Button itemButton = cardView.findViewById(R.id.item_button);

                        String potionName = doc.getString("name");
                        String potionDurability = doc.contains("durability") ? doc.getString("durability") : "oneTime";
                        Double powerBoost = doc.getDouble("powerBoost");
                        String status = doc.contains("status") ? doc.getString("status") : "notUsed";//used or notUsed
                        itemName.setText(potionName);
                        switch (potionName){
                            case "Red Potion" : itemImage.setImageResource(R.drawable.red_potion); break;
                            case "Purple Potion": itemImage.setImageResource(R.drawable.purple_potion); break;
                            case "Wine Potion": itemImage.setImageResource(R.drawable.wine_potion); break;
                            case "Yellow Potion": itemImage.setImageResource(R.drawable.yellow_potion); break;
                            default:
                                itemImage.setImageResource(R.drawable.back_arrow); // fallback
                                break;
                        }
                        //itemImage.setImageResource(R.drawable.sword);


                        //Button btn = new Button(this);

                        if(status != null && status.equals("used")){
                            itemButton.setEnabled(false);
                            itemButton.setText("Used");
                        }else{
                            itemButton.setText(potionName);
                        }
                        itemButton.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            itemButton.setEnabled(false);            // disable the button
                            itemButton.setText("Used");
                            Map<String, Object> potion = new HashMap<>();
                            potion.put("name", potionName);
                            potion.put("category", "potion");

                            potion.put("powerBoost", powerBoost);

                                        if(potionDurability.equals("oneTime")){
                                            addedPowerAmount += currentUserProfile.getPowerPoints() * (powerBoost/100);
                                            addedPowerPoints.setText(String.valueOf(addedPowerAmount));
                                            inventoryRef.document(potionId)
                                                    .delete();
                                        }else{
                                            currentUserProfile.setPowerPoints(currentUserProfile.getPowerPoints() + (int) ((double) currentUserProfile.getPowerPoints() * (powerBoost/100)));
                                            inventoryRef.document(potionId)
                                                    .update("status", "used");
                                        }
                            Toast.makeText(this, potionName + " selected!", Toast.LENGTH_SHORT).show();
                        });

                        layoutPotions.addView(cardView);
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
                        View cardView = getLayoutInflater().inflate(R.layout.item_card, layoutClothes, false);
                        TextView itemName = cardView.findViewById(R.id.item_name);
                        ImageView itemImage = cardView.findViewById(R.id.item_image);
                        Button itemButton = cardView.findViewById(R.id.item_button);
                        itemName.setText(clothesName);

                        Number powerBoost = null;
                        Number hitSuccessIncrease = null;
                        Number oneExtraHitChance = null;
                        Number durability = doc.contains("durability") ? doc.getDouble("durability") : Double.valueOf(2.0);
                        if (clothesName.equalsIgnoreCase("Gloves")) {
                            powerBoost = (Number) doc.getDouble("powerBoost");
                            itemImage.setImageResource(R.drawable.gloves);
                        } else if (clothesName.equalsIgnoreCase("Shield")) {
                            hitSuccessIncrease = (Number) doc.getDouble("hitSuccessIncrease");
                            itemImage.setImageResource(R.drawable.shield);
                            powerBoost = null;
                        } else {
                            oneExtraHitChance = (Number) doc.getDouble("oneExtraHitChance");
                            itemImage.setImageResource(R.drawable.boots);
                            powerBoost = null;
                        }



                        //Button btn = new Button(this);
                        if(status.equals("used")){
                            itemButton.setEnabled(false);
                            itemButton.setText("Used");
                            if (clothesName.equalsIgnoreCase("gloves")){

                                addedPowerAmount += doc.getDouble("additionalPoints");
                            }else{
                                Map<String, Object> clothes = new HashMap<>();
                                clothes.put("name", clothesName);
                                clothes.put("category", "clothes");
                                if (clothesName.equalsIgnoreCase("shield")) {
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
                            itemButton.setText(clothesName);
                        }
                        Number finalPowerBoost = powerBoost;
                        Number finalOneExtraHitChance = oneExtraHitChance;
                        Number finalHitSuccessIncrease = hitSuccessIncrease;

                        itemButton.setOnClickListener(v -> {
                            //selectedWeaponId = weaponId;
                            itemButton.setEnabled(false);            // disable the button
                            itemButton.setText("Used");
                            Map<String, Object> clothes = new HashMap<>();
                            clothes.put("name", clothesName);
                            clothes.put("category", "clothes");
                            if (clothesName.equalsIgnoreCase("Gloves")) {
                                double additionalPoints = ((double)currentUserProfile.getPowerPoints())*(finalPowerBoost.doubleValue()/100);
                                inventoryRef.document(clothesId)
                                        .update("additionalPoints", additionalPoints);
                                addedPowerAmount += additionalPoints;
                                addedPowerPoints.setText(String.valueOf(addedPowerAmount));

                            } else if (clothesName.equalsIgnoreCase("Shield")) {
                                clothes.put("hitSuccessIncrease", finalHitSuccessIncrease);
                            } else {
                                clothes.put("oneExtraHitChance", finalOneExtraHitChance);
                            }
                            inventoryRef.document(clothesId)
                                    .update("status", "used");
                            double newDurability = durability.doubleValue() - 1.0;

                            inventoryRef.document(clothesId)
                                    .update("durability", newDurability);

                            if(!clothesName.equalsIgnoreCase("gloves")) {
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

                        layoutClothes.addView(cardView);
                    }
                });
    }
}