# Quickstart: Using Hinge-Aware Layout

## Adding the Dependency
Ensure `:core:ui` includes the WindowManager dependency:
```kotlin
implementation(libs.androidx.window.core) // maps to androidx.window:window:1.3.0
```

## Using the Utility
A reusable composable `rememberHingeWidth()` is provided in `:core:ui`. Use it to conditionally render layouts based on the presence of a vertical hinge:

```kotlin
@Composable
fun MyListScreen() {
    val hingeWidth = rememberHingeWidth()
    val hasVerticalHinge = hingeWidth > 0.dp

    if (hasVerticalHinge) {
        // 2 columns with hinge gap in the middle
        LazyColumn {
            items(count = (itemCount + 1) / 2) { rowIndex ->
                val leftIndex = rowIndex * 2
                val rightIndex = leftIndex + 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(hingeWidth)
                ) {
                    if (leftIndex < itemCount) ItemCard(leftIndex).weight(1f)
                    if (rightIndex < itemCount) ItemCard(rightIndex).weight(1f)
                }
            }
        }
    } else {
        // 3-column grid when no hinge
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(count = itemCount) { index ->
                ItemCard(index)
            }
        }
    }
}
```

## What It Does
- **On a non-foldable device**: `rememberHingeWidth()` returns `0.dp` → 3-column grid layout.
- **On a foldable device with vertical hinge**: Returns the hinge width in Dp → 2-column layout with gap equal to the physical hinge width.
- **On a foldable device with horizontal hinge**: Returns `0.dp` (horizontal hinges don't affect vertical scrolling) → 3-column grid layout.
- Automatically updates when the device is folded/unfolded.

## Testing
Use the Android Emulator with a foldable device profile (e.g., "7.6in Fold-in with outer display") and toggle the fold posture to verify that:
1. With vertical hinge: 2 columns with gap in the middle, no items overlap the hinge
2. Without hinge: 3-column grid with standard spacing
