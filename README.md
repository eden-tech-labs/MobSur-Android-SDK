# MobSur Android SDK

Show MobSur surveys in your Android app when your users do something that matters.

**Version 1.4.0** · minSdk 21 · [Download `mobsur.aar`](mobsur.aar)

## Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [When a survey is shown](#when-a-survey-is-shown)
- [Migrating from 1.3](#migrating-from-13)
- [Local testing](#local-testing)
- [Data stored on the device](#data-stored-on-the-device)
- [Troubleshooting](#troubleshooting)

## Requirements

- AndroidX, `minSdk` 21 or higher, `compileSdk` 34 or higher.
- Activities that extend `FragmentActivity` (for example `AppCompatActivity` or Flutter's
  `FlutterFragmentActivity`). The survey is shown in a bottom sheet through the activity's
  `supportFragmentManager`.
- Kotlin 2.0 or newer, or Java. Java-only apps add the Kotlin standard library (see below).

## Installation

1. Download [`mobsur.aar`](mobsur.aar) and put it in your app module's `libs/` folder (for example
   `app/libs/mobsur.aar`).
2. Add the `.aar` and its dependencies to the app module's build file. A downloaded `.aar` does not bring its
   dependencies with it, so they have to be listed here:

   ```groovy
   // app/build.gradle
   dependencies {
       implementation files('libs/mobsur.aar')

       // Bottom sheet UI. Also brings androidx.fragment, appcompat and coordinatorlayout.
       implementation 'com.google.android.material:material:1.12.0'

       // Java-only apps. Kotlin apps already have it.
       // implementation 'org.jetbrains.kotlin:kotlin-stdlib:2.1.21'
   }
   ```

   ```kotlin
   // app/build.gradle.kts
   dependencies {
       implementation(files("libs/mobsur.aar"))
       implementation("com.google.android.material:material:1.12.0")
       // implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.21") // Java-only apps
   }
   ```

   Newer versions of these libraries work too. 1.4.0 no longer needs Retrofit or Gson.
3. Sync Gradle.

The SDK declares the `INTERNET` permission itself. Its R8/ProGuard rules are bundled in the `.aar`, so a
minified release build needs no extra rules.

## Usage

### 1. Set up the SDK once per launch

Call `setup` in `Application.onCreate`:

```kotlin
import io.edentechlabs.survey.sdk.MobSur

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // App ID: https://app.mobsur.com/user/apps
        MobSur.setup(this, "YOUR-APP-ID", userId = currentUserIdOrNull(), debug = BuildConfig.DEBUG)
    }
}
```

- `userId` is a stable identifier for the person using the app, at most 255 characters. Use your own
  account ID when you have one. Pass `null` if you don't: the SDK then generates a random ID once, stores it
  and reuses it on every launch.
- Never pass a value that changes on every launch, such as a new random ID. Survey sampling, "already
  answered" and cooldowns are all keyed on this ID, and every new ID counts as a new monthly tracked user
  on your plan.
- `debug = true` logs what the SDK does to Logcat under the tag `MobSur`. Keep it off in release builds.

### 2. Give the SDK the current activity's FragmentManager

In every activity that can show a survey:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    MobSur.setFragmentManager(supportFragmentManager)
}
```

The reference is held weakly, so it does not leak the activity.

### 3. Send events

```kotlin
MobSur.event("purchase_completed")
```

Use the same event name as the **App event** of the survey in the dashboard. Names are matched exactly,
including case. `event` returns immediately, and a network request never delays it.

### 4. Change the user

When someone signs in or out:

```kotlin
MobSur.updateUserId("new-user-id")   // or null to go back to the SDK-generated ID
```

When the ID actually changes, the previous person's survey list, event counts and completed surveys are
discarded, and surveys are fetched for the new person.

### From Java

```java
MobSur.INSTANCE.setup(context, "YOUR-APP-ID", "user-id", false);
MobSur.INSTANCE.setFragmentManager(getSupportFragmentManager());
MobSur.INSTANCE.event("purchase_completed");
```

No SDK method throws. Calls made before `setup` are ignored.

## When a survey is shown

A survey is launched in the dashboard with an **App event**, a **Delay after event**, a **From occurrence**
and an optional **Through occurrence**.

- **Counting.** The SDK counts each event on the device, separately for every running survey that uses it.
  Counts are kept across app launches and reset when the user ID changes.
- **Occurrence window.** The survey is shown on an occurrence between **From occurrence** and **Through
  occurrence**, both included. An empty **Through occurrence** means no upper limit.

  | From | Through | Shown on occurrence |
  |------|---------|---------------------|
  | 1    | (empty) | 1, 2, 3, … until the person answers it |
  | 3    | 3       | only the 3rd |
  | 2    | 4       | 2, 3 and 4 |

- **Delay.** Once the window matches, the survey opens after **Delay after event** (0–5 seconds). If the app
  has gone to the background or the activity is gone by then, it is not shown, and a later event can try
  again.
- **Priority.** When several surveys use the same event, they are checked in the dashboard's priority
  order. The first survey whose window matches is shown; the surveys after it are not counted for that
  event.
- **One survey per app session.** At most one survey is shown per app launch (process). While a survey is
  waiting for its delay or on screen, events are ignored and not counted.
- **Finishing.** When the person completes the survey, the sheet closes by itself (or after they tap
  **Finish** on the thank-you page) and that survey is not shown to them again.
- **Closing early.** Closing the sheet before finishing does not complete the survey. It can be shown again
  in a later session if its occurrence window still matches. Use **Through occurrence** to limit how often
  that can happen.
- **Start and end dates** from the dashboard are respected on the device.
- **Updates.** Surveys are fetched at launch, when the user ID changes, and again during use when the last
  fetch was more than 6 hours ago. Without a network connection, the last list is kept.

## Migrating from 1.3

1. Replace `libs/mobsur.aar` with the new file.
2. Update the dependencies (see [Installation](#installation)): add `material`, and remove
   `com.squareup.retrofit2:retrofit` and `converter-gson` if nothing else in your app uses them.
3. Your existing calls keep compiling:
   `MobSur.setup(context, appId, userId, debug)`, `MobSur.updateUserId(userId)`,
   `MobSur.setFragmentManager(fm)` and `MobSur.event(name)`.
4. If you generated a new random user ID on every launch (as the old sample app did), switch to a stable
   ID, or pass `null` and let the SDK keep one.

What changes for your users:

- The SDK no longer crashes the app when `event` or `updateUserId` is called before `setup`, or when a survey
  matches before `setFragmentManager` was called. These calls are now ignored.
- **From occurrence** is respected (1.3 ignored it), and **Through occurrence** includes the last occurrence
  (1.3 only stopped at exactly that number). Counts are kept across launches (1.3 reset them on every launch).
- Surveys follow the dashboard priority order (1.3 could pick any order).
- Surveys without a start and end date are shown (1.3 never showed them).
- `updateUserId` fetches the new person's surveys right away and resets the event counts.
- A survey that was completed is not shown again from an old cached list.
- The survey sheet survives screen rotation.
- Data stored by 1.3 is discarded on the first launch with 1.4, so event counts start from zero.
- If your app overrode any of the SDK's resources, note that they were renamed with a `mobsur_` prefix
  (`bottom_dialog` → `mobsur_sheet`, `rounded_corners` → `mobsur_rounded_corners`, …). The
  `MobSurBottomSheetTheme` style keeps its name.

New in the API: `userId` may be `null` (SDK-generated ID), an optional `baseUrl` for local testing, and
`MobSur.VERSION`.

## Local testing

To point the SDK at a local MobSur backend instead of `https://api.mobsur.com/`:

```kotlin
MobSur.setup(this, "YOUR-APP-ID", userId = "test-user", debug = true, baseUrl = "http://10.0.2.2:8000/")
```

Android blocks plain HTTP by default. Allow it for the local host in a debug-only
[network security configuration](https://developer.android.com/privacy-and-security/security-config).
Don't ship a release build with `baseUrl` set.

## Data stored on the device

The SDK keeps its data in its own SharedPreferences file, `MobSur`: the current survey list, the event
counts, the IDs of completed surveys, the generated user ID (only when you don't pass one), and a hash of
the app ID and user ID used to detect when the user changes. It sends the app ID, user ID, your app's
`versionName`, the platform (`android`), the SDK version and the device languages (`Accept-Language`) to
`api.mobsur.com`.

## Troubleshooting

Turn on `debug = true` and filter Logcat by the tag `MobSur` (`adb logcat -s MobSur`). The log shows each
fetch, how many surveys arrived, and for each event whether a survey matched and why it was or was not
shown.

- **`no eligible survey`**: the event name differs from the dashboard, the survey has not started or has
  ended, the occurrence window does not match yet, or this person already completed the survey.
- **`no FragmentManager; call MobSur.setFragmentManager`**: call `setFragmentManager` in the activity's
  `onCreate`.
- **`ignored: a survey is shown in this session`**: only one survey is shown per app launch.
- **`HTTP 403`**: the account has reached its monthly tracked users limit.
- **`Null extracted folder for artifact`** (Gradle): check that the file is at `app/libs/mobsur.aar` and
  that the name in the dependency matches.

## Sample project

A complete project with the SDK is in our
[GitHub repository](https://github.com/eden-tech-labs/MobSur-Android-App).
