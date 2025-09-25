package com.example.rpggame.helper;

import com.example.rpggame.domain.Boots;
import com.example.rpggame.domain.BowAndArrow;
import com.example.rpggame.domain.Gloves;
import com.example.rpggame.domain.Item;
import com.example.rpggame.domain.Potion;
import com.example.rpggame.domain.Shield;
import com.example.rpggame.domain.Sword;
import com.google.firebase.firestore.DocumentSnapshot;

public class ItemFactory {
    public static Item fromDocument(DocumentSnapshot doc) {
        if (!doc.exists()) return null;

        String category = doc.getString("category");
        if (category == null) return null;

        switch (category) {
            case "potion":
                return doc.toObject(Potion.class);

            case "clothes":
                // check subtype by name or by another field
                String name = doc.getString("name");
                if ("Gloves".equalsIgnoreCase(name)) {
                    return doc.toObject(Gloves.class);
                } else if ("Shield".equalsIgnoreCase(name)) {
                    return doc.toObject(Shield.class);
                } else if ("Boots".equalsIgnoreCase(name)) {
                    return doc.toObject(Boots.class);
                }
                break;

            case "weapon":
                String type = doc.getString("name");
                if ("Sword".equalsIgnoreCase(type)) {
                    return doc.toObject(Sword.class);
                } else if ("bow_and_arrow".equalsIgnoreCase(type) ) {
                    return doc.toObject(BowAndArrow.class);
                }
                break;
        }

        // fallback → generic
        return doc.toObject(Item.class);
    }
}
