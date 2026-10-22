package com.ebc.muffinshop.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ebc.muffinshop.model.Paso
import com.ebc.muffinshop.ui.screens.SaborScreen
import com.ebc.muffinshop.viewmodel.PedidoViewModel

@Composable
fun MuffinShopApp(viewModel: PedidoViewModel = viewModel()) {
    val navController = rememberNavController()
    val backEntry by navController.currentBackStackEntryAsState()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Text(text = "Soy el topbar")
        },
        bottomBar = {
            Text(text = "Soy el bottom")
        }
    ) {
        contentPadding ->

        NavHost(
            modifier = Modifier.padding((contentPadding)),
            navController = navController,
            startDestination = Paso.SABOR.ruta
        ) {
            composable(route = Paso.SABOR.ruta) {
                SaborScreen(state = state, onSeleccionar = viewModel::seleccionarSabor)
            }


            // Estas rutas son de prueba
            composable(route = "Inicio") {
                Column {
                    Text(text = "Pantalla Inicio")
                    Button(
                        onClick = { navController.navigate("Fin") }
                    ) {
                        Text(text = "Ir a fin")
                    }
                }
            }
            composable(route = "Fin") {
                Column {
                    Text(text = "Pantalla Fin")
                    Button(
                        onClick = { navController.navigate("Inicio") }
                    ) {
                        Text(text = "Ir a Inicio")
                    }
                }
            }
        }

    }
}