# Pennapps-Path-Guider

## Summary
Our app helps blind people to find objects in real world. Users can tell the phone directly which object they want to find, and the phone will give out voice instructions in turn. The user will need to equip a camera and raspberry pi around his head. It would also be possible for user to select an object that are not present in our supported trained data and train them directly, in future.
## Use Cycle
The user's command will be processed by Azure Speech-to-text REST API and then propagated to raspberry pi with Socket. Then our raspberry pi will process the commands from camera and radar information with OpenCV and DNN, and then propagate data back to the phone using Socket as well. Finally The phone will read out the command using Android Text-to-Speech API. We use Android(Java) for user end and Python for raspberry pi.
## User Guide
Press the phone and say sentences starting with "find", and then the phone will tell you instructions to help you find the object. 

## Android app:
Open with Android Studio 3.0.1. Invovles Azure speech-to-text REST API, socket and Android text-to-speech.

## Socket data transfer standards:
 * Pi as server and Android as client.
 * Android sends to Pi:
 	* DEF [Name] [Size]
 	* FIND [Name]
  * READY
 * Pi sends to Android:
 	* INSTR [sentence]
