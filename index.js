const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

const serviceAccount = require("./serviceAccountKey.json");


admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://group-4-planning-game.firebaseio.com",
});


const transporter = nodemailer.createTransport({
    service: "dal",
    auth: {
        user: "test01@dal.ca",
        pass: "123", ]
    },
});


exports.sendEmailNotification = functions.database
    .ref("/chats/{offerID}/messages/{messageID}")
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.val();
        const { email, chatMessage } = messageData;


        const offerID = context.params.offerID;
        const recipientEmailSnapshot = await admin
            .database()
            .ref(`/offers/${offerID}/recipientEmail`)
            .once("value");
        const recipientEmail = recipientEmailSnapshot.val();

        if (!recipientEmail) {
            console.log("Recipient email not found.");
            return null;
        }


        const mailOptions = {
            from: "your-email@gmail.com",
            to: recipientEmail,
            subject: "New Message Notification",
            text: `${email} sent you a message: "${chatMessage}"`,
        };

        try {
            await transporter.sendMail(mailOptions);
            console.log("Email sent successfully.");
        } catch (error) {
            console.error("Error sending email:", error);
        }

        return null;
    });
