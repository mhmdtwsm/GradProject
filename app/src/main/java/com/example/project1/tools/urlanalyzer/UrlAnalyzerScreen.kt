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

@Composable
fun UrlAnalyzerScreen(navController: NavController, viewModel: UrlAnalyzerViewModel = viewModel()) {
    var url by remember { mutableStateOf("") }
    val result by viewModel.result
    val extraInfo by viewModel.extraInfo

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                selectedScreen = "UrlAnalyzer"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101F31))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.weight(0.69f))
                Text("URL Analyzer", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.Gray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(60.dp))

            val context = LocalContext.current

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Enter URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.clipboard),
                        contentDescription = "Paste URL",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                if (text.isNotBlank()) {
                                    url = text
                                }
                            }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.analyzeUrl(url) }) {
                Text("Scan")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (result.isNotEmpty() || extraInfo != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E2A3A),
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(result, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                        extraInfo?.let { info ->

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.Gray)

                            info.ip_address?.let {
                                Text("\uD83D\uDCE1 IP Address: $it", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.categories?.let { cats ->
                                Text("\uD83D\uDCC1 Categories:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                cats.forEach { (k, v) -> Text("$k: $v", color = Color.White) }
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.last_http_response?.let { resp ->
                                Text("\uD83C\uDF10 HTTP Response:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                Text("Final URL: ${resp.final_url ?: "N/A"}", color = Color.White)
                                Text("Status Code: ${resp.status_code ?: "N/A"}", color = Color.White)
                                resp.headers?.forEach { (k, v) -> Text("$k: $v", color = Color.White, fontSize = 12.sp) }
                                Text("Body Length: ${resp.body_length ?: "N/A"}", color = Color.White)
                                Text("Body SHA-256: ${resp.body_sha256 ?: "N/A"}", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.url_info?.let { urlInfo ->
                                Text("\uD83D\uDCC4 URL Info:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                Text("Title: ${urlInfo.title ?: "N/A"}", color = Color.White)
                                Text("Description: ${urlInfo.description ?: "N/A"}", color = Color.White)
                                Text("Keywords: ${urlInfo.keywords?.joinToString() ?: "N/A"}", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.last_analysis_stats?.let { stats ->
                                Text("\uD83D\uDCCA Analysis Stats:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                Text("Harmless: ${stats.harmless ?: 0}", color = Color.White)
                                Text("Malicious: ${stats.malicious ?: 0}", color = Color.White)
                                Text("Suspicious: ${stats.suspicious ?: 0}", color = Color.White)
                                Text("Undetected: ${stats.undetected ?: 0}", color = Color.White)
                                Text("Timeout: ${stats.timeout ?: 0}", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.reputation?.let {
                                Text("\u2B50 Reputation: $it", color = Color.White)
                            }

                            info.total_votes?.let { votes ->
                                Text("\uD83D\uDC4D Harmless Votes = ${votes.harmless ?: 0}", color = Color.White)
                                Text("\uD83D\uDC4E Malicious Votes = ${votes.malicious ?: 0}", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.popular_threat_classification?.let { ptc ->
                                Text("\uD83D\uDD25 Threat Classification:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                Text("Threat Name: ${ptc.threat_name ?: "N/A"}", color = Color.White)
                                Text("Category: ${ptc.category ?: "N/A"}", color = Color.White)
                                Text("Ranking: ${ptc.popularity_ranking ?: "N/A"}", color = Color.White)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.whois?.let {
                                Text("\uD83D\uDCC7 Whois Info:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                Text(it, color = Color.White, fontSize = 12.sp)
                                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
                            }

                            info.content_categories?.let { cc ->
                                Text("\uD83D\uDDC2 Content Categories:", fontWeight = FontWeight.SemiBold, color = Color.Cyan)
                                cc.forEach { (k, v) -> Text("$k: $v", color = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }
}
