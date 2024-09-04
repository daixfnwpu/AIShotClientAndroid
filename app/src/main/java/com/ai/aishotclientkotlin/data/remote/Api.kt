package com.ai.aishotclientkotlin.data.remote

object Api {

    const val BASE_URL : String = "http://192.168.3.116:8080/"

    const val LOGIN : String = BASE_URL + "login/"
    const val REGISTER : String = BASE_URL + "register/"
    const val LANGUAGES : String = BASE_URL + "languages/"
    const val WORDS : String = BASE_URL + "words"

    const val SplashImagePath: String = BASE_URL + "images/1.jpg"
    const val LoginImagePath: String = BASE_URL + "images/2.jpg"
    const val LanguageImagePath: String = BASE_URL + "images/3.jpg"

   // const val BASE_URL = TYPEONE
    private const val BASE_POSTER_PATH =BASE_URL + "t/p/w342"
    private const val BASE_BACKDROP_PATH = "$BASE_URL/t/p/w780"
    private const val YOUTUBE_VIDEO_URL =BASE_URL + "watch?v="
    private const val YOUTUBE_THUMBNAIL_URL = "$BASE_URL/vi/"
    const val PAGING_SIZE = 20

    @JvmStatic
    fun getPosterPath(posterPath: String?): String {
        return BASE_POSTER_PATH + posterPath
    }

    @JvmStatic
    fun getBackdropPath(backdropPath: String?): String {
        return BASE_BACKDROP_PATH + backdropPath
    }

    @JvmStatic
    fun getYoutubeVideoPath(videoPath: String?): String {
        return YOUTUBE_VIDEO_URL + videoPath
    }

    @JvmStatic
    fun getYoutubeThumbnailPath(thumbnailPath: String?): String {
        return "$YOUTUBE_THUMBNAIL_URL$thumbnailPath/default.jpg"
    }
}