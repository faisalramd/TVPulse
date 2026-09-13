package com.bookcabin.tvpulse.features.navigation

/**
 * Deep link entry points into the app. Keep these in sync with the intent filters
 * declared for MainActivity in the app manifest.
 */
object DeepLinks {
    const val SCHEME = "tvpulse"

    const val HOST_HOME = "home"
    const val HOST_FAVORITE = "favorite"
    const val HOST_DETAIL = "detail"

    const val HOME = "$SCHEME://$HOST_HOME"
    const val FAVORITE = "$SCHEME://$HOST_FAVORITE"

    const val DETAIL = "$SCHEME://$HOST_DETAIL"
}
