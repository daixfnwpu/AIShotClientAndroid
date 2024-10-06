package com.ai.aishotclientkotlin.data.dao.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey

@Immutable
@Entity(primaryKeys = [("video_id")],
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = Movie::class,
            parentColumns = ["id"], // 假设 Movie 类中有一个名为 id 的主键
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Video (
  //  movie = models.ForeignKey(Movie, related_name='videos', on_delete=models.CASCADE)
    var movieId: String,
    var video_id : String,// = models.CharField(max_length=255)
    var name : String,//models.CharField(max_length=255)
    var site :String?,//= models.CharField(max_length=255,null=True, blank=True)  # e.g., "YouTube"
    var key :String?,//= models.CharField(max_length=255,null=True, blank=True)  # e.g., YouTube video key
    var size : Int?,//= models.IntegerField(null=True, blank=True)  # e.g., 720, 1080
    var type : String = "image"//= models.CharField(max_length=255,default="image",blank=True)  # e.g., "image", "video"
)