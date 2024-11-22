package co.algorand.app.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

var passphraseTextField: String = ""

@SuppressLint("ComposableNaming")
@Composable
fun AlgorandButton(
    stringResourceId: StringResource,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        // colors = ButtonDefaults.buttonColors(colorResource(Color.)),
        shape = RoundedCornerShape(8.dp),
        modifier =
        Modifier
            .width(190.dp)
            .height(50.dp),
    ) {
        Text(
            stringResource(resource = stringResourceId),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun AlgorandDivider() {
    HorizontalDivider(
        modifier =
        Modifier
            .width(300.dp),
        thickness = 1.dp,
        color = Color.Gray,
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun PassphraseField(
    label: String,
    textData: String,
) {
    var textInput by remember { mutableStateOf(textData) }
    OutlinedTextField(
        value = textInput,
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        onValueChange = {
            textInput = it
            passphraseTextField = it
        },
        label = {
            Text(
                text = label,
                color = Color.Black,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(),
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp),
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun ShowSnackbar(message: String) {
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    SnackbarHost(
        modifier = Modifier,
        hostState = snackState,
    ) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.White,
            contentColor = Color.Black,
        )
    }

    LaunchedEffect(Unit) {
        snackScope.launch { snackState.showSnackbar(message) }
        // openSnackbar(false)
    }
}

annotation class SuppressLint(val value: String)