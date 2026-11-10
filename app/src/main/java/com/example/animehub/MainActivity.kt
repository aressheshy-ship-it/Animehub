package com.example.animehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
