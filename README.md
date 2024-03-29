# MobSur (Android SDK)
---
## Table of contents
* [Requirements](#requirements)
* [Setup](#setup)
* [Usage](#usage)
* [Sample project](#Sample project) (public)

## Requirements
- AndroidX
- Kotlin 1.4+

## Setup

With the MobSur Android SDK, you can integrate surveys into your app with just a few lines of code. Below are the prerequisites:

1. [Download](mobsur.aar) the **mobsur.aar** file.
2. Place it into the **app/libs/** directory of your Android application.
3. Add the file to the dependencies in the **build.gradle** file:

```groovy
dependencies {
    //..
    implementation files('libs/mobsur.aar')
    //..
}
```

4. Add the dependencies required for Retrofit2 in the build.gradle file:

```groovy
dependencies {

//..
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
//..
}
```

5. Synchronize the gradle file.

	
## Usage
1. Import the package into the file where it is going to be used:


```kotlin
import io.edentechlabs.survey.sdk.MobSur
```

2. Initialize the SDK and set the fragment manager:

The `debug` parameter is optional, but if it's set to `true`, you will see debug info in the logs. 

```kotlin
//appId can be taken from https://app.mobsur.com/user/apps
MobSur.setup(applicationContext, "appId", "userId", dabug = false)
MobSur.setFragmentManager(supportFragmentManager)
```

3. Trigger an event. For example, to start a servey on button click, you can use the following piece of code:

```kotlin
val button = findViewById(R.id.button) as Button
        button.setOnClickListener {
            MobSur.event("myevent")
       }
```

4. If you need to change the userId, use the **updateUserId** method:

```kotlin
MobSur.updateUserId("newUserId")
```


## Sample project

A complete project with the SDK can be found in our [GitHub repository](https://github.com/eden-tech-labs/MobSur-Android-App).

