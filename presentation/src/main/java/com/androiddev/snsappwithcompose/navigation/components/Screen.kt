package com.androiddev.snsappwithcompose.navigation.components
import android.os.Bundle
import androidx.navigation.NavType
import com.androiddev.domain.model.PostPreview
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.reflect.typeOf
import android.net.Uri
sealed interface Screen {

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
    //@Serializable
    //data object NearPostsScreen: Screen
    @Serializable
    data object UploadPostScreen: Screen
    @Serializable
    data object InitScreen: Screen
    @Serializable
    data class CropScreen(val encodedUri:String): Screen
    @Serializable
    data class PostDetailScreen(val post:PostPreview?): Screen

}
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