// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

const RANK_UPDATED = "rank_updated"
const JOKE_APPROVED = "joke_approved"

exports.onJokeApproved_test = functions.database.ref('/_dev/jokes_dev/{pushId}')
    .onWrite(event => {

        var jokeId = -1;

        if ((!event.before.child("approved").val()) && event.after.child("approved").val()) {

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
                            title: "Bancul tău a fost aprobat, felicitări!",
                            body: "Trimite-l prietenilor, adună voturi și urcă în rang. Click aici să vezi bancul.",
                            regards: JOKE_APPROVED,
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

exports.onJokeApproved_prod = functions.database.ref('/jokes/{pushId}')
    .onWrite(event => {

        var jokeId = -1;

        if ((!event.before.child("approved").val()) && event.after.child("approved").val()) {

            const message = event.after.child("jokeText").val();
            const createdBy = event.after.child("createdBy").val();
            jokeId = event.after.child("uid").val();

            console.log('The joke ' + message + ' was created by: ' + createdBy + ' and has the id: ' + jokeId);

            //first of all get user object based on uid for the created joke
            const getUser = admin.database().ref("/users").orderByChild('userId').equalTo(createdBy).once('value')

            //return this promise first
            return Promise.all([getUser]).then(results => {

                var userUid;

                results[0].forEach(function(childSnapshot) {
                    var value = childSnapshot.val();
                    userUid = value.uid;
                    console.log("The key for the user is: " + value.uid)
                });

                //using the key of the user get access to the instanceId property in the user object
                var getInstanceId = admin.database().ref(`/users/${userUid}/instanceId`).once('value');

                //then return this promise
                return Promise.all([getInstanceId]).then(results => {

                    const instanceId = results[0].val();

                    console.log("The instance id is: " + instanceId)

                    const payload = {

                        data: {
                            title: "Bancul tău a fost aprobat, felicitări!",
                            body: "Trimite-l prietenilor, adună voturi și urcă în rang. Click aici să vezi bancul.",
                            regards: JOKE_APPROVED,
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

exports.onRankUpdated_test = functions.database.ref('/_dev/ranks_dev/{pushId}')
    .onWrite(event => {

        var jokeId = -1;
        var instanceId = null;
        var createdBy = event.after.child("userId").val();
        var currentRank = event.after.child("rank").val();

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

                if (currentRank === 'Hamsie') {

                    var payload = {

                        data: {
                            title: `În bancul de pești ești ${currentRank}.`,
                            body: `Adaugă bancuri, adună voturi și trimite-le prietenilor că să ajungi un pește mai mare. Verifică aici numărul de voturi până la următorul rang în bancul de pești.`,
                            regards: RANK_UPDATED
                        }

                    };

                } else {

                    var payload = {

                        data: {
                            title: `Ai urcat în rangul peștilor, felicitări!`,
                            body: `Acum ești ${currentRank}. \nAdaugă bancuri, adună voturi și trimite-le prietenilor ca să ajungi un pește mai mare. Verifică aici numărul de voturi până la următorul rang în bancul de pești.`,
                            regards: RANK_UPDATED
                        }

                    };

                }

                admin.messaging().sendToDevice(instanceId, payload)
                    .then(function(response) {
                        console.log("Successfully sent message:", response);
                    })
                    .catch(function(error) {
                        console.log("Error sending message:", error);
                    });
            })
        })

    });
//wait for last version to be on the store.

//        exports.onRankUpdated_prod = functions.database.ref('/ranks/{pushId}')
//                .onWrite(event => {
//
//                    var jokeId = -1;
//                    var instanceId = null;
//                    var createdBy = event.after.child("userId").val();
//                    var currentRank = event.after.child("rank").val();
//
//                        //first of all get user object based on uid for the created joke
//                        const getUser = admin.database().ref("/users").orderByChild('userId').equalTo(createdBy).once('value')
//
//                        //return this promise first
//                        return Promise.all([getUser]).then(results => {
//
//                            var userUid;
//
//                            results[0].forEach(function(childSnapshot) {
//                                var value = childSnapshot.val();
//                                userUid = value.uid;
//                                console.log("The key for the user is: " + value.uid)
//                            });
//
//                            //using the key of the user get access to the instanceId property in the user object
//                            var getInstanceId = admin.database().ref(`/users/${userUid}/instanceId`).once('value');
//
//                            //then return this promise
//                            return Promise.all([getInstanceId]).then(results => {
//
//                                const instanceId = results[0].val();
//
//                                console.log("The instance id is: " + instanceId)
//
//                                const payload = {
//
//                                    if(currentRank === 'Hamsie'){
//
//                                    data: {
//                                        title: `În bancul de pești ești ${currentRank}.`,
//                                        body: `Adaugă bancuri, adună voturi și trimite-le prietenilor că să ajungi un pește mai mare. Verifică aici numărul de voturi până la următorul rang în bancul de pești.`,
//                                        regards: RANK_UPDATED
//                                    }
//
//                                    }else{
//
//                                    data: {
//                                        title: `Ai urcat în rangul peștilor, felicitari!`,
//                                        body: `Acum ești ${currentRank}. \nAdaugă bancuri, adună voturi și trimite-le prietenilor că să ajungi un pește mai mare. Verifică aici numărul de voturi până la următorul rang în bancul de pești.`,
//                                        regards: RANK_UPDATED
//                                    }
//
//                                    }
//
//
//                                };
//
//                                admin.messaging().sendToDevice(instanceId, payload)
//                                    .then(function(response) {
//                                        console.log("Successfully sent message:", response);
//                                    })
//                                    .catch(function(error) {
//                                        console.log("Error sending message:", error);
//                                    });
//                            })
//                        })
//
//                });