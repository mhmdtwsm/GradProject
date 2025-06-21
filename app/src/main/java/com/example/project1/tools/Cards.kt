package com.example.project1.tools

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project1.R

@Composable
fun Cards(iconId: Int, title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        backgroundColor = Color(0xFF2E3B4E), // لون فاتح يتناسب مع الخلفية الداكنة
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp)
            .clickable(onClick = onClick)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()

                .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "$title",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .padding(vertical = 12.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$title",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
fun CardsPreview() {
    ToolsMenu(navController = NavController(LocalContext.current))
}