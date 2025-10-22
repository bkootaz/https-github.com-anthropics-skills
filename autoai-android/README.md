# AutoAI Assistant - Voice-Controlled Android App

A complete voice assistant Android application that can replace Google Assistant with always-on voice activation, even when the phone is locked.

## Features

- **Always-on Voice Activation**: Listens for wake words like "Hey Assistant" or "Hello Assistant"
- **Lock Screen Override**: Works even when the phone is locked
- **Firebase Integration**: Uses Firebase Functions for AI processing
- **Speech Recognition**: Built-in speech-to-text capabilities
- **Text-to-Speech**: Natural voice responses
- **Accessibility Service**: Background voice activation
- **Device Admin**: Lock screen override permissions

## Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 26+ (Android 8.0+)
- Firebase project
- Google Play Services

## Setup Instructions

### 1. Firebase Setup

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable the following services:
   - Authentication
   - Firestore
   - Functions
   - Cloud Messaging
3. Download `google-services.json` and place it in the `app/` directory
4. Update the Firebase configuration in `google-services.json` with your project details

### 2. Deploy Firebase Functions

```bash
cd firebase-functions
npm install
firebase login
firebase use --add  # Select your Firebase project
firebase deploy --only functions
```

### 3. Android App Setup

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and install the app on your device

### 4. Required Permissions

The app requires the following permissions:
- `RECORD_AUDIO` - For voice recognition
- `SYSTEM_ALERT_WINDOW` - For lock screen override
- `USE_FULL_SCREEN_INTENT` - For full-screen assistant
- `SHOW_WHEN_LOCKED` - To show on lock screen
- `TURN_SCREEN_ON` - To wake up the screen
- `POST_NOTIFICATIONS` - For background notifications
- `FOREGROUND_SERVICE` - For background voice activation
- `FOREGROUND_SERVICE_MICROPHONE` - For microphone access in background

### 5. Device Admin Setup

1. Install the app
2. Open the app and tap "Enable Device Admin"
3. Grant device admin access when prompted

### 6. Accessibility Service Setup

1. Go to Settings > Accessibility
2. Find "AutoAI Assistant" in the services list
3. Enable the service
4. Grant all required permissions

## Usage

1. Launch the app
2. Grant all required permissions
3. Enable device admin and accessibility service
4. The app will start listening for wake words in the background
5. Say "Hey Assistant" or "Hello Assistant" to activate
6. The assistant will appear even on the lock screen

## Architecture

### Core Components

- **VoiceActivationService**: Always-on background service for wake word detection
- **AssistantActivity**: Main UI that can override lock screen
- **SpeechRecognitionService**: Handles speech-to-text conversion
- **TextToSpeechService**: Handles text-to-speech conversion
- **FirebaseAIService**: Integrates with Firebase Functions for AI processing
- **VoiceAccessibilityService**: Accessibility service for background activation

### Firebase Functions

- `processVoiceCommand`: Main AI processing function
- `sendNotification`: Send notifications to the device
- `wakeUpAssistant`: Wake up the assistant remotely

## Customization

### Adding New Commands

1. Edit `firebase-functions/index.js`
2. Add new command patterns in the `processCommand` function
3. Deploy the updated functions:
   ```bash
   firebase deploy --only functions
   ```

### Wake Word Detection

The current implementation uses a simple energy-based detection. For production use, integrate with:
- [Picovoice Porcupine](https://picovoice.ai/platform/porcupine/)
- [Google ML Kit](https://developers.google.com/ml-kit)
- Custom wake word detection models

### UI Customization

- Edit `AssistantActivity.kt` for UI changes
- Modify `MainActivity.kt` for the main screen
- Update themes in `themes.xml`

## Troubleshooting

### Common Issues

1. **Voice activation not working**
   - Check microphone permissions
   - Ensure accessibility service is enabled
   - Verify the app is not battery optimized

2. **Lock screen override not working**
   - Enable device admin access
   - Check if the device supports lock screen override
   - Some devices may require additional permissions

3. **Firebase connection issues**
   - Verify `google-services.json` is correctly placed
   - Check Firebase project configuration
   - Ensure functions are deployed

### Debug Mode

Enable debug logging by setting `Log.isLoggable("AutoAI", Log.DEBUG)` in your code.

## Security Considerations

- The app requires extensive permissions for full functionality
- Device admin access allows the app to override security features
- Always review permissions before granting access
- Consider implementing additional security measures for production use

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review Firebase documentation
3. Open an issue on GitHub

## Future Enhancements

- [ ] Integration with popular AI services (OpenAI, Google AI)
- [ ] Custom wake word training
- [ ] Multi-language support
- [ ] Voice cloning for personalized responses
- [ ] Integration with smart home devices
- [ ] Offline voice processing capabilities