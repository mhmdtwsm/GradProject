package com.example.project1.tools.urlanalyzer

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.project1.R
import com.example.project1.home.BottomNavigationBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.project1.ui.theme.customColors
import java.net.InetAddress
import java.net.URL

const val API_KEY = "6aac0414b4328baf05b3b58fcb9a3e8263941f987933b9177d613293f6323070"

interface VirusTotalApi {
    @FormUrlEncoded
    @POST("urls")
    suspend fun submitUrl(
        @Field("url") url: String,
        @Header("x-apikey") apiKey: String = API_KEY
    ): Response<SubmitUrlResponse>

    @GET("urls/{id}")
    suspend fun getUrlReport(
        @Path("id") id: String,
        @Header("x-apikey") apiKey: String = API_KEY
    ): Response<UrlReportResponse>
}

object ApiClient {
    val retrofit: VirusTotalApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.virustotal.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VirusTotalApi::class.java)
    }
}

data class SubmitUrlResponse(val data: DataId)
data class DataId(val id: String)

data class UrlReportResponse(val data: UrlData)
data class UrlData(val attributes: UrlAttributes)

data class UrlAttributes(
    val last_analysis_results: Map<String, AnalysisResult>? = null,
    val last_http_response: HttpResponse? = null,
    val categories: Map<String, String>? = null,
    val url_info: UrlInfo? = null,
    val last_analysis_stats: AnalysisStats? = null,
    val reputation: Int? = null,
    val total_votes: Votes? = null,
    val date: Long? = null,
    val creation_date: Long? = null,
    val popular_threat_classification: PopularThreatClassification? = null,
    val whois: String? = null,
    val content_categories: Map<String, String>? = null,
    val ip_address: String? = null // ✅ تمت الإضافة
)

data class AnalysisResult(val result: String?)
data class HttpResponse(
    val final_url: String? = null,
    val status_code: Int? = null,
    val headers: Map<String, String>? = null,
    val body_length: Int? = null,
    val body_sha256: String? = null
)
data class UrlInfo(
    val title: String? = null,
    val description: String? = null,
    val keywords: List<String>? = null
)
data class AnalysisStats(
    val harmless: Int? = null,
    val malicious: Int? = null,
    val suspicious: Int? = null,
    val undetected: Int? = null,
    val timeout: Int? = null
)
data class Votes(
    val harmless: Int? = null,
    val malicious: Int? = null
)
data class PopularThreatClassification(
    val popularity_ranking: Int? = null,
    val threat_name: String? = null,
    val category: String? = null
)

suspend fun resolveIpFromUrl(url: String): String? {
    return try {
        val host = URL(url).host
        InetAddress.getByName(host).hostAddress
    } catch (e: Exception) {
        null
    }
}

class UrlAnalyzerViewModel : ViewModel() {
    private val _result = mutableStateOf("")
    val result: State<String> = _result

    private val _extraInfo = mutableStateOf<UrlAttributes?>(null)
    val extraInfo: State<UrlAttributes?> = _extraInfo

    fun analyzeUrl(inputUrl: String) {
        viewModelScope.launch {
            try {
                _result.value = "\uD83D\uDCE4 Submitting URL..."
                val submitResponse = ApiClient.retrofit.submitUrl(url = inputUrl)
                if (!submitResponse.isSuccessful) {
                    _result.value = "\u274C Submission failed: ${submitResponse.message()}"
                    return@launch
                }

                val encodedUrl = Base64.encodeToString(
                    inputUrl.toByteArray(),
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
                _result.value = "\u231B Waiting for scan result..."
                delay(15000)

                val reportResponse = ApiClient.retrofit.getUrlReport(encodedUrl)
                if (!reportResponse.isSuccessful) {
                    _result.value = "\u274C Error fetching report: ${reportResponse.message()}"
                    return@launch
                }

                val body = reportResponse.body()
                val attributes = body?.data?.attributes
                val results = attributes?.last_analysis_results
                val maliciousCount = results?.count {
                    val verdict = it.value.result
                    verdict != null && verdict !in listOf("clean", "harmless", "unrated")
                } ?: 0
                val total = results?.size ?: 0

                _result.value = "\uD83D\uDCCA Malicious: $maliciousCount out of $total engines."

                val resolvedIp = resolveIpFromUrl(inputUrl)
                val updatedAttributes = attributes?.copy(ip_address = resolvedIp)

                _extraInfo.value = updatedAttributes

            } catch (e: Exception) {
                _result.value = "\u26A0\uFE0F Error: ${e.message}"
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlAnalyzerScreen(navController: NavController, viewModel: UrlAnalyzerViewModel = viewModel()) {
    var url by remember { mutableStateOf("") }
    val result by viewModel.result
    val extraInfo by viewModel.extraInfo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("URL Analyzer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            UrlInputField(
                url = url,
                onUrlChange = { url = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (url.isNotBlank()) viewModel.analyzeUrl(url) },
                modifier = Modifier.fillMaxWidth(),
                enabled = url.isNotBlank()
            ) {
                Text("Analyze URL")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (result.isNotEmpty() || extraInfo != null) {
                AnalysisResultCard(result = result, extraInfo = extraInfo)
            }
        }
    }
}

@Composable
private fun UrlInputField(url: String, onUrlChange: (String) -> Unit) {
    val context = LocalContext.current
    OutlinedTextField(
        value = url,
        onValueChange = onUrlChange,
        label = { Text("Enter or paste a URL") },
        placeholder = { Text("https://example.com") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.customColors.inputBackground,
            unfocusedContainerColor = MaterialTheme.customColors.inputBackground,
            focusedTextColor = MaterialTheme.customColors.onInputBackground,
            unfocusedTextColor = MaterialTheme.customColors.onInputBackground,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        trailingIcon = {
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.primaryClip?.getItemAt(0)?.text?.toString()?.let {
                    onUrlChange(it)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.clipboard),
                    contentDescription = "Paste URL",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun AnalysisResultCard(result: String, extraInfo: UrlAttributes?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(result, style = MaterialTheme.typography.titleMedium)

            extraInfo?.let { info ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ResultSection(title = "IP Address", value = info.ip_address)
                ResultSection(title = "Reputation", value = info.reputation?.toString())
                ResultSection(
                    title = "Analysis Stats",
                    content = {
                        Column {
                            Text("Harmless: ${info.last_analysis_stats?.harmless ?: 0}")
                            Text("Malicious: ${info.last_analysis_stats?.malicious ?: 0}")
                            Text("Suspicious: ${info.last_analysis_stats?.suspicious ?: 0}")
                        }
                    }
                )
                ResultSection(
                    title = "Community Votes",
                    content = {
                        Column {
                            Text("Harmless: ${info.total_votes?.harmless ?: 0}")
                            Text("Malicious: ${info.total_votes?.malicious ?: 0}")
                        }
                    }
                )
                ResultSection(title = "Final URL", value = info.last_http_response?.final_url)
                ResultSection(title = "Status Code", value = info.last_http_response?.status_code?.toString())
            }
        }
    }
}

@Composable
private fun ResultSection(title: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }
}

@Composable
private fun ResultSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            content()
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}