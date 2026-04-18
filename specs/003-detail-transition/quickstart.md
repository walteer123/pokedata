# Quickstart: Shared Element Transitions

**Date**: 2026-04-18
**Feature**: 003-detail-transition

## Prerequisites

- Android device or emulator with API 24+
- The app must be built and running (`./gradlew assembleDebug`)

## Manual Verification Steps

### 1. Build the project
```bash
./gradlew assembleDebug
```
Expected: Build succeeds with no errors.

### 2. Run on device/emulator
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.pokedata/.MainActivity
```

### 3. Test Pokemon List → Detail transition
- Open the app (Pokemon List screen)
- Tap any Pokemon card
- **Expected**: The sprite image on the card smoothly expands/animates to the artwork position on the detail screen. The remaining detail content (stats, types, description) fades in around it. No blank placeholder flashes.
- **Expected**: The animation completes within 500ms.

### 4. Test Detail → List return transition
- Press the back button on the detail screen
- **Expected**: The artwork image smoothly shrinks and returns to the sprite position on the card. The list is in the same scroll position.

### 5. Test Search → Detail transition
- Navigate to Search tab
- Search for a Pokemon and tap a result card
- **Expected**: Same shared element animation as list → detail.

### 6. Test Favorites → Detail transition
- Navigate to Favorites tab
- Tap a favorited Pokemon card
- **Expected**: Same shared element animation as list → detail.

### 7. Test edge cases
- Rapidly tap two different cards in quick succession
- **Expected**: No crashes, no flickering. The second tap is ignored or queued.
- Navigate to detail with slow network (throttle in emulator settings)
- **Expected**: The sprite from the route renders immediately while artwork loads in background.
- Rotate device during transition
- **Expected**: Animation completes or restarts cleanly without visual glitches.

## Success Criteria Verification

| Criterion | How to verify |
|-----------|---------------|
| SC-001: Forward transition < 500ms | Visual observation / screen recording timestamp |
| SC-002: Return transition < 500ms | Visual observation / screen recording timestamp |
| SC-003: Smooth and connected feel | Manual user testing / subjective observation |
| SC-004: No flickering or layout jumps | Visual observation across 20+ navigation events |
| SC-005: Acceptable on mid-range device | Test on API 28 emulator with GPU rendering |
