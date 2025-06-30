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
    private var _retrofit: VirusTotalApi? = null

    fun getRetrofit(): VirusTotalApi {
        if (_retrofit == null) {
            _retrofit = Retrofit.Builder()
                .baseUrl("https://www.virustotal.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VirusTotalApi::class.java)
        }
        return _retrofit!!
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
    val ip_address: String? = null
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
                _result.value = "📤 Submitting URL..."
                val submitResponse = ApiClient.getRetrofit().submitUrl(url = inputUrl)
                if (!submitResponse.isSuccessful) {
                    _result.value = "❌ Submission failed: ${submitResponse.message()}"
                    return@launch
                }

                val encodedUrl = Base64.encodeToString(
                    inputUrl.toByteArray(),
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
                _result.value = "⏳ Waiting for scan result..."
                delay(15000)

                val reportResponse = ApiClient.getRetrofit().getUrlReport(encodedUrl)
                if (!reportResponse.isSuccessful) {
                    _result.value = "❌ Error fetching report: ${reportResponse.message()}"
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

                _result.value = "📊 Malicious: $maliciousCount out of $total engines."

                val resolvedIp = resolveIpFromUrl(inputUrl)
                val updatedAttributes = attributes?.copy(ip_address = resolvedIp)

                _extraInfo.value = updatedAttributes

            } catch (e: Exception) {
                _result.value = "⚠️ Error: ${e.message}"
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
                DetailedAnalysisResultCard(result = result, extraInfo = extraInfo)
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
private fun DetailedAnalysisResultCard(result: String, extraInfo: UrlAttributes?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = result,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            extraInfo?.let { info ->
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                info.ip_address?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "📡 IP Address: $it",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.categories?.let { cats ->
                    Text(
                        "📁 Categories:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    cats.forEach { (k, v) ->
                        Text(
                            "$k: $v",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.last_http_response?.let { resp ->
                    Text(
                        "🌐 HTTP Response:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Final URL: ${resp.final_url ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Status Code: ${resp.status_code ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    resp.headers?.forEach { (k, v) ->
                        Text(
                            "$k: $v",
                            color = MaterialTheme.customColors.secondaryText,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Body Length: ${resp.body_length ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Body SHA-256: ${resp.body_sha256 ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.url_info?.let { urlInfo ->
                    Text(
                        "📄 URL Info:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Title: ${urlInfo.title ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Description: ${urlInfo.description ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Keywords: ${urlInfo.keywords?.joinToString() ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.last_analysis_stats?.let { stats ->
                    Text(
                        "📊 Analysis Stats:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Harmless: ${stats.harmless ?: 0}",
                        color = MaterialTheme.customColors.success
                    )
                    Text(
                        "Malicious: ${stats.malicious ?: 0}",
                        color = MaterialTheme.customColors.danger
                    )
                    Text(
                        "Suspicious: ${stats.suspicious ?: 0}",
                        color = MaterialTheme.customColors.warning
                    )
                    Text(
                        "Undetected: ${stats.undetected ?: 0}",
                        color = MaterialTheme.customColors.secondaryText
                    )
                    Text(
                        "Timeout: ${stats.timeout ?: 0}",
                        color = MaterialTheme.customColors.secondaryText
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.reputation?.let {
                    val reputationColor = when {
                        it > 0 -> MaterialTheme.customColors.success
                        it < 0 -> MaterialTheme.customColors.danger
                        else -> MaterialTheme.customColors.secondaryText
                    }
                    Text(
                        "⭐ Reputation: $it",
                        color = reputationColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                info.total_votes?.let { votes ->
                    Text(
                        "👍 Harmless Votes = ${votes.harmless ?: 0}",
                        color = MaterialTheme.customColors.success
                    )
                    Text(
                        "👎 Malicious Votes = ${votes.malicious ?: 0}",
                        color = MaterialTheme.customColors.danger
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.popular_threat_classification?.let { ptc ->
                    Text(
                        "🔥 Threat Classification:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Threat Name: ${ptc.threat_name ?: "N/A"}",
                        color = MaterialTheme.customColors.danger
                    )
                    Text(
                        "Category: ${ptc.category ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Ranking: ${ptc.popularity_ranking ?: "N/A"}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.whois?.let {
                    Text(
                        "📇 Whois Info:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        it,
                        color = MaterialTheme.customColors.secondaryText,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                info.content_categories?.let { cc ->
                    Text(
                        "🗂️ Content Categories:",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    cc.forEach { (k, v) ->
                        Text(
                            "$k: $v",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
