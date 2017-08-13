const functions = require('firebase-functions');
const admin = require('firebase-admin');
var request = require('request');
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

		admin.messaging().sendToTopic(zipcode, payload)
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
});