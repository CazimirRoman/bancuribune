// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.newJokeAdded = functions.database.ref('/_dev/jokes_dev/{pushId}')
    .onWrite(event => {

        const message = event.after.child("jokeText").val();
        const createdBy = event.after.child("createdBy").val();

		console.log('The joke ' + message + ' was created by: ' + createdBy)

        //get user entry associated with the added joke - this user entry contains the instanceId to use
		const promise = admin.database().ref("/_dev/users_dev/-LQndI3n2Qos0LjxXpDG/instanceId").once('value');

        return Promise.all([promise]).then(results =>{

        const instanceId = results[0].val();

                const payload = {
                        notification: {
                            title: "Un banc nou asteapta aprobarea ta",
                            body: message,
                        }
                    };

        admin.messaging().sendToDevice(instanceId, payload)
        .then(function (response) {
            console.log("Successfully sent message:", response);
        })
        .catch(function (error) {
            console.log("Error sending message:", error);
        });

        }

		//const instanceId = "e8vHqpeH_7k:APA91bEOrgaa2HnFR7H29Wcekj394b9FZoGSTjvm44ER0F79654Z6ODEDD51azZ3laUDlZktdpmHS4Zx__uIAXPvK_eXZbM6tsZv11H69Y0JcN9Y31BoNK1ErR8y43dVEn_Tpc8TlejB"


    });