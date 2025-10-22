#!/bin/bash

# AutoAI Assistant Deployment Script
# This script helps deploy the Firebase Functions and build the Android app

set -e

echo "🚀 AutoAI Assistant Deployment Script"
echo "====================================="

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found. Please install it first:"
    echo "   npm install -g firebase-tools"
    exit 1
fi

# Check if user is logged in to Firebase
if ! firebase projects:list &> /dev/null; then
    echo "🔐 Please log in to Firebase:"
    firebase login
fi

# Deploy Firebase Functions
echo "📦 Deploying Firebase Functions..."
cd firebase-functions

if [ ! -d "node_modules" ]; then
    echo "📥 Installing dependencies..."
    npm install
fi

echo "🚀 Deploying functions..."
firebase deploy --only functions

cd ..

# Build Android app
echo "🔨 Building Android app..."
cd app

# Check if google-services.json exists
if [ ! -f "google-services.json" ]; then
    echo "⚠️  Warning: google-services.json not found!"
    echo "   Please download it from Firebase Console and place it in the app/ directory"
    echo "   You can download it from: https://console.firebase.google.com/"
    echo "   Project Settings > General > Your apps > Android app"
fi

# Build the app
echo "🔨 Building APK..."
./gradlew assembleDebug

echo "✅ Build complete!"
echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"

cd ..

echo ""
echo "🎉 Deployment complete!"
echo ""
echo "Next steps:"
echo "1. Install the APK on your Android device"
echo "2. Grant all required permissions"
echo "3. Enable device admin access"
echo "4. Enable accessibility service"
echo "5. Test voice activation with 'Hey Assistant'"
echo ""
echo "For detailed setup instructions, see README.md"