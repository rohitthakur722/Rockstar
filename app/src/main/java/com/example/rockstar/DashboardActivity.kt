package com.example.rockstar

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockstar.ui.theme.RockstarTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RockstarTheme {
                DashboardBody()
            }
        }
    }
}

@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("name", "User") ?: "User"

    val tanColor = Color(0xFFD9B08C)
    val bgColor = Color(0xFF0D0D0D)
    val cardBg = Color(0xFF1C1C1E)

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab) { selectedTab = it }
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header: Menu, Logo, Notification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1C1C1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = tanColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Rockstar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Welcome Text
            Text(
                text = "Hello, $userName!",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Good to see you back 👋",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp)),
                placeholder = { Text("Search songs, artists, albums...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Featured Artist Card
            FeaturedArtistCard(tanColor)

            Spacer(modifier = Modifier.height(32.dp))

            // Recommendation Header
            SectionHeader(title = "Recommendation", tanColor = tanColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations List
            RecommendationList()

            Spacer(modifier = Modifier.height(32.dp))

            // Recently Played Header
            SectionHeader(title = "Recently Played", showSeeAll = false)

            Spacer(modifier = Modifier.height(16.dp))

            // Recently Played List
            RecentlyPlayedList()

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FeaturedArtistCard(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Increased height to fit content
            .clip(RoundedCornerShape(24.dp))
            .background(accentColor)
    ) {
        // Text Content
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.6f),
            verticalArrangement = Arrangement.Center
        ) {
            Text("ARTIST", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
            Text(
                "Meet\nPhosphenes",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "The indie band\nbased in Nepal.",
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Explore Now", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }

        // Artist Image
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxHeight()
                .width(195.dp)
                .background(Color.Gray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Artist Image", color = Color.White)
        }
    }
}

@Composable
fun SectionHeader(title: String, tanColor: Color = Color.Gray, showSeeAll: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        if (showSeeAll) {
            Text(
                text = "See all",
                style = TextStyle(color = Color.Gray, fontSize = 13.sp)
            )
        }
    }
}

@Composable
fun RecommendationList() {
    val items = listOf(
        RecommendationItem("SZA - Snooze", Icons.Default.MusicNote),
        RecommendationItem("The Marias - Heavy", Icons.Default.MusicNote),
        RecommendationItem("Wave to e", Icons.Default.MusicNote)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            Column(modifier = Modifier.width(140.dp)) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        tint = Color.Gray
                    )
                    // Play Button Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

data class RecommendationItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun RecentlyPlayedList() {
    val songs = listOf(
        SongItem("1. Dosii - in dreams", "2:56", Icons.Default.MusicNote),
        SongItem("2. Dvwm - fairy", "3:10", Icons.Default.MusicNote),
        SongItem("3. ADOY - Swim", "3:48", Icons.Default.MusicNote)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        songs.forEach { song ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = song.icon,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = song.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                Text(text = song.duration, color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

data class SongItem(val title: String, val duration: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tanColor = Color(0xFFD9B08C)
    val bgColor = Color(0xFF0D0D0D)

    NavigationBar(
        containerColor = bgColor,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("Playlists", Icons.Default.LibraryMusic, 1),
            Triple("Liked", Icons.Default.FavoriteBorder, 2),
            Triple("All Songs", Icons.Default.MusicNote, 3),
            Triple("Account", Icons.Default.Person, 4)
        )

        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = tanColor,
                    selectedTextColor = tanColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    RockstarTheme {
        DashboardBody()
    }
}