# AutoAI Assistant - Complete Setup Guide

This guide will walk you through setting up a complete voice assistant Android app that can replace Google Assistant with always-on voice activation.

## 🎯 What You'll Build

A fully functional voice assistant that:
- ✅ Listens for wake words like "Hey Assistant" or "Hello Assistant"
- ✅ Works even when your phone is locked
- ✅ Overrides the lock screen to show the assistant
- ✅ Uses Firebase for AI processing
- ✅ Provides natural voice responses
- ✅ Runs in the background continuously

## 📋 Prerequisites

Before starting, ensure you have:
- [ ] Android Studio (Arctic Fox or later)
- [ ] Android device with Android 8.0+ (API 26+)
- [ ] Google account for Firebase
- [ ] Internet connection

## 🚀 Step-by-Step Setup

### Step 1: Firebase Project Setup

1. **Create Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Create a project"
   - Name it "AutoAI Assistant" (or any name you prefer)
   - Enable Google Analytics (optional)

2. **Enable Required Services**
   - Go to "Authentication" → Enable "Email/Password" (for user management)
   - Go to "Firestore Database" → Create database in test mode
   - Go to "Functions" → Enable Cloud Functions
   - Go to "Cloud Messaging" → This will be enabled automatically

3. **Add Android App**
   - Click "Add app" → Android
   - Package name: `com.autoai.app`
   - App nickname: "AutoAI Assistant"
   - Download `google-services.json`

4. **Place Configuration File**
   - Move the downloaded `google-services.json` to `/workspace/autoai-android/app/`
   - This file contains your Firebase project configuration

### Step 2: Deploy Firebase Functions

1. **Install Firebase CLI** (if not already installed)
   ```bash
   npm install -g firebase-tools
   ```

2. **Login to Firebase**
   ```bash
   firebase login
   ```

3. **Deploy Functions**
   ```bash
   cd /workspace/autoai-android/firebase-functions
   npm install
   firebase use --add  # Select your Firebase project
   firebase deploy --only functions
   ```

### Step 3: Build and Install Android App

1. **Open in Android Studio**
   - Open Android Studio
   - Open the project: `/workspace/autoai-android`
   - Wait for Gradle sync to complete

2. **Build the App**
   ```bash
   cd /workspace/autoai-android
   ./deploy.sh
   ```

3. **Install on Device**
   - Connect your Android device via USB
   - Enable USB Debugging in Developer Options
   - Run the app from Android Studio or install the APK

### Step 4: Configure App Permissions

1. **Open the App**
   - Launch "AutoAI Assistant" on your device
   - You'll see the main setup screen

2. **Grant Permissions**
   - Tap "Grant Permissions"
   - Allow all requested permissions:
     - Microphone access
     - Display over other apps
     - Notification access
     - Background activity

3. **Enable Device Admin**
   - Tap "Enable Device Admin"
   - This allows the app to override the lock screen
   - Follow the system prompts

4. **Enable Accessibility Service**
   - Go to Settings → Accessibility
   - Find "AutoAI Assistant" in the services list
   - Enable the service
   - Grant all required permissions

### Step 5: Test Voice Activation

1. **Start Background Service**
   - The app should automatically start listening
   - You'll see a notification: "Listening for wake words..."

2. **Test Wake Words**
   - Say "Hey Assistant" or "Hello Assistant"
   - The assistant should appear even on the lock screen
   - Try asking: "What time is it?" or "Hello"

3. **Verify Lock Screen Override**
   - Lock your phone
   - Say a wake word
   - The assistant should appear over the lock screen

## 🔧 Troubleshooting

### Voice Activation Not Working

**Problem**: Wake words aren't being detected
**Solutions**:
- Check microphone permissions in Settings
- Ensure the app isn't battery optimized
- Restart the accessibility service
- Check if other apps are using the microphone

### Lock Screen Override Not Working

**Problem**: Assistant doesn't appear on lock screen
**Solutions**:
- Verify device admin is enabled
- Check if your device supports lock screen override
- Some devices require additional security settings
- Try disabling other lock screen apps

### Firebase Connection Issues

**Problem**: App can't connect to Firebase
**Solutions**:
- Verify `google-services.json` is in the correct location
- Check your internet connection
- Ensure Firebase Functions are deployed
- Check Firebase Console for error logs

### App Crashes or Freezes

**Problem**: App stops working unexpectedly
**Solutions**:
- Check device storage space
- Restart the app
- Clear app data and reconfigure
- Check Android system logs for errors

## 🎨 Customization

### Adding New Voice Commands

1. **Edit Firebase Functions**
   ```javascript
   // In firebase-functions/index.js
   if (lowerCommand.includes('your_command')) {
       return {
           text: "Your response",
           shouldSpeak: true,
           action: 'your_action',
           confidence: 0.9
       };
   }
   ```

2. **Deploy Changes**
   ```bash
   firebase deploy --only functions
   ```

### Changing Wake Words

1. **Edit Wake Word Detection**
   ```kotlin
   // In VoiceActivationService.kt
   private val wakeWords = listOf(
       "hey assistant",
       "hello assistant", 
       "okay assistant",
       "your custom wake word"  // Add your custom wake word
   )
   ```

2. **Rebuild and Install**
   ```bash
   ./deploy.sh
   ```

### UI Customization

1. **Modify Assistant Screen**
   - Edit `AssistantActivity.kt`
   - Change colors, layout, or animations

2. **Update Main Screen**
   - Edit `MainActivity.kt`
   - Customize the setup interface

## 🔒 Security Considerations

- **Permissions**: The app requires extensive permissions for full functionality
- **Device Admin**: Allows override of security features
- **Background Access**: Continuously listens for wake words
- **Data Privacy**: Voice data is processed by Firebase

## 📱 Device Compatibility

**Minimum Requirements**:
- Android 8.0 (API 26)
- 2GB RAM
- Microphone access
- Internet connection

**Tested Devices**:
- Google Pixel series
- Samsung Galaxy series
- OnePlus devices
- Most modern Android devices

## 🚀 Advanced Features

### Integration with Other Services

1. **Weather API**
   - Add weather service integration
   - Update Firebase Functions to fetch weather data

2. **Smart Home Control**
   - Integrate with smart home platforms
   - Add voice commands for device control

3. **Calendar Integration**
   - Connect to Google Calendar
   - Add scheduling and reminder features

### Performance Optimization

1. **Battery Optimization**
   - Implement smart wake word detection
   - Add power management features

2. **Memory Management**
   - Optimize audio processing
   - Implement efficient background services

## 📞 Support

If you encounter issues:

1. **Check the troubleshooting section above**
2. **Review Firebase documentation**
3. **Check Android system logs**
4. **Open an issue on GitHub**

## 🎉 Congratulations!

You now have a fully functional voice assistant that can replace Google Assistant! The app will:

- ✅ Listen for wake words continuously
- ✅ Work on the lock screen
- ✅ Process voice commands with AI
- ✅ Provide natural voice responses
- ✅ Run in the background

Enjoy your new voice assistant! 🎤🤖