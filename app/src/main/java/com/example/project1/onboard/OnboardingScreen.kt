package com.example.project1.onboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.DataStoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {

    val pages = listOf(
        OnboardingModel.FirstPage, OnboardingModel.SecondPage, OnboardingModel.ThirdPages
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Start")
                else -> listOf("", "")
            }
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B)) // Replace with your desired color
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 70.dp)
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (buttonState.value[0].isNotEmpty()) {
                        ButtonUi(
                            text = buttonState.value[0],
                            backgroundColor = Color.Transparent,
                            textColor = Color.Gray
                        ) {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    ButtonUi(
                        text = buttonState.value[1],
                        backgroundColor = Color.Gray,
                        textColor = Color.White
                    ) {
                        scope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                DataStoreManager.saveOnboardingStatus(context, true)
                                onFinished()
                            }
                        }
                    }
                }

            }
        }
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                OnboardingGraphUI(onboardingModel = pages[index])
            }
        }
    })


}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen {

    }
}