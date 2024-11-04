package com.bignerdranch.android.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.photogallery.api.GalleryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {

    private val photoRepository = PhotoRepository()
    private val preferencesRepository = PreferencesRepository.get()

    private val _galleryItems: MutableStateFlow<List<GalleryItem>> =
        MutableStateFlow(emptyList())
    val galleryItems: StateFlow<List<GalleryItem>>
        get() = _galleryItems.asStateFlow()

    init {
        viewModelScope.launch {

            preferencesRepository.storedQuery.collectLatest {
                try {
                    Log.d(TAG, " about to invoke fetchGalleryItems() via collectlatest in init block")
                    val items = fetchGalleryItems(it)
                    _galleryItems.value = items
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch gallery items", e)
                }
            }
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch { preferencesRepository.setStoredQuery(query) }
    }

    private suspend fun fetchGalleryItems(query: String): List<GalleryItem> {
        return if (query.isNotEmpty()) {
            photoRepository.searchPhotos(query)
        }
        else {
            photoRepository.fetchPhotos()
        }
    }
}