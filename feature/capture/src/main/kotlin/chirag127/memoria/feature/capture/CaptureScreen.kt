package chirag127.memoria.feature.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** MVP capture surface: type/paste → save → AI enrich → markdown → vault commit. */
@Composable
fun CaptureScreen(viewModel: CaptureViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Capture", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = state.text,
            onValueChange = viewModel::onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("What happened / what did you learn?") },
            minLines = 4,
            enabled = !state.saving,
        )
        Button(
            onClick = { viewModel.save() },
            enabled = !state.saving && state.text.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.saving) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
            Text(if (state.saving) "Saving…" else "Save to vault")
        }
        state.lastSavedId?.let {
            Text("Saved ✓ ($it)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
        state.error?.let {
            Text("Error: $it", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
    }
}
