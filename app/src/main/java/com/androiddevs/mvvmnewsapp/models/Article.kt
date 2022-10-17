package com.androiddevs.mvvmnewsapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity( tableName = "Articles")
@Parcelize  //The @Parcelize annotation Instructs the Kotlin compiler to generate the
//writeToParcel(), and describeContents() methods, also the CREATOR factory class automatically.
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val author: String? = null,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source, //Since room can only handle primitive data types,
                        //we have to add typeConverters for custom data types
                        //like 'Source'
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Parcelable