package com.example.animehub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashFile = File(filesDir, "crash.txt")
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            try { crashFile.writeText(Log.getStackTraceString(e)) } catch (_: Throwable) {}
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
        if (crashFile.exists()) {
            val text = crashFile.readText()
            setContent { CrashScreen(text) { crashFile.delete(); recreate() } }
            return
        }
        setContent {
            MaterialTheme {
                Surface {
                    val nav = rememberNavController()
                    val vm: AnimeViewModel = viewModel()
                    NavHost(nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(vm, { nav.navigate("detail/$it") }, { nav.navigate("player") })
                        }
                        composable("detail/{id}", listOf(navArgument("id") { type = NavType.IntType })) {
                            DetailScreen(vm.byId(it.arguments!!.getInt("id"))) { nav.navigate("player") }
                        }
                        composable("player") { PlayerScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun CrashScreen(text: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .statusBarsPadding().padding(12.dp)
    ) {
        Text("حدث خطأ. صوّر هذه الشاشة وابعثها:", fontSize = 16.sp)
        Text(text.take(2500), fontSize = 9.sp)
        Button(onClick = onRetry) { Text("حاول مرة أخرى") }
    }
}
