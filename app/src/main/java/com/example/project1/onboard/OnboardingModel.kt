package com.example.project1.onboard

import androidx.annotation.DrawableRes

import com.example.project1.R

sealed class OnboardingModel(
    @DrawableRes val image: Int,
    val description : String,
    val title : String,) {

    data object FirstPage : OnboardingModel(
        image = R.drawable.ai,
        title = "AI Power",
        description = "Use our AI-powered tools to instantly detect phishing attempts."
    )

    data object SecondPage : OnboardingModel(
        image = R.drawable.tools,
        title = "Tool Set",
        description = "Explore interactive tools"
    )

    data object ThirdPages : OnboardingModel(
        image = R.drawable.lock,
        title = "Security",
        description = "Continuously improve with the latest cybersecurity advancements.\u2028Your safety is our priority, powered by trusted AI."
    )

}