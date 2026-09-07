package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@Composable
fun ReelsScreen() {
    val pagerState = rememberPagerState(pageCount = { 5 })
    
    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        ReelItem(page, isCurrentPage = pagerState.currentPage == page)
    }
}

@Composable
fun ReelItem(page: Int, isCurrentPage: Boolean) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Video Player using Media3 ExoPlayer
        val context = LocalContext.current
        var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
        
        // Mock Video URLs. In a real app, this comes from the Repository.
        val videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"

        DisposableEffect(videoUrl) {
            val player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }
            exoPlayer = player
            onDispose {
                player.release()
            }
        }

        LaunchedEffect(isCurrentPage, exoPlayer) {
            if (isCurrentPage) {
                exoPlayer?.play()
            } else {
                exoPlayer?.pause()
            }
        }

        exoPlayer?.let { player ->
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } ?: run {
            // Placeholder while loading
            AsyncImage(
                model = "https://picsum.photos/seed/reel$page/1080/1920",
                contentDescription = "Reel Content",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Overlay Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Right Side Actions
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReelAction(icon = Icons.Filled.Favorite, label = "${100 + page * 15}K")
                ReelAction(icon = Icons.Filled.Comment, label = "${10 + page}")
                ReelAction(icon = Icons.Filled.Share, label = "Share")
                ReelAction(icon = Icons.Filled.MoreVert, label = "")
            }

            // Bottom Info
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(end = 64.dp)) {
                Text(
                    text = "@creator_$page",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This is a beautiful reel $page #utsav #trending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReelAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(32.dp))
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}
