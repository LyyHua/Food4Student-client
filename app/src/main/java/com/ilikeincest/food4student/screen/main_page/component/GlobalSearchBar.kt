package com.ilikeincest.food4student.screen.main_page.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilikeincest.food4student.DEBOUNCE_DELAY
import com.ilikeincest.food4student.component.AsyncImageOrMonogram
import com.ilikeincest.food4student.component.preview_helper.ComponentPreview
import kotlinx.coroutines.delay

/**
 * A global search bar to be used in main screens
 * Look at MapSearchBar and MapScreen for example usage
 *
 * Needs to be wrapped inside a Box with fillMaxSize for expanded search bar to work properly
 * Other content might need to be offset by the height of the search bar (76.dp)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchBar(
    userName: String,
    userAvatarUrl: String,
    modifier: Modifier = Modifier,
    onAvatarClicked: () -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {},
    isVisible: Boolean = true,
) {
    if (isVisible) return
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // TODO: Implement search
    val onSearch: (String) -> Unit = {}

    // query search debounce
    LaunchedEffect(query) {
        if (query.isBlank()) return@LaunchedEffect
        delay(DEBOUNCE_DELAY)
        onSearch(query)
    }

    LaunchedEffect(expanded) {
        onExpandedChange(expanded)
    }

    val searchBarPadding by animateDpAsState(
        targetValue = if(expanded) 0.dp else 16.dp,
        label = "Global search bar padding"
    )

    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = searchBarPadding),
        inputField = { SearchBarDefaults.InputField(
            query = query,
            onQueryChange = { query = it },
            onSearch = { onSearch(query) },
            expanded = expanded,
            onExpandedChange = {
                expanded = it
                // Clear query text when search bar expands
                query = ""
                // Hide keyboard when search bar closes
                if (!it) keyboardController?.hide()
            },
            placeholder = { Text("Trà đào, Phúc Long") },
            leadingIcon = {
                if (expanded) Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable {
                        expanded = false // Collapse search bar
                        keyboardController?.hide() // Hide keyboard
                    }
                )
                else Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                )
            },
            trailingIcon = {
                if (expanded) {
                    if (query.isNotEmpty()) Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable {
                            query = "" // Clear query text
                        }
                    )
                }
                else AsyncImageOrMonogram(
                    model = userAvatarUrl,
                    name = userName,
                    contentDescription = "User avatar",
                    size = 38.dp,
                    onClick = onAvatarClicked
                )
            },
        ) },
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            // Hide keyboard when search bar closes
            if (!it) keyboardController?.hide()
        },
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            repeat(4) { idx ->
                val resultText = "Suggestion $idx"
                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .clickable {
                            query = resultText
                            expanded = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchPrev() {
    ComponentPreview {
        Box(Modifier.fillMaxSize()) {
            GlobalSearchBar(
                userName = "Ho Nguyen",
                userAvatarUrl = "",
                modifier = Modifier.align(Alignment.TopCenter)
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 68.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.semantics { traversalIndex = 1f },
            ) {
                val list = List(100) { "Text $it" }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}