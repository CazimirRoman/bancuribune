// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.onJokeApproved_test = functions.database.ref('/_dev/jokes_dev/{pushId}')
    .onWrite(event => {

        var jokeId = -1;

        if((!event.before.child("approved").val()) && event.after.child("approved").val()){

        const message = event.after.child("jokeText").val();
                const createdBy = event.after.child("createdBy").val();
                jokeId = event.after.child("uid").val();

                console.log('The joke ' + message + ' was created by: ' + createdBy + ' and has the id: ' + jokeId);

                //first of all get user object based on uid for the created joke
                const getUser = admin.database().ref("/_dev/users_dev").orderByChild('userId').equalTo(createdBy).once('value')

                //return this promise first
                return Promise.all([getUser]).then(results => {

                    var userUid;

                    results[0].forEach(function(childSnapshot) {
                        var value = childSnapshot.val();
                        userUid = value.uid;
                        console.log("The key for the user is: " + value.uid)
                    });

                    //using the key of the user get access to the instanceId property in the user object
                    var getInstanceId = admin.database().ref(`/_dev/users_dev/${userUid}/instanceId`).once('value');

                    //then return this promise
                    return Promise.all([getInstanceId]).then(results => {

                        const instanceId = results[0].val();

                        console.log("The instance id is: " + instanceId)

                        const payload = {

                            data: {
                                title: "Bancul tău a fost aprobat!",
                                body: "Trimite-l prietenilor și adună voturi; astfel crești în rang.",
                                jokeId: jokeId
                            }
                        };

                        admin.messaging().sendToDevice(instanceId, payload)
                            .then(function(response) {
                                console.log("Successfully sent message:", response);
                            })
                            .catch(function(error) {
                                console.log("Error sending message:", error);
                            });
                    })
                })
        }


    });

//deploy this to production after most of the users got the new version

//exports.onJokeApproved_prod = functions.database.ref('/jokes/{pushId}')
//    .onWrite(event => {
//
//        var jokeId = -1;
//
//        if((!event.before.child("approved").val()) && event.after.child("approved").val()){
//
//        const message = event.after.child("jokeText").val();
//                const createdBy = event.after.child("createdBy").val();
//                jokeId = event.after.child("uid").val();
//
//                console.log('The joke ' + message + ' was created by: ' + createdBy + ' and has the id: ' + jokeId);
//
//                //first of all get user object based on uid for the created joke
//                const getUser = admin.database().ref("/users").orderByChild('userId').equalTo(createdBy).once('value')
//
//                //return this promise first
//                return Promise.all([getUser]).then(results => {
//
//                    var userUid;
//
//                    results[0].forEach(function(childSnapshot) {
//                        var value = childSnapshot.val();
//                        userUid = value.uid;
//                        console.log("The key for the user is: " + value.uid)
//                    });
//
//                    //using the key of the user get access to the instanceId property in the user object
//                    var getInstanceId = admin.database().ref(`/users/${userUid}/instanceId`).once('value');
//
//                    //then return this promise
//                    return Promise.all([getInstanceId]).then(results => {
//
//                        const instanceId = results[0].val();
//
//                        console.log("The instance id is: " + instanceId)
//
//                        const payload = {
//
//                            data: {
//                                title: "Bancul tău a fost aprobat!",
//                                body: "Trimite-l prietenilor și adună voturi; astfel crești în rang.",
//                                jokeId: jokeId
//                            }
//                        };
//
//                        admin.messaging().sendToDevice(instanceId, payload)
//                            .then(function(response) {
//                                console.log("Successfully sent message:", response);
//                            })
//                            .catch(function(error) {
//                                console.log("Error sending message:", error);
//                            });
//                    })
//                })
//        }
//
//
//    });