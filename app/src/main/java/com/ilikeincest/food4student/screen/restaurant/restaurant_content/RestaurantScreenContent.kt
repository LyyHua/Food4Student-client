package com.ilikeincest.food4student.screen.restaurant.restaurant_content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ilikeincest.food4student.component.ErrorDialog
import com.ilikeincest.food4student.screen.restaurant.RestaurantViewModel
import com.ilikeincest.food4student.screen.restaurant.component.FoodItemMainCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreenContent(
    onNavigateToAddEditFoodItem: () -> Unit,
    viewModel: RestaurantViewModel
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (errorMessage.isNotEmpty()) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { viewModel.dismissErrorDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách món ăn") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.setSelectedFoodItem(null)
                onNavigateToAddEditFoodItem()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm món ăn")
            }
        }
    ) { innerPadding ->
        restaurant?.let { restaurant ->
            val foodItems = restaurant.foodCategories.flatMap { it.foodItems }
            if (foodItems.isNotEmpty()) {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.Companion.fillMaxSize()
                ) {
                    items(foodItems) { foodItem ->
                        FoodItemMainCard(
                            foodItem = foodItem,
                            onClick = {
                                viewModel.setSelectedFoodItem(foodItem)
                                onNavigateToAddEditFoodItem()
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Text("Chưa có món ăn nào. Hãy thêm món mới!")
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Companion.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}