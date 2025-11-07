package com.utc.vistadehalcon
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utc.vistadehalcon.ui.theme.VistaDeHalconTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        setContent {
            VistaDeHalconTheme {
                val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
                AppNavigator(
                    isLoggedIn = isLoggedIn,
                    onLogin = {
                        // Guardar sesión
                        CoroutineScope(Dispatchers.IO).launch {
                            sessionManager.setLoggedIn(true)
                        }
                    },
                    onLogout = {
                        CoroutineScope(Dispatchers.IO).launch {
                            sessionManager.setLoggedIn(false)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigator(
    isLoggedIn: Boolean,
    onLogin: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                onLogin()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(onLogout = {
                onLogout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inicio de sesión")

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (username.isEmpty()) Text("Correo institucional")
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (password.isEmpty()) Text("Contraseña")
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val institutionalDomain = "@utc.edu.mx"
            when {
                username.isEmpty() || password.isEmpty() -> {
                    message = "Por favor, completa todos los campos"
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches() -> {
                    message = "Correo inválido"
                }
                !username.endsWith(institutionalDomain) -> {
                    message = "Usa tu correo institucional ($institutionalDomain)"
                }
                else -> {
                    message = ""
                    onLoginSuccess()
                }
            }
        }) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        androidx.core.app.ActivityCompat.requestPermissions(
            (context as ComponentActivity),
            permissions,
            0
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a la pantalla principal")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                try {
                    if (!isRecording) {
                        val filePath = recorder.startRecording()
                        if (filePath != null) {
                            recordedFilePath = filePath
                            message = "Grabando... archivo: ${filePath.substringAfterLast('/')}"
                            isRecording = true
                        } else {
                            message = "Error al iniciar grabación (verifica permisos)."
                        }
                    } else {
                        recorder.stopRecording()
                        message = "Grabación detenida.\nArchivo: ${recordedFilePath ?: "desconocido"}"
                        isRecording = false
                    }
                } catch (e: Exception) {
                    message = "Error: ${e.message}"
                    e.printStackTrace()
                }
            }
        ) {
            Text(if (!isRecording) "Iniciar alerta" else "Detener grabación")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onLogout() }) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    VistaDeHalconTheme {
        LoginScreen(onLoginSuccess = {})
    }
}