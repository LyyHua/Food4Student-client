package com.ilikeincest.food4student.screens.app

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilikeincest.food4student.R
import com.ilikeincest.food4student.model.User
import com.ilikeincest.food4student.ui.theme.Food4StudentTheme
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AccountCenterViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = User())
    val provider = user.provider.replaceFirstChar { it.titlecase(Locale.getDefault()) }
    val context = LocalContext.current

    Scaffold {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(title = { Text(stringResource(R.string.account_center)) })

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            DisplayNameCard(user.displayName) { viewModel.onUpdateDisplayNameClick(it) }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            Card(modifier = Modifier.card()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    if (!user.isAnonymous) {
                        Text(
                            text = String.format(
                                stringResource(R.string.profile_email),
                                user.email
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = String.format(stringResource(R.string.profile_uid), user.id),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = String.format(stringResource(R.string.profile_provider), provider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            if (user.isAnonymous) {
                AccountCenterCard(
                    stringResource(R.string.sign_in),
                    Icons.Filled.Face,
                    Modifier.card()
                ) {
                    viewModel.onSignInClick(navController)
                }

                AccountCenterCard(
                    stringResource(R.string.sign_up),
                    Icons.Filled.AccountCircle,
                    Modifier.card()
                ) {
                    viewModel.onSignUpClick(navController)
                }
            } else {
                ExitAppCard(navController, context) { navController, context -> viewModel.onSignOutClick(navController, context) }
                RemoveAccountCard(navController, context) { navController, context -> viewModel.onDeleteAccountClick(navController, context) }
            }
        }
    }
}