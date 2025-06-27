package com.example.project1.home.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

/**
 * Network service class for the Community API
 */
class CommunityNetworkService {
    companion object {
        private const val TAG = "CommunityNetworkService"
        private const val BASE_URL = "http://phishaware.runasp.net/api/Community"
        private const val API_GET_POSTS = "$BASE_URL/PostsWithCommentsAndLikes"
        private const val API_CREATE_POST = "$BASE_URL/Post"
        private const val API_CREATE_COMMENT = "$BASE_URL/Comment"
        private const val API_LIKE_POST = "$BASE_URL/Like"
        private const val API_USER_POSTS = "$BASE_URL/UserPosts"
        private const val API_DELETE_POST = "$BASE_URL/Post"
        private const val API_UPDATE_POST = "$BASE_URL/Post"
        private const val API_USER_ME = "http://phishaware.runasp.net/api/user/me"
    }

    /**
     * Convert bitmap to base64 string for API upload
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Fetch all posts with comments and likes
     */
    suspend fun getAllPosts(authToken: String): List<PostDto> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            Log.d(TAG, "Fetching all posts from API...")

            val url = URL(API_GET_POSTS)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $authToken")
            connection.connectTimeout = 20000
            connection.readTimeout = 20000

            val responseCode = connection.responseCode
            Log.d(TAG, "Get posts response code: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: $responseCode")
                throw Exception("Server returned error code: $responseCode")
            }

            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                response.toString()
            }

            Log.d(TAG, "Posts API Response: $response")
            return@withContext parsePostsResponse(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching posts: ${e.message}", e)
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Create a new post
     */
    suspend fun createPost(authToken: String, content: String, image: String = ""): Boolean =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                Log.d(TAG, "Creating new post with content: $content")

                val url = URL(API_CREATE_POST)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $authToken")
                connection.doOutput = true
                connection.connectTimeout = 20000
                connection.readTimeout = 20000

                val jsonPayload = JSONObject().apply {
                    put("content", content)
                    put("image", image)
                }

                Log.d(TAG, "Post creation payload: $jsonPayload")

                connection.outputStream.use { os ->
                    val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                Log.d(TAG, "Create post response code: $responseCode")

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                Log.d(TAG, "Post created successfully")
                return@withContext true

            } catch (e: Exception) {
                Log.e(TAG, "Error creating post: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }

    /**
     * Like a post
     */
    suspend fun likePost(authToken: String, postId: Int): Boolean = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            Log.d(TAG, "Liking post with ID: $postId")

            val url = URL(API_LIKE_POST)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $authToken")
            connection.doOutput = true
            connection.connectTimeout = 20000
            connection.readTimeout = 20000

            val jsonPayload = JSONObject().apply {
                put("postId", postId)
            }

            Log.d(TAG, "Like post payload: $jsonPayload")

            connection.outputStream.use { os ->
                val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "Like post response code: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: $responseCode")
                throw Exception("Server returned error code: $responseCode")
            }

            Log.d(TAG, "Post liked successfully")
            return@withContext true

        } catch (e: Exception) {
            Log.e(TAG, "Error liking post: ${e.message}", e)
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Create a comment on a post
     */
    suspend fun createComment(authToken: String, postId: Int, content: String, parentCommentId: Int = 0): Boolean =
        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null

            try {
                Log.d(TAG, "Creating comment on post $postId with content: $content")

                val url = URL(API_CREATE_COMMENT)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $authToken")
                connection.doOutput = true
                connection.connectTimeout = 20000
                connection.readTimeout = 20000

                val jsonPayload = JSONObject().apply {
                    put("postId", postId)
                    put("content", content)
                    put("parentCommentId", parentCommentId)
                }

                Log.d(TAG, "Create comment payload: $jsonPayload")

                connection.outputStream.use { os ->
                    val input = jsonPayload.toString().toByteArray(StandardCharsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                Log.d(TAG, "Create comment response code: $responseCode")

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "HTTP error code: $responseCode")
                    throw Exception("Server returned error code: $responseCode")
                }

                Log.d(TAG, "Comment created successfully")
                return@withContext true

            } catch (e: Exception) {
                Log.e(TAG, "Error creating comment: ${e.message}", e)
                throw e
            } finally {
                connection?.disconnect()
            }
        }

    /**
     * Get posts by specific user
     */
    suspend fun getUserPosts(authToken: String, userId: String): List<PostDto> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            Log.d(TAG, "Fetching posts for user: $userId")

            val url = URL("$API_USER_POSTS/$userId")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $authToken")
            connection.connectTimeout = 20000
            connection.readTimeout = 20000

            val responseCode = connection.responseCode
            Log.d(TAG, "Get user posts response code: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: $responseCode")
                throw Exception("Server returned error code: $responseCode")
            }

            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                response.toString()
            }

            Log.d(TAG, "User posts API Response: $response")
            return@withContext parsePostsResponse(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user posts: ${e.message}", e)
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Parse the JSON response into PostDto objects
     */
    private fun parsePostsResponse(response: String): List<PostDto> {
        try {
            val posts = mutableListOf<PostDto>()
            val jsonArray = JSONArray(response)

            Log.d(TAG, "Parsing ${jsonArray.length()} posts from response")

            for (i in 0 until jsonArray.length()) {
                val postJson = jsonArray.getJSONObject(i)

                // Parse comments
                val commentsArray = postJson.getJSONArray("comments")
                val comments = mutableListOf<CommentDto>()

                for (j in 0 until commentsArray.length()) {
                    val commentJson = commentsArray.getJSONObject(j)
                    comments.add(
                        CommentDto(
                            id = commentJson.getInt("id"),
                            content = commentJson.getString("content"),
                            createdAt = commentJson.getString("createdAt"),
                            userId = commentJson.getString("userId"),
                            likeCount = commentJson.getInt("likeCount")
                        )
                    )
                }

                // Create post object
                val post = PostDto(
                    id = postJson.getInt("id"),
                    content = postJson.getString("content"),
                    createdAt = postJson.getString("createdAt"),
                    userId = postJson.getString("userId"),
                    image = postJson.optString("image", ""),
                    likeCount = postJson.getInt("likeCount"),
                    comments = comments
                )

                posts.add(post)
                Log.d(TAG, "Parsed post: ${post.id} with ${comments.size} comments")
            }

            Log.d(TAG, "Successfully parsed ${posts.size} posts")
            return posts

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing posts response: ${e.message}", e)
            throw Exception("Failed to parse server response: ${e.message}")
        }
    }

    /**
     * Get current user information
     */
    suspend fun getUserInfo(authToken: String): UserInfoDto = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            Log.d(TAG, "Fetching user info from API...")

            val url = URL(API_USER_ME)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $authToken")
            connection.connectTimeout = 20000
            connection.readTimeout = 20000

            val responseCode = connection.responseCode
            Log.d(TAG, "Get user info response code: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error code: $responseCode")
                throw Exception("Server returned error code: $responseCode")
            }

            val response = BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                response.toString()
            }

            Log.d(TAG, "User info API Response: $response")

            // Parse JSON response
            val jsonObject = JSONObject(response)
            val userInfo = UserInfoDto(
                id = jsonObject.getString("id"),
                userName = jsonObject.getString("userName"),
                email = jsonObject.getString("email")
            )

            Log.d(TAG, "Parsed user info: $userInfo")
            return@withContext userInfo

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user info: ${e.message}", e)
            throw e
        } finally {
            connection?.disconnect()
        }
    }
}

// Data Models
data class PostDto(
    val id: Int,
    val content: String,
    val createdAt: String,
    val userId: String,
    val image: String,
    val likeCount: Int,
    val comments: List<CommentDto>
)

data class CommentDto(
    val id: Int,
    val content: String,
    val createdAt: String,
    val userId: String,
    val likeCount: Int
)

data class UserInfoDto(
    val id: String,
    val userName: String,
    val email: String
)

// Request models
data class CreatePostRequest(
    val content: String,
    val image: String = ""
)

data class CreateCommentRequest(
    val postId: Int,
    val content: String,
    val parentCommentId: Int = 0
)

data class LikePostRequest(
    val postId: Int
)
