# SecureTravel
An Android app to easily share your location via SMS in case of emergency

###Created by
- Sulyab Thottungal (sulyabtv@gmail.com)
- Mohd Abdullah
- Geetha Madhuri

###Features
- Single SMS mode to send a single location SMS
- Continuous Location Update mode to send an SMS every minute until the user turns it off
- Location is sent as Google Maps link for easy navigation
- On-screen map showing current location (user can wait till it is set to send SMS with location. If location is not received, distress message is sent without location)
- If the receiver has SecureTravel installed, a distress alarm rings upon the arrival of a distress SMS

##Stuff for developers
- Google API key is required and is to be added in google_maps_api.xml file

###Known Issues
- App does not work on certain devices with Android version older than 5.0

###TODO list
- Secret trigger (such as pressing the power button twice) to send SMS without navigating to the app
- Option to change interval between two SMS in Continuous Location Update mode
- Longer distress alarm
- (If the idea seems fit) Extend the feature to allow casual location sharing, i.e without distress message and the alarm wailing
- Battery consumption considerations
- Fix Known Issues written over there ^
