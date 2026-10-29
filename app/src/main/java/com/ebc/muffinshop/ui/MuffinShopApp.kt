package com.ebc.muffinshop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ebc.muffinshop.model.Paso
import com.ebc.muffinshop.ui.screens.CantidadScreen
import com.ebc.muffinshop.ui.screens.ClienteScreen
import com.ebc.muffinshop.ui.screens.ExitoScreen
import com.ebc.muffinshop.ui.screens.RellenoScreen
import com.ebc.muffinshop.ui.screens.ResumenScreen
import com.ebc.muffinshop.ui.screens.SaborScreen
import com.ebc.muffinshop.ui.screens.ToppingsScreen
import com.ebc.muffinshop.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuffinShopApp(viewModel: PedidoViewModel = viewModel()) {
    val navController = rememberNavController()
    val backEntry by navController.currentBackStackEntryAsState()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var mostrarCancelacion by remember { mutableStateOf(true) }

    val paso = Paso.entries.firstOrNull {
        it.ruta == backEntry?.destination?.route
    } ?: Paso.SABOR

    fun reiniciarCompra() {
        viewModel.reiniciar()
        navController.navigate(Paso.SABOR.ruta) {
            popUpTo(Paso.SABOR.ruta) {inclusive = true}
        }
    }

    if (mostrarCancelacion) {
        AlertDialog(
            onDismissRequest = {mostrarCancelacion = false},
            title = { Text(text = "¿Cancelar el pedido?") },
            text = { Text(text = "Se perderán toda la configuración del pedido y tendrás que iniciar desde 0.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarCancelacion = false
                        reiniciarCompra()

                    }
                ) {
                    Text(text = "Sí, reiniciar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {mostrarCancelacion = false}
                ) {
                    Text(text = "No... continuar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            //Text(text = "Soy el topbar")
            TopAppBar(
                title = {
                    Text(text = if (paso == Paso.EXITO) "¡Gracias!" else paso.titulo)
                },
                navigationIcon = {
                    if(paso != Paso.SABOR && paso != Paso.EXITO) {
                        TextButton(
                            onClick = {navController.popBackStack()}
                        ) {
                            Text(text = "Atrás")
                        }
                    }
                },

                actions = {
                    if(paso != Paso.EXITO) {
                        TextButton(
                            onClick = {
                                mostrarCancelacion = true
                            }
                        ) {
                            Text(text = "Cancelar")
                        }
                    }
                }
            )
        },
        bottomBar = {
            //Text(text = "Soy el bottom")

            if(paso != Paso.EXITO) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {navController.popBackStack()},
                            enabled = paso != Paso.SABOR,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Regresar")
                        }

                        Button(
                            onClick = {
                                if(paso == Paso.RESUMEN) {
                                    viewModel.confirmarPedido()
                                    navController.navigate(Paso.EXITO.ruta) {
                                        popUpTo(Paso.SABOR.ruta) { inclusive = false }
                                    }
                                } else {
                                    Paso.entries.getOrNull(paso.ordinal + 1)?.let {
                                        siguiente -> navController.navigate(siguiente.ruta)
                                    }                                }
                            },

                            enabled = state.esValido(paso),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = if (paso == Paso.RESUMEN) "Enviar" else "Siguiente")
                        }
                    }
                }
            }

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

            composable(route = Paso.RELLENO.ruta) {
                RellenoScreen(state = state, onSeleccionar = viewModel::seleccionarRelleno)
            }

            composable(route = Paso.TOPPINGS.ruta) {
                ToppingsScreen(state = state, onAlternar = viewModel::alternarTopping)
            }

            composable(route = Paso.CANTIDAD.ruta) {
                CantidadScreen(state = state, onCantidadChange = viewModel::cambiarCantidad)
            }

            composable(route = Paso.CLIENTE.ruta) {
                ClienteScreen(
                    state = state,
                    onNombreChange = viewModel::cambiarNombre,
                    onTelefonoChange = viewModel::cambiarTelefono,
                    onCorreoChange = viewModel::cambiarCorreo
                )
            }

            composable(route = Paso.RESUMEN.ruta) {
                ResumenScreen(state = state)
            }

            composable(route = Paso.EXITO.ruta) {
                ExitoScreen(state = state, onNuevoPedido = {
                    reiniciarCompra()
                })
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