package com.example.project1.chat

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: Float = 16f
) {
    val context = LocalContext.current

    // Create and remember a simpler Markwon instance without syntax highlighting
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(HtmlPlugin.create())
            .build()
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                this.textSize = fontSize
                if (color != Color.Unspecified) {
                    setTextColor(color.toArgb())
                }
            }
        },
        update = { textView ->
            // Apply markdown to TextView
            markwon.setMarkdown(textView, markdown)
        }
    )
}
