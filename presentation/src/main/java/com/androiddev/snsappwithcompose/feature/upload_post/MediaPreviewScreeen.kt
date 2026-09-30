package com.androiddev.snsappwithcompose.feature.upload_post


/**@Composable
fun MediaPreviewScreen(
    navController:NavController,
    viewModel: UploadPostViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
) {

    val launchMediaPicker = rememberMediaPicker { uriList ->
        viewModel.onEvent(UploadPostEvent.AddMedia(uriList))
    }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CenterAlignedTopBar(
                    title = getString(LocalContext.current, R.string.media),
                    rightAction = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }


        }

    ) { paddingValues ->
        Box (
            modifier = Modifier.fillMaxSize().padding(paddingValues)

        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                items(
                    items = viewModel.selectedMediaItems
                ) { item ->
                    MediaItemView(
                        item = item,
                        onClick = {
                            if(item.type== MediaType.VIDEO) {
                                val source = item.uri?.toString()
                                    ?: (BuildConfig.BASE_URL + item.remotePath)

                                val encoded = Uri.encode(source)
                                navController.navigate(
                                    Screen.VideoPlayerScreen(
                                        encodedUri = encoded,
                                ))

                            }

                                  },
                        onDelete = { item ->
                            viewModel.onEvent(UploadPostEvent.DeleteMedia(item))

                        }
                    )

                }
               item {
                   Text(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(14.dp)
                           .clickable { launchMediaPicker() },
                       textAlign = TextAlign.Center,
                       fontWeight = FontWeight.Bold,
                       fontSize = 18.sp,
                       text = getString(context,R.string.button_text_add_media),
                       color = Color.Black
                   )
               }

            }


        }

    }


}**/
