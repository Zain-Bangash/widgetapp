# Warith App Features

This file lists all the currently implemented features of the Warith app for testing and verification purposes.

## 1. Home Screen Widget (Glance)
- **Source Display**: Shows one Islamic text source at a time (e.g., Al-Kafi).
- **Narration Text**: Displays the text of a narration or page.
- **Narrator Display**: Shows the narrator (if available) with an em-dash (—).
- **Reference Link**: Displays the reference string. Tapping it opens the source link in the browser.
- **Source Navigation**: Left and right arrows to cycle through all loaded JSON books.
- **Refresh Button**: Circular arrow icon in the top-right to pick a new random entry from the same source.
- **Dynamic Sizing**: Resizable widget (default 4x3) that fits text gracefully.
- **System Theme Support**: Background and text colors adapt to your system's Light or Dark mode.

## 2. Source Management
- **Auto-Discovery**: New books can be added by simply dropping a JSON file into `assets/sources/` without code changes.
- **Offline First**: All data is bundled within the app; no internet required.

## 3. Rotation Logic
- **Random Unseen**: Picking a random entry that hasn't been shown yet for that source.
- **Cycle Reset**: Once all entries in a source are seen, the history for that source resets automatically.
- **Daily Auto-Rotation**: Optional feature to advance to the next narration for every source at midnight.

## 4. Main App (MainActivity)
- **Source Listing**: Shows all loaded books and their entry counts.
- **Daily Rotation Toggle**: Switch to enable/disable the midnight update worker.
- **Manual History Reset**: Button to clear all "already seen" records for all sources.
- **Widget Pinning**: Button to easily add the widget to your home screen (on supported launchers).

## 5. Deployment & CI/CD
- **GitHub Actions**: Automated builds to generate APKs and AABs.
- **Release Signing**: Configurable signing for Play Store publishing.
- **Documentation**: Detailed guides for both users and developers.
