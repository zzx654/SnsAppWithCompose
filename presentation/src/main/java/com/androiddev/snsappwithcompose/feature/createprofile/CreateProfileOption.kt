package com.androiddev.snsappwithcompose.feature.createprofile

import com.androiddev.snsappwithcompose.R
import com.androiddev.snsappwithcompose.common.model.BottomSheetOption

enum class CreateProfileImageOption(
    override val iconRes: Int,
    override val titleRes: Int
): BottomSheetOption {
    Camera(R.drawable.camera_outlined,R.string.take_picture),
    Gallery(R.drawable.photo_library,R.string.choose_from_gallery),
    Delete(R.drawable.delete,R.string.delete_profileimage)
}