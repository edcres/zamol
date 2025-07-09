package com.example.zamol

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// To set up hilt
//  - Tells Hilt to start managing dependencies from the application level

@HiltAndroidApp
class ZamolApp : Application()
