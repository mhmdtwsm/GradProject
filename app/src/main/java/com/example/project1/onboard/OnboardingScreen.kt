package com.example.project1.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.project1.DataStoreManager
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.LocalContext

private enum class OnboardingButtonType { PRIMARY, SECONDARY }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingModel.FirstPage, OnboardingModel.SecondPage, OnboardingModel.ThirdPages
    )
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Get Started")
                else -> listOf("", "")
            }
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (buttonState.value[0].isNotEmpty()) {
                            OnboardingButton(
                                text = buttonState.value[0],
                                type = OnboardingButtonType.SECONDARY
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                        }
                    }

                    // Indicator
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)

                    // Next/Start Button
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        OnboardingButton(
                            text = buttonState.value[1],
                            type = OnboardingButtonType.PRIMARY
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
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            HorizontalPager(state = pagerState) { index ->
                OnboardingPageContent(onboardingModel = pages[index])
            }
        }
    }
}

@Composable
fun OnboardingPageContent(onboardingModel: OnboardingModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = onboardingModel.image),
            contentDescription = onboardingModel.title,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = onboardingModel.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = onboardingModel.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun IndicatorUI(pageSize: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageSize) { pageIndex ->
            val isSelected = pageIndex == currentPage
            Box(
                modifier = Modifier
                    .height(10.dp)
                    .width(if (isSelected) 24.dp else 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
private fun OnboardingButton(
    text: String,
    type: OnboardingButtonType,
    onClick: () -> Unit
) {
    val buttonColors = when (type) {
        OnboardingButtonType.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        OnboardingButtonType.SECONDARY -> ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val buttonElevation = if (type == OnboardingButtonType.PRIMARY) {
        ButtonDefaults.buttonElevation()
    } else {
        null
    }

    Button(
        onClick = onClick,
        colors = buttonColors,
        elevation = buttonElevation,
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    Project1Theme {
        OnboardingScreen {}
    }
}