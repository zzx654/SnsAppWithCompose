package com.androiddev.snsappwithcompose.common.navigation.component
import android.os.Bundle
import androidx.navigation.NavType
import com.androiddev.domain.model.PostPreview
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.reflect.typeOf
import android.net.Uri
import com.androiddev.domain.model.Comment
import com.androiddev.domain.model.Media
import com.androiddev.domain.model.MediaPost
import com.androiddev.snsappwithcompose.feature.upload_post.component.MediaItem

sealed interface Screen {

    @Serializable
    data object MainScreen: Screen
    @Serializable
    data object SignInScreen: Screen
    @Serializable
    data class SignUpScreen(val phoneNumber: String): Screen
    @Serializable
    data class AuthPhoneScreen(val platform:String,val account:String?): Screen
    @Serializable
    data object CreateprofileScreen: Screen
    @Serializable
    data object HomeScreen: Screen
    @Serializable
    data object NotificationScreen: Screen
    @Serializable
    data class UploadPostScreen(val post:PostPreview? = null): Screen
    @Serializable
    data object InitScreen: Screen
    @Serializable
    data class CropScreen(val encodedUri:String): Screen
    @Serializable
    data class PostDetailScreen(val postId:Int,val notificationCommentId:Int? = null): Screen
    @Serializable
    data class ReplyScreen(val comment: Comment?): Screen
    @Serializable
    data class TagPostsScreen(val tagId:Int): Screen
    @Serializable
    data class UserProfileScreen(val userId:Int): Screen
    @Serializable
    data object MediaEditScreen: Screen
    @Serializable
    data class MediaScreen(val mediaItems:List<Media>?): Screen
    @Serializable
    data class VideoPlayerScreen(val encodedUri:String): Screen

    @Serializable
    data object ImageViewerScreen: Screen



    @Serializable
    data object UploadFlow : Screen

}
const val UPLOAD_FLOW = "upload_flow"
inline fun <reified T : Any?> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String):T? {
        return json.decodeFromString(bundle.getString(key) ?: return null)
    }


    override fun parseValue(value: String): T = json.decodeFromString(Uri.decode(value))

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }

    override fun serializeAsValue(value: T): String = Uri.encode(Json.encodeToString(value))
}
/**inline fun <reified T : Any?> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }

    override fun serializeAsValue(value: T): String = json.encodeToString(value)
}**/
val postTypeMap = mapOf(typeOf<PostPreview?>() to serializableType<PostPreview?>(isNullableAllowed = true))
val commentTypeMap = mapOf(typeOf<Comment?>() to serializableType<Comment?>(isNullableAllowed = true))
val mediaTypeMap = mapOf(typeOf<List<Media>?>() to serializableType<List<Media>?>(isNullableAllowed = true))
val mediaPostsTypeMap = mapOf(typeOf<List<MediaPost>?>() to serializableType<List<MediaPost>?>(isNullableAllowed = true))