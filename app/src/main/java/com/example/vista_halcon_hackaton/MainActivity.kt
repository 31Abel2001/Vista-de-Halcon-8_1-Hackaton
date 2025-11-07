package com.utc.vistadehalcon


import com.utc.vistadehalcon.R
import com.utc.vistadehalcon.ui.theme.* // ¡ESTA IMPORTA TUS COLORES!
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.Manifest // ¡Importación necesaria para permisos!
import androidx.core.app.ActivityCompat // ¡Importación necesaria para permisos!

// --- ¡NUEVAS IMPORTACIONES PARA ANIMACIÓN E ÍCONOS! ---
import androidx.compose.animation.core.*

import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer

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

    // Fondo completo de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {

        Image(
            painter = painterResource(id = R.drawable.background_login), //
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )


        Image(
            painter = painterResource(id = R.drawable.logo_3),
            contentDescription = "Logo Vista de Halcón",
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopCenter)
                .offset(y = 35.dp)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.height(160.dp))

            Text(
                text = "¡BIENVENIDO\nHALCÓN!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = WelcomeGreen,
                textAlign = TextAlign.Start,
                lineHeight = 36.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Mantente a salvo,\nvuela tranquilo",
                fontSize = 18.sp,
                color = SubtitleGreen,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 32.dp)
                    .fillMaxWidth()
            )


            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Correo institucional") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp)),

                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent, // Quitar el underline cuando está enfocado
                    unfocusedIndicatorColor = Color.Transparent, // Quitar el underline cuando no está enfocado
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    // Colores de fondo del campo
                    focusedContainerColor = TextFieldGray,
                    unfocusedContainerColor = TextFieldGray,
                    disabledContainerColor = TextFieldGray,

                    unfocusedLabelColor = TextFieldLabelGray,
                    focusedLabelColor = TextFieldLabelGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp)),

                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    // Colores de fondo del campo
                    focusedContainerColor = TextFieldGray,
                    unfocusedContainerColor = TextFieldGray,
                    disabledContainerColor = TextFieldGray,
                    // Colores del texto "Contraseña"
                    unfocusedLabelColor = TextFieldLabelGray,
                    focusedLabelColor = TextFieldLabelGray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))



            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    val institutionalDomain = "@alumno.utc.edu.mx"
                    when {
                        username.isEmpty() || password.isEmpty() -> {
                            message = "Por favor, completa todos los campos"
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches() -> {
                            message = "Usa tu correo institucional (@alumno.utc.edu.mx)"
                        }
                        !username.endsWith(institutionalDomain) -> {
                            message = "Usa tu correo institucional ($institutionalDomain)"
                        }
                        else -> {
                            message = ""
                            onLoginSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(ButtonStartGreen, ButtonEndGreen)
                        ),
                        shape = RoundedCornerShape(30.dp) // Bordes redondeados
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Iniciar Sesión",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))



            Spacer(modifier = Modifier.height(16.dp))
            if (message.isNotEmpty()) {
                Text(message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
// --- PANTALLA DE HOME/SOS ---
@Composable
fun HomeScreen(onLogout: () -> Unit) {

    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }
    var isCountingDown by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(3) }
    var cancelRequested by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(
            (context as ComponentActivity),
            permissions,
            0
        )
    }


    val backgroundColor = when {
        isCountingDown -> CountdownBackground
        isRecording -> RecordingBackground
        else -> SosBackground
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        when {

            isCountingDown -> {
                SosCountdownScreen(
                    countdown = countdown,
                    onCancel = {
                        cancelRequested = true
                        isCountingDown = false
                        message = "Grabación cancelada."
                    }
                )
            }


            isRecording -> {
                SosRecordingScreen(
                    onStopRecording = {
                        recorder.stopRecording()
                        message = "Grabación detenida.\nArchivo: ${recordedFilePath ?: "desconocido"}"
                        isRecording = false
                    }
                )
            }

            // --- ESTADO 1: INICIAL ---
            else -> {
                SosDefaultScreen(
                    onStartAlert = {
                        // --- Lógica
                        if (!isRecording && !isCountingDown) {
                            isCountingDown = true
                            cancelRequested = false
                            countdown = 3
                            message = "Grabación iniciará en $countdown..."

                            CoroutineScope(Dispatchers.Main).launch {
                                for (i in 3 downTo 1) {
                                    if (cancelRequested) break
                                    countdown = i
                                    message = "Grabación iniciará en $i..."
                                    delay(1000L)
                                }

                                if (!cancelRequested) {
                                    try {
                                        val filePath = recorder.startRecording()
                                        if (filePath != null) {
                                            recordedFilePath = filePath
                                            message = "Grabando..."
                                            isRecording = true
                                        } else {
                                            message = "Error al iniciar grabación (verifica permisos)."
                                        }
                                    } catch (e: Exception) {
                                        message = "Error: ${e.message}"
                                    } finally {
                                        isCountingDown = false
                                    }
                                } else {
                                    isCountingDown = false
                                    message = "Grabación cancelada."
                                }
                            }
                        }
                        // --- Fin de la lógica  ---
                    },
                    onLogout = onLogout
                )
            }
        }



    }
}


// Pantalla SOS
@Composable
fun SosDefaultScreen(onStartAlert: () -> Unit, onLogout: () -> Unit) {


    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")


    val pulse1Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(0) // <-- ¡CAMBIO! (De 'initialAnimationClockOffset' a 'initialStartOffset')
        ), label = "pulse1Alpha"
    )
    val pulse1Size by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f, // Tamaño un poco más grande que el botón, pero no mucho
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(0)
        ), label = "pulse1Size"
    )

    // Animación para el segundo pulso
    val pulse2Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(500)
        ), label = "pulse2Alpha"
    )
    val pulse2Size by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(500) //
        ), label = "pulse2Size"
    )

    // Animación para el tercer pulso
    val pulse3Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000) // <-- ¡CAMBIO! (Y 1000ms de retraso)
        ), label = "pulse3Alpha"
    )
    val pulse3Size by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000) // <-- ¡CAMBIO!
        ), label = "pulse3Size"
    )


    Box(modifier = Modifier.fillMaxSize()) {
        // Cabecera con logo
        Image(
            painter = painterResource(id = R.drawable.header_sos),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 35.dp), // Botón
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Botón de SOS
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(270.dp)
                    .clickable { onStartAlert() }
            ) {
                //  TRES círculos de pulso,
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleX = pulse3Size
                            scaleY = pulse3Size
                            alpha = pulse3Alpha
                        }
                        .background(SosPulseBlue.copy(alpha = 0.6f), shape = CircleShape) // Ligeramente más opaco
                )

                // Pulso 2
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleX = pulse2Size
                            scaleY = pulse2Size
                            alpha = pulse2Alpha
                        }
                        .background(SosPulseBlue.copy(alpha = 0.8f), shape = CircleShape) // Más opaco
                )

                // Pulso 1
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            scaleX = pulse1Size
                            scaleY = pulse1Size
                            alpha = pulse1Alpha
                        }
                        .background(SosPulseBlue, shape = CircleShape) // Completamente opaco al inicio
                )


                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp) // Tamaño del botón real. Ajusta si necesitas que sea más grande o pequeño
                        .background(SosBlue, shape = CircleShape)
                ) {
                    Text(
                        text = "SOS",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto 1
            Text(
                text = "Presiona el botón solo en caso de peligro.",
                color = WelcomeGreen, // Tu color #5d6e1f
                fontWeight = FontWeight.Bold, // Negrita
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp)) //

            // Texto 2
            Text(
                text = "Se enviará un aviso a tu comunidad y se iniciará una grabación de seguridad.",
                color = WelcomeGreen,

                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
        }

        // Pie de página
        Image(
            painter = painterResource(id = R.drawable.footer_sos),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        // Botón de cerrar sesión
        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            elevation = null
        ) {
            Text(
                "Cerrar sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---  ESTADO 2: Conteo ---
@Composable
fun SosCountdownScreen(countdown: Int, onCancel: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {


         Modifier
          .fillMaxSize()
          .background(SosBackground)


        Image(
            painter = painterResource(id = R.drawable.header_sos), // <-- ¡CAMBIO! (Tu nueva cabecera)
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )

        // Contenido principal
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Ícono de micrófono
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .background(MicBackground, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabando",
                    modifier = Modifier.size(100.dp),
                    tint = SosTextGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Cancelar
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SosBlue
                )
            ) {
                Text(
                    "Cancelar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }



        // --- Mensaje de conteo de abajo
        Text(
            text = "Grabación\niniciará en $countdown...",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SosBlue,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp, //
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp, start = 30.dp, end = 30.dp)
        )


        Image(
            painter = painterResource(id = R.drawable.footer_sos),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )
    }
}
// --- ESTADO 3: Grabando ---
@Composable
fun SosRecordingScreen(onStopRecording: () -> Unit) {

    // Animación de "está grabando"
    val infiniteTransition = rememberInfiniteTransition(label = "recordingPulse")
    val recordingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, // Empieza semitransparente
        targetValue = 1f, // Se vuelve opaco
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Va y vuelve (pulso)
        ), label = "recordingAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Ícono de micrófono
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .alpha(recordingAlpha) //
                    .background(MicBackground, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabando",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF3B5183)//
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de "Cancelar"

            Button(
                onClick = onStopRecording,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 48.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B5183)
                )
            ) {
                Text(
                    "Detener Grabación",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

            }
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    VistaDeHalconTheme {

        SosDefaultScreen(onStartAlert = {}, onLogout = {})


    }
}