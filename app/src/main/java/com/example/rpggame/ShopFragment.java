package com.example.rpggame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rpggame.domain.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ShopFragment extends Fragment {


    private FirebaseUser currentUser;
    private UserProfile currentUserProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ZadatakRepository repository;
    Button oneTimePotion20, oneTimePotion40, permanentPotion5, permanentPotion10, glovesBtn, shieldBtn, bootsBtn, swordBtn, bowArrowBtn;

    CollectionReference inventoryRef;

    public ShopFragment() {
        // Required empty public constructor
    }






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate fragment layout instead of setContentView()
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new ZadatakRepository(getActivity().getApplication());
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadData(view);




    }
    private void loadData(View view){
        currentUser = mAuth.getCurrentUser();
        oneTimePotion20 = view.findViewById(R.id.one_time_20);
        oneTimePotion40 = view.findViewById(R.id.one_time_40);
        permanentPotion5 = view.findViewById(R.id.perma_5);
        permanentPotion10 = view.findViewById(R.id.perma_10);
        glovesBtn = view.findViewById(R.id.btn_gloves);
        shieldBtn = view.findViewById(R.id.btn_shield);
        bootsBtn = view.findViewById(R.id.btn_boots);
        swordBtn = view.findViewById(R.id.sword);
        bowArrowBtn = view.findViewById(R.id.bow_and_arrow);
        TextView usersCoins = view.findViewById(R.id.tv_coins_count);
        String userUID = currentUser.getUid();
        db.collection("users").document(userUID).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentUserProfile = doc.toObject(UserProfile.class);
                    }
                    inventoryRef = db.collection("users")
                            .document(currentUser.getUid())
                            .collection("inventory");
                    usersCoins.setText(String.valueOf(currentUserProfile.getCollectedCoins()));
                    int price20 = getPrice(50);
                    int price40 = getPrice(70);
                    int pricePerma5 = getPrice(200);
                    int pricePerma10 = getPrice(1000);
                    int priceGloves = getPrice(60);
                    int priceShield = getPrice(60);
                    int priceBoots = getPrice(80);
                    int upgradeSword = getPrice(60);
                    int upgradeBow = getPrice(60);
                    inventoryRef.get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (querySnapshot.isEmpty()) {
                                    // No items at all
                                    disableButton(swordBtn);
                                    disableButton(bowArrowBtn);

                                    return;
                                }

                                for (DocumentSnapshot inventoryItem : querySnapshot) {
                                    String name = inventoryItem.getString("name");
                                    String category = inventoryItem.getString("category");
                                    //Long quantity = inventoryItem.getLong("quantity");

                                    boolean hasItem = (name != null);

                                    if ("sword".equalsIgnoreCase(name)) {
                                        if (hasItem && currentUserProfile.getCollectedCoins() >= upgradeSword) enableButton(swordBtn); else disableButton(swordBtn);
                                    }

                                    if ("bow_and_arrow".equalsIgnoreCase(name)) {
                                        if (hasItem && currentUserProfile.getCollectedCoins() >= upgradeBow) enableButton(bowArrowBtn); else disableButton(bowArrowBtn);
                                    }


                                    // You can also check by category if needed
                                    if ("weapon".equalsIgnoreCase(category)) {
                                        // maybe enable a "weapons tab" or something
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to check inventory: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                disableButton(swordBtn);
                                disableButton(bowArrowBtn);
                                disableButton(glovesBtn);
                                disableButton(shieldBtn);
                                disableButton(bootsBtn);
                            });

                    oneTimePotion20.setText(String.valueOf(price20));
                    oneTimePotion40.setText(String.valueOf(price40));
                    permanentPotion5.setText(String.valueOf(pricePerma5));
                    permanentPotion10.setText(String.valueOf(pricePerma10));
                    glovesBtn.setText(String.valueOf(priceGloves));
                    shieldBtn.setText(String.valueOf(priceShield));
                    bootsBtn.setText(String.valueOf(priceBoots));
                    swordBtn.setText(String.valueOf(upgradeSword));
                    bowArrowBtn.setText(String.valueOf(upgradeBow));

                    // Disable button if not enough coins
                    if (currentUserProfile.getCollectedCoins() < price20) {
                        oneTimePotion20.setEnabled(false);
                        oneTimePotion20.setAlpha(0.5f); // make button look disabled
                    }

                    if (currentUserProfile.getCollectedCoins() < price40) {
                        oneTimePotion40.setEnabled(false);
                        oneTimePotion40.setAlpha(0.5f);
                    }
                    if (currentUserProfile.getCollectedCoins() < pricePerma5) {
                        permanentPotion5.setEnabled(false);
                        permanentPotion5.setAlpha(0.5f);
                    }
                    if (currentUserProfile.getCollectedCoins() < pricePerma10) {
                        permanentPotion10.setEnabled(false);
                        permanentPotion10.setAlpha(0.5f);
                    }
                    if (currentUserProfile.getCollectedCoins() < priceGloves) {
                        glovesBtn.setEnabled(false);
                        glovesBtn.setAlpha(0.5f);
                    }else{
                        glovesBtn.setEnabled(true);
                        glovesBtn.setAlpha(1.0f);
                    }
                    if (currentUserProfile.getCollectedCoins() < priceShield) {
                        shieldBtn.setEnabled(false);
                        shieldBtn.setAlpha(0.5f);
                    }else{
                        shieldBtn.setEnabled(true);
                        shieldBtn.setAlpha(1.0f);
                    }
                    if (currentUserProfile.getCollectedCoins() < priceBoots) {
                        bootsBtn.setEnabled(false);
                        bootsBtn.setAlpha(0.5f);
                    } else{
                        bootsBtn.setEnabled(true);
                        bootsBtn.setAlpha(1.0f);
                    }
                    if (currentUserProfile.getCollectedCoins() < upgradeSword) {
                        swordBtn.setEnabled(false);
                        swordBtn.setAlpha(0.5f);
                    }
                    if (currentUserProfile.getCollectedCoins() < upgradeBow) {
                        bowArrowBtn.setEnabled(false);
                        bowArrowBtn.setAlpha(0.5f);
                    }

                    oneTimePotion20.setOnClickListener(v -> {
                        // Add potion to inventory (subcollection)
                        Map<String, Object> potion = new HashMap<>();
                        potion.put("name", "Red Potion");
                        potion.put("category", "potion");
                        potion.put("powerBoost", 20);
                        potion.put("durability", "oneTime");
                        potion.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(oneTimePotion20, potion);
                    });
                    oneTimePotion40.setOnClickListener(v -> {
                        // Add potion to inventory (subcollection)
                        Map<String, Object> potion = new HashMap<>();
                        potion.put("name", "Purple Potion");
                        potion.put("category", "potion");

                        potion.put("powerBoost", 40);
                        potion.put("durability", "oneTime");
                        potion.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(oneTimePotion40, potion);
                    });
                    permanentPotion5.setOnClickListener(v -> {
                        // Add potion to inventory (subcollection)
                        Map<String, Object> potion = new HashMap<>();
                        potion.put("name", "Wine Potion");
                        potion.put("category", "potion");
                        potion.put("powerBoost", 5);
                        potion.put("durability", "infinite");
                        potion.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(permanentPotion5, potion);
                    });
                    permanentPotion10.setOnClickListener(v -> {
                        // Add potion to inventory (subcollection)
                        Map<String, Object> potion = new HashMap<>();
                        potion.put("name", "Yellow Potion");
                        potion.put("category", "potion");
                        potion.put("powerBoost", 10);
                        potion.put("durability", "infinite");

                        potion.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(permanentPotion10, potion);
                    });
                    glovesBtn.setOnClickListener(v -> {
                        // Add equipment to inventory (subcollection)
                        Map<String, Object> gloves = new HashMap<>();
                        gloves.put("name", "Gloves");
                        gloves.put("category", "clothes");
                        gloves.put("powerBoost", 10);
                        gloves.put("durability", 2);
                        gloves.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(glovesBtn, gloves);
                    });
                    shieldBtn.setOnClickListener(v -> {
                        // Add equipment to inventory (subcollection)
                        Map<String, Object> shield = new HashMap<>();
                        shield.put("name", "Shield");
                        shield.put("category", "clothes");
                        shield.put("hitSuccessIncrease", 10);
                        shield.put("durability", 2);
                        shield.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(shieldBtn, shield);
                    });
                    bootsBtn.setOnClickListener(v -> {
                        // Add equipment to inventory (subcollection)
                        Map<String, Object> boots = new HashMap<>();
                        boots.put("name", "Boots");
                        boots.put("category", "clothes");
                        boots.put("oneExtraHitChance", 40);
                        boots.put("durability", 2);
                        boots.put("timestamp", FieldValue.serverTimestamp());

                        buyPotion(bootsBtn, boots);
                    });

                    swordBtn.setOnClickListener(v -> {
                        db.collection("users").document(userUID)
                                .collection("inventory")
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot inventoryItem : querySnapshot) {

                                        if (Objects.equals(inventoryItem.getString("name"), "Sword")) {
                                            Double currentPower = inventoryItem.getDouble("powerIncreasePercent");
                                            if (currentPower == null) currentPower = 5.0;

                                            double newPower = currentPower + 0.01;

                                            upgradeWeapon("Sword","powerIncreasePercent", newPower, userUID, upgradeSword);
                                        }
                                    }

                                });
                    });
                    bowArrowBtn.setOnClickListener(v -> {
                        db.collection("users").document(userUID)
                                .collection("inventory")
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot inventoryItem : querySnapshot) {
                                        if (inventoryItem.getString("name").equals("bow_and_arrow")) {
                                            Double currentLoot = inventoryItem.getDouble("coinsIncreasePercent");
                                            if (currentLoot == null) currentLoot = 5.0;

                                            double newLoot = currentLoot + 0.01;

                                            upgradeWeapon("bow_and_arrow","coinsIncreasePercent", newLoot, userUID,upgradeBow);
                                        }
                                    }

                                });
                    });

                });
    }
    // helper functions
    private void disableButton(Button btn) {
        btn.setEnabled(false);
        btn.setAlpha(0.5f);
    }

    private void enableButton(Button btn) {
        btn.setEnabled(true);
        btn.setAlpha(1f);
    }
    private void upgradeWeapon(String weapon, String property, double newValue, String userUID, int price){
        // update Firestore
        db.collection("users").document(userUID)
                .collection("inventory")
                .whereEqualTo("name", weapon) // find by "name" field
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            // Update weapon property
                            doc.getReference()
                                    .update(property, newValue)
                                    .addOnSuccessListener(aVoid -> {
                                        // Deduct coins after upgrade succeeds
                                        db.collection("users").document(userUID)
                                                .update("collectedCoins", FieldValue.increment(-price))
                                                .addOnSuccessListener(v -> {

                                                    loadData(requireView());
                                                        }

                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getContext(), "Failed to update coins: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    });

                        }
                    } else {
                        Toast.makeText(getContext(), "Weapon " + weapon + " not found in inventory", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error finding weapon: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }
    private void buyPotion(Button potionBtn, Map<String, Object> potionVal){
        // Parse price from button text
        int price = Integer.parseInt(potionBtn.getText().toString());

        if (currentUserProfile.getCollectedCoins() >= price) {
            // Deduct coins from user
            int newCoins = currentUserProfile.getCollectedCoins() - price;
            currentUserProfile.setCollectedCoins(newCoins);

            // Save coins update to Firestore
            db.collection("users")
                    .document(currentUser.getUid())
                    .update("collectedCoins", newCoins)
                    .addOnSuccessListener(aVoid -> {
                        // Add potion to inventory (subcollection)


                        inventoryRef.add(potionVal)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(getContext(), "Item bought!", Toast.LENGTH_SHORT).show();

                                    if (currentUserProfile.getClanId() != null && !currentUserProfile.getClanId().isEmpty()) {
                                        repository.nanesiStetuMisiji(currentUserProfile.getClanId(), ZadatakRepository.AkcijaMisije.KUPOVINA, null, (success, message, damage) -> {
                                            if (success) {
                                                if(getActivity() != null) {
                                                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Tvoja kupovina je nanela " + damage + " HP štete bosu misije!", Toast.LENGTH_SHORT).show());
                                                }
                                            }
                                        });
                                    }
                                    loadData(requireView());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to add potion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update coins: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private int getPrice(int percentIncrease){
        //Get coins gain for current level
        int currentLevel = 2;
        int coinsGainAfterBoss = 200;
        if(currentUserProfile.getLevel() > 1) {
            while (currentLevel <= currentUserProfile.getLevel()) {
                coinsGainAfterBoss += (int) (coinsGainAfterBoss*0.2);
                currentLevel++;
            }
        }
        //Get price for asset
        return (int) (coinsGainAfterBoss * (percentIncrease / 100.0));
    }
}