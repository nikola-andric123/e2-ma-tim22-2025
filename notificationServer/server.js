import express from "express";
import admin from "firebase-admin";
import { readFileSync } from "fs";

const app = express();
app.use(express.json()); // ✅ enables JSON body parsing



let serviceAccount;

if (process.env.FIREBASE_SERVICE_ACCOUNT) {
  // Running on Render: load from environment variable
  serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
} else {
  // Running locally: load from file
  serviceAccount = JSON.parse(readFileSync("./service-account.json", "utf8"));
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

app.post("/send", async (req, res) => {
  const { targetToken, data,notification, title, body } = req.body;
  console.log("Received request:", req.body); // should now log full JSON

  if (!targetToken) {
    return res.status(400).json({ error: "Missing targetToken" });
  }

  try {
    const message = {
  token: targetToken,
  data: {
    type: "CLAN_INVITE",
    clanId: data.clanId, // or clanId
    senderId: data.senderId
  },
  notification: {
    title: notification.title,
    body: notification.body
  }
};
    const response = await admin.messaging().send(message);
    res.status(200).json({ success: true, response });
  } catch (error) {
    console.error("Error sending message:", error);
    res.status(500).json({ error: error.message });
  }
});

app.post("/sendRequestAccepted", async (req, res) => {
  const { targetToken, data, notification } = req.body;
  console.log("Received Accepted request:", req.body); // should now log full JSON

  if (!targetToken) {
    return res.status(400).json({ error: "Missing targetToken" });
  }

  try {
    const message = {
  token: targetToken,
  data: {
    type: data.type,
    clanId: data.clanId, // or clanId
    clanName: data.clanName,
    
  },
  notification
};
    const response = await admin.messaging().send(message);
    res.status(200).json({ success: true, response });
  } catch (error) {
    console.error("Error sending message:", error);
    res.status(500).json({ error: error.message });
  }
});



app.listen(3000, () => {
  console.log("Server running on http://localhost:3000");
});
