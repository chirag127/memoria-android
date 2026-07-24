package chirag127.memoria.feature.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chirag127.memoria.domain.model.CaptureSourceKind
import chirag127.memoria.domain.repository.MemoryRepository
import chirag127.memoria.domain.repository.RawCapture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaptureUiState(
    val text: String = "",
    val saving: Boolean = false,
    val lastSavedId: String? = null,
    val error: String? = null,
)

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: MemoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CaptureUiState())
    val state: StateFlow<CaptureUiState> = _state.asStateFlow()

    fun onTextChange(text: String) = _state.update { it.copy(text = text, error = null) }

    fun save(source: CaptureSourceKind = CaptureSourceKind.MANUAL) {
        val text = _state.value.text.trim()
        if (text.isEmpty()) return
        _state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            repository.capture(RawCapture(text = text, sourceKind = source))
                .onSuccess { id -> _state.update { CaptureUiState(lastSavedId = id) } }
                .onFailure { e -> _state.update { it.copy(saving = false, error = e.message ?: "capture failed") } }
        }
    }
}
