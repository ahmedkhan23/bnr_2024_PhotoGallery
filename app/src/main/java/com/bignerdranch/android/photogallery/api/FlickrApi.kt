package com.bignerdranch.android.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "7ae806edff274ef06c3919ab1cf6a13d"

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(): FlickrResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse


}