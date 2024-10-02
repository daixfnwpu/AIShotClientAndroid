package com.ai.aishotclientkotlin.data.remote

object Api {

    const val BASE_URL : String = "http://192.168.3.116:8080/"
    // const val BASE_URL : String = "http://10.0.2.2:8080/"

    const val LOGIN : String = BASE_URL + "login/"
    const val REGISTER : String = BASE_URL + "register/"
    const val LANGUAGES : String = BASE_URL + "languages/"
    const val WORDS : String = BASE_URL + "words"

    const val SplashImagePath: String = BASE_URL + "images/splash.jpg"
    const val LoginImagePath: String = BASE_URL + "images/2.jpg"
    const val LanguageImagePath: String = BASE_URL + "images/3.jpg"
    const val AvatarImagePath: String = BASE_URL + "t/avatar/"

   // const val BASE_URL = TYPEONE
    private const val BASE_POSTER_PATH =BASE_URL + "t/p/w342/"
    private const val BASE_BACKDROP_PATH = "${BASE_URL}t/p/w780"
    private const val YOUTUBE_VIDEO_URL =BASE_URL + "watch?v="
    private const val YOUTUBE_THUMBNAIL_URL = "${BASE_URL}/vi/"
    private const val BILIBILI_VIDEO_URL:String = "https://www.bilibili.com/video/"
    private const val MYVIDEO_URL = BASE_URL + "t/video/"
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
    fun getBilibiliVideoPath(videoPath: String?): String {
        return BILIBILI_VIDEO_URL + videoPath
    }

    @JvmStatic
    fun getMySiteVideoPath(videoPath: String?): String {
        return MYVIDEO_URL + videoPath
    }


    @JvmStatic
    fun getYoutubeThumbnailPath(thumbnailPath: String?): String {
        return "$YOUTUBE_THUMBNAIL_URL$thumbnailPath/default.jpg"
    }

    fun getAvatarImage(posterPath: String?): String {
        return AvatarImagePath + posterPath
    }
}