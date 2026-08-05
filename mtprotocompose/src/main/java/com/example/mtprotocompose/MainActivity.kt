package com.example.mtprotocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mtprotocompose.ui.theme.MtprotoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MtprotoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    println(innerPadding)
                    val listState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    var isRefreshing0 by remember { mutableStateOf(false) }

                    val onRefresh: () -> Unit = {
                        isRefreshing0 = true
                        coroutineScope.launch {
                            delay(600)
                            isRefreshing0 = false
                        }
                    }
                    PullToRefreshBox(
                        isRefreshing = isRefreshing0, // Indicates if the loading indicator should be shown
                        onRefresh = onRefresh,     // The action to perform when the user triggers a refresh
                        modifier = Modifier
                    ) {
                        LazyColumn(Modifier.fillMaxSize(), state = listState) {
                            items(220) { index ->
                                ListItem(headlineContent = { Text(text = "@@@$index") })
                            }
                        }
                    }
                }
            }
        }
    }
}