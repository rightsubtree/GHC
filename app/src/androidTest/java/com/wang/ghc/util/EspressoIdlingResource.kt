package com.wang.ghc.util

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"
    private val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

    fun register() {
        androidx.test.espresso.IdlingRegistry.getInstance().register(countingIdlingResource)
    }

    fun unregister() {
        androidx.test.espresso.IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    val idlingResource: IdlingResource
        get() = countingIdlingResource
}