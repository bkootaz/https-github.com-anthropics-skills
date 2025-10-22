const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin
admin.initializeApp();

// Process voice commands with AI
exports.processVoiceCommand = functions.https.onCall(async (data, context) => {
    try {
        const { command, context: userContext, timestamp } = data;
        
        // Log the incoming command
        console.log('Processing voice command:', command);
        
        // Simple command processing (replace with your AI service)
        const response = await processCommand(command, userContext);
        
        return {
            text: response.text,
            shouldSpeak: response.shouldSpeak,
            action: response.action,
            confidence: response.confidence,
            timestamp: Date.now()
        };
        
    } catch (error) {
        console.error('Error processing voice command:', error);
        return {
            text: "Sorry, I encountered an error processing your request.",
            shouldSpeak: true,
            action: null,
            confidence: 0.0,
            timestamp: Date.now()
        };
    }
});

// Simple command processing function
async function processCommand(command, userContext) {
    const lowerCommand = command.toLowerCase();
    
    // Time-related commands
    if (lowerCommand.includes('time') || lowerCommand.includes('what time')) {
        const now = new Date();
        const timeString = now.toLocaleTimeString();
        return {
            text: `The current time is ${timeString}`,
            shouldSpeak: true,
            action: 'tell_time',
            confidence: 0.9
        };
    }
    
    // Date-related commands
    if (lowerCommand.includes('date') || lowerCommand.includes('what date')) {
        const now = new Date();
        const dateString = now.toLocaleDateString();
        return {
            text: `Today's date is ${dateString}`,
            shouldSpeak: true,
            action: 'tell_date',
            confidence: 0.9
        };
    }
    
    // Weather commands
    if (lowerCommand.includes('weather') || lowerCommand.includes('temperature')) {
        return {
            text: "I don't have access to weather data right now, but you can check your weather app.",
            shouldSpeak: true,
            action: 'weather_info',
            confidence: 0.7
        };
    }
    
    // Greeting commands
    if (lowerCommand.includes('hello') || lowerCommand.includes('hi') || lowerCommand.includes('hey')) {
        return {
            text: "Hello! How can I help you today?",
            shouldSpeak: true,
            action: 'greeting',
            confidence: 0.9
        };
    }
    
    // Goodbye commands
    if (lowerCommand.includes('goodbye') || lowerCommand.includes('bye') || lowerCommand.includes('see you')) {
        return {
            text: "Goodbye! Have a great day!",
            shouldSpeak: true,
            action: 'goodbye',
            confidence: 0.9
        };
    }
    
    // Help commands
    if (lowerCommand.includes('help') || lowerCommand.includes('what can you do')) {
        return {
            text: "I can help you with time, date, weather information, and answer general questions. What would you like to know?",
            shouldSpeak: true,
            action: 'help',
            confidence: 0.8
        };
    }
    
    // Phone-related commands
    if (lowerCommand.includes('call') || lowerCommand.includes('phone')) {
        return {
            text: "I can't make phone calls directly, but I can help you with other tasks.",
            shouldSpeak: true,
            action: 'phone_info',
            confidence: 0.7
        };
    }
    
    // Music commands
    if (lowerCommand.includes('music') || lowerCommand.includes('play') || lowerCommand.includes('song')) {
        return {
            text: "I can't control music playback directly, but you can use your music app.",
            shouldSpeak: true,
            action: 'music_info',
            confidence: 0.7
        };
    }
    
    // Navigation commands
    if (lowerCommand.includes('navigate') || lowerCommand.includes('directions') || lowerCommand.includes('map')) {
        return {
            text: "I can't provide navigation directly, but you can use your maps app for directions.",
            shouldSpeak: true,
            action: 'navigation_info',
            confidence: 0.7
        };
    }
    
    // Default response for unrecognized commands
    return {
        text: `I heard you say "${command}". I'm still learning, but I can help with basic tasks like telling time, date, and answering questions.`,
        shouldSpeak: true,
        action: 'unknown_command',
        confidence: 0.5
    };
}

// Send notification to device
exports.sendNotification = functions.https.onCall(async (data, context) => {
    try {
        const { token, title, body, data: payload } = data;
        
        const message = {
            token: token,
            notification: {
                title: title,
                body: body
            },
            data: payload || {},
            android: {
                priority: 'high',
                notification: {
                    sound: 'default',
                    priority: 'high'
                }
            }
        };
        
        const response = await admin.messaging().send(message);
        console.log('Successfully sent message:', response);
        
        return { success: true, messageId: response };
        
    } catch (error) {
        console.error('Error sending notification:', error);
        return { success: false, error: error.message };
    }
});

// Wake up assistant
exports.wakeUpAssistant = functions.https.onCall(async (data, context) => {
    try {
        const { token } = data;
        
        const message = {
            token: token,
            data: {
                type: 'wake_up',
                timestamp: Date.now().toString()
            },
            android: {
                priority: 'high'
            }
        };
        
        const response = await admin.messaging().send(message);
        console.log('Wake up message sent:', response);
        
        return { success: true, messageId: response };
        
    } catch (error) {
        console.error('Error sending wake up message:', error);
        return { success: false, error: error.message };
    }
});