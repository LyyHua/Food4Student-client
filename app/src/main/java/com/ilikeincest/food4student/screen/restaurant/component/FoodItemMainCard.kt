package com.ilikeincest.food4student.screen.restaurant.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ilikeincest.food4student.model.FoodItem

@Composable
fun FoodItemMainCard(
    foodItem: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if (!foodItem.foodItemPhotoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = foodItem.foodItemPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .background(colorScheme.surfaceVariant)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Fastfood,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = foodItem.name,
                    style = typography.titleMedium
                )
                Text(
                    text = "${foodItem.basePrice} VND",
                    style = typography.bodyMedium,
                    color = colorScheme.primary
                )
                foodItem.description?.let {
                    Text(
                        text = it,
                        style = typography.bodySmall
                    )
                }
            }
        }
    }
}