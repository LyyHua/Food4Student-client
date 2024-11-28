package com.ilikeincest.food4student.admin.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ilikeincest.food4student.admin.viewmodel.AdminUserViewModel
import com.ilikeincest.food4student.model.User

@Composable
fun AdminUserActionDialog(
    user: User,
    onDismiss: () -> Unit,
    viewModel: AdminUserViewModel
) {
    val context = LocalContext.current
    val currentUserRole = viewModel.currentUserRole

    Log.d("AdminUserActionDialog", "Current user role: $currentUserRole")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("User Actions") },
        text = {
            Column {
                Text(
                    "Select an action for ${user.displayName ?: "this user"}.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                // User Management Actions
                Text(
                    "User Management",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyColumn {
                    when (user.role) {
                        "Banned" -> {
                            item {
                                ActionListItem(
                                    text = if (user.ownedRestaurant) "Unban as Restaurant Owner" else "Unban as User",
                                    icon = if (user.ownedRestaurant) Icons.Default.Restaurant else Icons.Default.Person,
                                    onClick = {
                                        if (user.ownedRestaurant) {
                                            viewModel.unbanRestaurantOwner(user.id)
                                            Toast.makeText(
                                                context,
                                                "Restaurant Owner unbanned",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            viewModel.unbanUser(user.id)
                                            Toast.makeText(
                                                context,
                                                "User unbanned",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        onDismiss()
                                    }
                                )
                            }
                        }
                        "User", "RestaurantOwner" -> {
                            item {
                                ActionListItem(
                                    text = "Ban User",
                                    icon = Icons.Default.Block,
                                    onClick = {
                                        viewModel.banUser(user.id)
                                        onDismiss()
                                        Toast.makeText(
                                            context,
                                            "User banned",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                        "Moderator" -> {
                            if (currentUserRole == "Admin") {
                                item {
                                    ActionListItem(
                                        text = "Revoke Moderator Role",
                                        icon = Icons.Default.PersonRemove,
                                        onClick = {
                                            viewModel.revokeModeratorRole(user.id)
                                            onDismiss()
                                            Toast.makeText(
                                                context,
                                                "Moderator role revoked",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (user.role != "Admin" && user.role != "Moderator") {
                        item {
                            ActionListItem(
                                text = "Give Moderator Role",
                                icon = Icons.Default.AddModerator,
                                onClick = {
                                    viewModel.giveModeratorRole(user.id)
                                    onDismiss()
                                    Toast.makeText(
                                        context,
                                        "User promoted to Moderator",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }

                HorizontalDivider()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}