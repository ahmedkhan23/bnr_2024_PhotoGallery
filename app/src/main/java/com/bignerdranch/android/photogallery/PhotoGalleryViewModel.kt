package com.bignerdranch.android.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.photogallery.api.GalleryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {

    private val photoRepository = PhotoRepository()
    private val preferencesRepository = PreferencesRepository.get()

//    private val _galleryItems: MutableStateFlow<List<GalleryItem>> =
//        MutableStateFlow(emptyList())
//    val galleryItems: StateFlow<List<GalleryItem>>
//        get() = _galleryItems.asStateFlow()

    private val _uiState: MutableStateFlow<PhotoGalleryUiState> =
        MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {

            preferencesRepository.storedQuery.collectLatest {
                try {
                    Log.d(TAG, " about to invoke fetchGalleryItems() via collectlatest in init block")
                    val items = fetchGalleryItems(it)

                    _uiState.update { oldState ->
                        oldState.copy(
                            images = items,
                            query = it
                        )
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch gallery items", e)
                }
            }
        }

        viewModelScope.launch {
            preferencesRepository.isPolling.collect { isPolling ->
                _uiState.update { it.copy(isPolling = isPolling) }
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

    fun togglePolling() {
        viewModelScope.launch {
            preferencesRepository.setPolling(!uiState.value.isPolling)
        }
    }


}

data class PhotoGalleryUiState(
    val images: List<GalleryItem> = listOf(),
    val query: String = "",
    val isPolling: Boolean = false
)