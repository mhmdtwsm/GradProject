package com.example.passwordmanager.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.passwordmanager.model.VaultIcon
import com.example.project1.R

@Composable
fun VaultIcon(
    icon: VaultIcon,
    tint: Color = Color.Black,
    modifier: Modifier = Modifier.size(24.dp)
) {
    val imageVector = when (icon) {
        VaultIcon.WORK -> ImageVector.vectorResource(R.drawable.work)
        VaultIcon.SOCIAL -> SocialIcon()
        VaultIcon.WARNING -> ImageVector.vectorResource(R.drawable.important)
        VaultIcon.DOTS -> ImageVector.vectorResource(R.drawable.dots)
    }

    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun SocialIcon(): ImageVector {
    // Since there's no built-in @ icon in Material Icons, we'll use Work as a placeholder
    // In a real app, you'd use a custom vector asset or a library like Compose Icons Extended
    return ImageVector.vectorResource(R.drawable.social)
}
