package com.example.zamol.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zamol.ui.screens.AuthScreen
import com.example.zamol.ui.screens.ChatScreen
import com.example.zamol.ui.screens.UserSelectorScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

                    val chatRoomId = listOf(me, selectedUser.uid)
                        .sorted()
                        .joinToString("_")

                    val chatTitle = selectedUser.displayName.ifBlank { selectedUser.email }
                    val encodedTitle = Uri.encode(chatTitle)

                    navController.navigate("${Routes.CHAT}/$chatRoomId/false/$encodedTitle")
                },
                onGroupSelected = { room ->
                    val chatTitle = room.name ?: "Group"
                    val encodedTitle = Uri.encode(chatTitle)

                    navController.navigate("${Routes.CHAT}/${room.id}/true/$encodedTitle")
                }
            )

        }

        composable(
            route = "${Routes.CHAT}/{chatRoomId}/{isGroup}/{title}",
            arguments = listOf(
                navArgument("chatRoomId") { type = NavType.StringType },
                navArgument("isGroup") { type = NavType.BoolType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: return@composable
            val isGroup = backStackEntry.arguments?.getBoolean("isGroup") ?: false
            val title = backStackEntry.arguments?.getString("title") ?: ""

            ChatScreen(
                chatRoomId = chatRoomId,
                isGroup = isGroup,
                title = title,
                onLeaveGroup = {
                    navController.popBackStack()
                }
            )
        }
    }
}

