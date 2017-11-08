const functions = require('firebase-functions');
const admin = require('firebase-admin');
var request = require('request');
var geodist = require('geodist')
var API_KEY = "AAAAy6R4AXo:APA91bEv7tqFhYcMvRPiA5x1JxkJ5R2p6NiE--DPhv6loNGE_ciBp2O1IWJlKwPxRRodwEMo77qy2ibADo0q6mIArtucl1lGgySzLUik5_ScoOCRN-q1EEWrTMigPXXmzNYcT1szkq03"; // Your Firebase Cloud Messaging Server API key
require('@google-cloud/debug-agent').start({ allowExpressions: true });

admin.initializeApp(functions.config().firebase);

exports.sendNotificationToZipCode = functions.database.ref("notificationRequests/{notificationID}").onWrite(event =>{
	if(event.data.val()){
		var zipcode = event.data.val().zipcode;
		var message = event.data.val().message;
		var mkey = event.data.val().key;
		var type = event.data.val().type;

		const payload = {
			/*
			notification: {
				title: "EAGER",
				click_action: "OPEN_VIEW_NOTIFICATION",
				body: message
			},*/
			data: {
				key: mkey,
				body: message,
				zipcode: zipcode,
				type: type
			}
		};
		var longitude = 0;
		var latitude = 0;

		var loc = zipcode.split("_");

		if(loc.length == 2){
			var longitude = parseFloat(loc[0]);
			var latitude = parseFloat(loc[1]);
			console.log("Report loc" + longitude + ", " +latitude);

		}

		var allSubscriptionsRef = admin.database().ref("AllSubscriptions")

		allSubscriptionsRef.orderByChild("topic").on('value', function(snapshot){
			snapshot.forEach(function(childSnapshot){
				var topic = childSnapshot.val().topic;	

				if(topic){
					var snapshotLongitude = parseFloat(topic.split("_")[0]);
					var snapshotLatitude = parseFloat(topic.split("_")[1]);

					
					var dist = 0;
					dist = geodist({lat: latitude, lon: longitude}, {lat: snapshotLatitude, lon: snapshotLongitude});
					console.log("Distance " + dist);

					switch(true){
						case (dist <= 1.0):
							sendmessage(topic+"_"+"1.0", payload, event);
							console.log("Send to 1.0");
						case (dist <= 2.0):
							sendmessage(topic+"_"+"2.0", payload, event);
							console.log("Send to 2.0");
						case (dist <= 3.0):
							sendmessage(topic+"_"+"3.0", payload, event);
							console.log("Send to 3.0");
						case (dist <= 4.0):
							sendmessage(topic+"_"+"4.0", payload, event);
							console.log("Send to 4.0");
							break;
					}
				}
			});
		});
	}
});



function sendmessage(topic, payload, event){
	admin.messaging().sendToTopic(topic, payload)
			.then(function(response) {
				// See the MessagingDeviceGroupResponse reference documentation for
				// the contents of response.
				console.log("Successfully sent message:", payload);
				event.data.ref.remove();
			})
			.catch(function(error) {
				console.log(error);
			});
}


exports.sendAck = functions.database.ref("path/{pathID}").onWrite(event =>{
	if(event.data.val()){
		console.log(event.data.val());
		var registrationToken = event.data.val().token;
		var eventKey = event.data.key;
		const payload = {
			data: {
				notificationType: "Ack",
				msg: "Report submited through radio",
				key: eventKey
			}
		};
		admin.messaging().sendToDevice(registrationToken, payload)
		.then(function(response){
			console.log("Successfully sent message:", payload);
		})
		.catch(function(error){
			console.log(error);
		});		


	}
		
});






