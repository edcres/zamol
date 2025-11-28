package com.example.zamol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zamol.ui.screens.AuthScreen
import com.example.zamol.ui.screens.ChatScreen
import com.example.zamol.ui.screens.UserSelectorScreen
import com.google.firebase.auth.FirebaseAuth

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
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.SELECT_USER) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SELECT_USER) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            UserSelectorScreen(
                onUserSelected = { selectedUser ->
                    val me = currentUserId ?: return@UserSelectorScreen

                    // DM-style deterministic room ID for 1-to-1 chat
                    val chatRoomId = listOf(me, selectedUser.uid)
                        .sorted()
                        .joinToString("_")

                    navController.navigate("${Routes.CHAT}/$chatRoomId")
                }
            )
        }

        composable(
            route = "${Routes.CHAT}/{chatRoomId}",
            arguments = listOf(navArgument("chatRoomId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: return@composable
            ChatScreen(chatRoomId = chatRoomId)
        }
    }
}

