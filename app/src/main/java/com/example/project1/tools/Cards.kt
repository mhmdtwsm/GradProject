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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.R

@Composable
fun Cards(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp, // Remove any shadow that might look like a border
        backgroundColor = Color(0xFF1C2431), // Ensure background matches parent
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .border(3.dp, Color.Gray, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chat),
                contentDescription = "Password Test",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .padding(vertical = 10.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Password Test",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
            )
        }
    }
}