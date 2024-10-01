package com.aaraf.dhl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException

class MainActivity2 : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceNoteApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoiceNoteApp() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    val voiceNotes = remember { mutableStateListOf<File>() }
    var currentFilePath by remember { mutableStateOf<String?>(null) }
    val mediaRecorder = remember { MediaRecorder() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Voice Notes") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        stopRecording(mediaRecorder, currentFilePath, voiceNotes)
                    } else {
                        startRecording(mediaRecorder, context)?.let { filePath ->
                            currentFilePath = filePath
                        }
                    }
                    isRecording = !isRecording
                },
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Record Voice Note",
                    tint = Color.White
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End
                ) {
                    items(voiceNotes.size) { index ->
                        val file = voiceNotes[index]
                        VoiceNoteItem(file)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isRecording) "Recording..." else "Hold to Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

fun startRecording(mediaRecorder: MediaRecorder, context: Context): String? {
    val outputDir = ContextCompat.getExternalFilesDirs(context, null)[0]
    val outputFile = File(outputDir, "voice_note_${System.currentTimeMillis()}.3gp")

    try {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.setOutputFile(outputFile.absolutePath)
        mediaRecorder.prepare()
        mediaRecorder.start()
        return outputFile.absolutePath
    } catch (e: IOException) {
        Toast.makeText(context, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
    return null
}

fun stopRecording(mediaRecorder: MediaRecorder, filePath: String?, voiceNotes: MutableList<File>) {
    try {
        mediaRecorder.stop()
        filePath?.let {
            voiceNotes.add(File(it))
        }
    } catch (e: IllegalStateException) {
        // Handle stop failure
    }
}


@Composable
fun VoiceNoteItem(file: File) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var totalDuration by remember { mutableIntStateOf(0) }
    var currentPosition by remember { mutableIntStateOf(0) }

    // Handler for updating progress while playing
    val handler = remember { Handler(Looper.getMainLooper()) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
            }

            // Keep updating the progress
            while (isPlaying && currentPosition < totalDuration) {
                delay(100L)
                currentPosition = mediaPlayer.currentPosition
                currentProgress = currentPosition.toFloat() / totalDuration.toFloat()
            }
        }
    }

    // Setup the media player when the file is loaded
    LaunchedEffect(file) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            totalDuration = mediaPlayer.duration
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to load audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .padding(vertical = 8.dp)
            .background(
                color = Color(0xFFF8C4C4),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Play/Pause Button
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Pause",
            tint = Color.Red,
            modifier = Modifier
                .clickable(onClick = {
                    if (isPlaying) {
                        mediaPlayer.pause()
                    } else {
                        mediaPlayer.start()
                    }
                    isPlaying = !isPlaying
                })
                .size(32.dp)
        )


        // Progress Bar (simulating waveform)
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Waveform/Progress Bar
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                val progressWidth = size.width * currentProgress
                drawLine(
                    color = Color.Red,
                    start = Offset.Zero,
                    end = Offset(progressWidth, 0f),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Audio Duration and Timestamp
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatDuration(currentPosition),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDuration(totalDuration),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Helper function to format milliseconds into MM:SS format
@SuppressLint("DefaultLocale")
fun formatDuration(durationMs: Int): String {
    val minutes = (durationMs / 1000) / 60
    val seconds = (durationMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
