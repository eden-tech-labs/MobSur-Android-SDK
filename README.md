# MobSur (Android SDK)
---
## Table of contents
* [Requirements](#requirements) (public)
* [Setup](#setup) (public)
* [Usage](#usage) (public)
* [Building SDK](#building) (private)

## Requirements
- AndroidX
- Kotlin 1.6+

## Setup
- Drop the `.aar` file in `/libs/` directory
- Add in your project level build file as dependency
```groovy
dependencies {
    //..
    implementation files('libs/mobsur.aar')
    //..
}
```
- Sync your gradle file

```occurred null - show```
	
## Usage
- Initialize the SDK (required)

```kotlin
MobSur.setup(applicationContext, appId, userId)
```
- Set your fragment manager (required)

```kotlin
MobSur.setFragmentManager(supportFragmentManager)
```

- Trigger an event

```kotlin
MobSur.event(eventName)
```

- Change user identifier

```kotlin
MobSur.updateUserId(newUserId)
```
	
## Building

- Open Gradle panel (usually top right in Android Studio)
- Select `assemble` option in survey context

![assemble](/screenshots/gradle_assemble.png)
- `survey-debug.aar` and `survey-release.aar` will be assembled in `{project_root}>survey>build>outputs>aar`

![assemble](/screenshots/aar_dir.png)
