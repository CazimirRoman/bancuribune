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
        // HOW do i get the id of the user entry?
		const promise = admin.database().ref("/_dev/users_dev/-LQndI3n2Qos0LjxXpDG/instanceId").once('value');

        return Promise.all([promise]).then(results =>{

        const instanceId = results[0].val();
        console.log("The instance id is: " + instanceId)

                const payload = {
                        notification: {
                            title: "Un banc nou asteapta aprobarea ta",
                            body: "Test message",
                        }
                    };

        return admin.messaging().sendToDevice(instanceId, payload)
        .then(function (response) {
            console.log("Successfully sent message:", response);
        })
        .catch(function (error) {
            console.log("Error sending message:", error);
        });

});

    });