# AutoAI Android (Prototype)

A portable offline-first automotive assistant for Ford Falcons and Holden Commodores. RAG over AU forums and dealer sources; blocks eBay. Dark-mode UI, on-device LLM (stub), ONNX embeddings (stub), crawler respecting robots.txt.

## Build (debug APK)

- Requirements: Android Studio Hedgehog+ or JDK 17; Android SDK 35; Gradle 8.9
- Open project in Android Studio and build `:app` -> `assembleDebug`.
- Or via CLI: `./gradlew :app:assembleDebug` (ensure Gradle installed if wrapper fails).
- APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- LLM and embeddings currently stubs; wire llama.cpp GGUF and an ONNX embedding model.
- Settings allow adding system instructions, allowlist and blocklist.
- Sync worker seeds a few AU domains. Extend to deep crawl, apply rate limits.
- RAG includes abstention policy when insufficient sources.
