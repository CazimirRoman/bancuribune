// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.newJokeAdded = functions.database.ref('/_dev/jokes_dev/{pushId}')
    .onWrite(event => {
        const message = event.after.child("jokeText").val();
        const createdBy = event.after.child("createdBy").val();
		console.log('This joke was created by: ' + createdBy)
		
		const getInstanceIdPromise = admin.database().ref(`/users/${createdBy}/instanceId`).once('value');
		console.log("This promise has id: " + getInstanceIdPromise)

			
		return Promise.all([getInstanceIdPromise]).then(results => {
			
            const instanceId = results[0].val();
            
            console.log('got instance id: ' + results);

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
        });
		
    });