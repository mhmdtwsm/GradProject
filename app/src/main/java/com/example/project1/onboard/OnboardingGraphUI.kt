package com.example.project1.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingGraphUI(onboardingModel: OnboardingModel) {

    Column(modifier = Modifier.fillMaxSize().background(Color(android.graphics.Color.parseColor("#101F31")))
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {


        Image(
            painter = painterResource(id = onboardingModel.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            alignment = Alignment.Center
        )

        Spacer(
            modifier = Modifier.size(50.dp)
        )

        Text(
            text = onboardingModel.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(15.dp)
        )

        Text(
            text = onboardingModel.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 0.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)
        )

    }


}


@Preview()
@Composable
fun OnboardingGraphUIPreview1(){
    OnboardingGraphUI(onboardingModel = OnboardingModel.FirstPage)
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview2(){
    OnboardingGraphUI(onboardingModel = OnboardingModel.SecondPage)
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPreview3(){
    OnboardingGraphUI(onboardingModel = OnboardingModel.ThirdPages)
}



