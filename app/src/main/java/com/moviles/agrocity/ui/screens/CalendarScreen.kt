package com.moviles.agrocity.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.moviles.agrocity.R
import com.moviles.agrocity.network.GeminiSugeridorClient
import com.moviles.agrocity.network.WeatherViewModel
import com.moviles.agrocity.network.fetchWeatherByLocation
import com.moviles.agrocity.network.procesarRespuestaSugerencia
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val weatherViewModel: WeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val climaTexto by weatherViewModel.weatherText.collectAsState(initial = "")
    val scope = rememberCoroutineScope()

    val hoy = ZonedDateTime.now(ZoneId.of("America/Costa_Rica")).toLocalDate()
    val currentMonth = YearMonth.now()

    val mesesEspañol = mapOf(
        "JANUARY" to "Enero", "FEBRUARY" to "Febrero", "MARCH" to "Marzo",
        "APRIL" to "Abril", "MAY" to "Mayo", "JUNE" to "Junio",
        "JULY" to "Julio", "AUGUST" to "Agosto", "SEPTEMBER" to "Septiembre",
        "OCTOBER" to "Octubre", "NOVEMBER" to "Noviembre", "DECEMBER" to "Diciembre"
    )
    val nombreMes = mesesEspañol[currentMonth.month.name] ?: currentMonth.month.name
    val diasMes = currentMonth.lengthOfMonth()
    val primerDiaSemana = currentMonth.atDay(1).dayOfWeek.value
    val diaActual = hoy.dayOfMonth

    var locationFetched by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("Obteniendo ubicación...") }
    var sugerenciaCultivos by remember { mutableStateOf("Cargando sugerencias de cultivos...") }

    val geminiClient = remember { GeminiSugeridorClient(apiKey = "AIzaSyD_FIqvsukQPWJtkq1wVlrlL5TmMsdFHlg") }

    var cultivosEmojis by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var cultivosDetectados by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        if (!locationFetched) {
            locationFetched = true
            locationName = "La Victoria, Heredia , Costa Rica"
            val lat = "10.4300"
            val lon = "-84.0200"
            fetchWeatherByLocation(lat, lon, weatherViewModel, scope)
        }
    }

    LaunchedEffect(locationName, climaTexto, nombreMes, diaActual) {
        if (
            locationName.isNotBlank() &&
            climaTexto.isNotBlank() &&
            locationName !in listOf("Obteniendo ubicación...", "Ubicación no disponible", "Permiso de ubicación no concedido") &&
            climaTexto != "Cargando clima..."
        ) {
            geminiClient.sugerirCultivos(
                ubicacion = locationName,
                clima = climaTexto,
                mes = nombreMes,
                dia = hoy
            ) { respuesta ->
                procesarRespuestaSugerencia(respuesta) { sugerencia, cultivos, emojis ->
                    sugerenciaCultivos = sugerencia
                    cultivosDetectados = cultivos
                    cultivosEmojis = emojis
                }
            }
        }
    }

    val diasCalendario = remember {
        mutableStateListOf<String>().apply {
            repeat(primerDiaSemana - 1) { add("") }
            for (i in 1..diasMes) add(i.toString())
        }
    }

    val diasSemana = listOf("  L", "M", "M", "J", "V", "S", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendario de Siembra",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = Color(0xFF4CAF50)
            )
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "$nombreMes ${currentMonth.year}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(15.dp))
                    diasSemana.forEach { dia ->
                        Text(
                            text = dia,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF388E3C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .height(420.dp)
                        .padding(top = 6.dp),
                    userScrollEnabled = false
                ) {
                    items(diasCalendario.size) { index ->
                        val dia = diasCalendario[index]
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.8f)
                                .padding(1.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (dia.isNotEmpty()) {
                                val numDia = dia.toInt()
                                val mostrarEmoji = numDia >= diaActual

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(modifier = Modifier.height(20.dp)) {
                                        if (mostrarEmoji && cultivosEmojis.isNotEmpty()) {
                                            Text(
                                                text = cultivosEmojis.values.random(),
                                                fontSize = 18.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = dia,
                                        fontSize = 16.sp,
                                        fontWeight = if (numDia == diaActual) FontWeight.Bold else FontWeight.Normal,
                                        color = if (numDia == diaActual) Color(0xFF4CAF50) else Color.Black
                                    )

                                    if (mostrarEmoji && cultivosEmojis.isNotEmpty()) {
                                        Row {
                                            cultivosEmojis.values.forEach { emoji ->
                                                Text(text = emoji, fontSize = 14.sp, modifier = Modifier.padding(end = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ubicación: $locationName",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "  $climaTexto",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDFF0D8), shape = MaterialTheme.shapes.medium)
                    .border(1.dp, Color(0xFF4CAF50), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    text = sugerenciaCultivos,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
