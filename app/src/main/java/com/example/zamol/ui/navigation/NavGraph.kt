package com.example.zamol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zamol.ui.screens.AuthScreen
import com.example.zamol.ui.screens.ChatScreen
import com.example.zamol.ui.screens.UserSelectorScreen

object Routes {
    const val AUTH = "auth"
    const val SELECT_USER = "select_user"
    const val CHAT = "chat"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH,
        modifier = modifier
    ) {
        // Auth screen
        composable(Routes.AUTH) {
            AuthScreen(onAuthSuccess = {
                navController.navigate(Routes.SELECT_USER) {
                    popUpTo(Routes.AUTH) { inclusive = true }
                }
            })
        }

        // Select user to chat with
        composable(Routes.SELECT_USER) {
            UserSelectorScreen { selectedUser ->
                navController.navigate("${Routes.CHAT}/${selectedUser.uid}")
            }
        }

        // Chat screen with receiverId as nav argument
        composable(
            route = "${Routes.CHAT}/{receiverId}",
            arguments = listOf(navArgument("receiverId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
            ChatScreen(receiverId = receiverId)
        }
    }
}
