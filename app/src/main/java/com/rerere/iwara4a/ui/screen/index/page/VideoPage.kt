package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.public.QueryParamSelector
import com.rerere.iwara4a.ui.public.items
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun VideoListPage(navController: NavController, indexViewModel: IndexViewModel) {
    val videoList = indexViewModel.videoPager.collectAsLazyPagingItems()
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = videoList.loadState.refresh == LoadState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        if (videoList.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        videoList.retry()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                    LottieAnimation(
                        modifier = Modifier.size(150.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { videoList.refresh() },
                indicator = { s, trigger ->
                    SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colors.primary)
                }) {
                Column {
                    QueryParamSelector(
                        queryParam = indexViewModel.videoQueryParam,
                        onChangeSort = {
                            indexViewModel.videoQueryParam.sortType = it
                            videoList.refresh()
                        },
                        onChangeFilters = {
                            indexViewModel.videoQueryParam.filters = it
                            videoList.refresh()
                        }
                    )
                    Box(contentAlignment = Alignment.Center) {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            cells = GridCells.Fixed(2)
                        ) {
                            items(videoList) {
                                MediaPreviewCard(navController, it!!)
                            }

                            when (videoList.loadState.append) {
                                LoadState.Loading -> {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(Modifier.size(30.dp))
                                            Text(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                text = "加载中..."
                                            )
                                        }
                                    }
                                }
                                is LoadState.Error -> {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .noRippleClickable { videoList.retry() }
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(140.dp)
                                                        .padding(10.dp)
                                                        .clip(CircleShape)
                                                ) {
                                                    Image(
                                                        modifier = Modifier.fillMaxSize(),
                                                        painter = painterResource(R.drawable.anime_2),
                                                        contentDescription = null
                                                    )
                                                }
                                                Text(
                                                    modifier = Modifier.padding(horizontal = 16.dp),
                                                    text = "加载失败: ${(videoList.loadState.append as LoadState.Error).error.message}"
                                                )
                                                Text(text = "点击重试")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (videoList.loadState.refresh == LoadState.Loading && videoList.itemCount == 0) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.sunny
                                )
                            )
                            LottieAnimation(
                                modifier = Modifier.size(250.dp),
                                composition = composition,
                                iterations = LottieConstants.IterateForever
                            )
                        }
                    }
                }
            }
        }
    }
}