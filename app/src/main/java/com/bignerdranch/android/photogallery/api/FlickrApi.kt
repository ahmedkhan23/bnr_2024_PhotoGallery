package com.bignerdranch.android.photogallery.api

import retrofit2.http.GET

private const val API_KEY = "7ae806edff274ef06c3919ab1cf6a13d"

interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=$API_KEY" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    suspend fun fetchPhotos(): FlickrResponse


}