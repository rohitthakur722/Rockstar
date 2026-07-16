package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rockstar.R
import com.example.rockstar.viewmodel.LibrarySection

@Composable
fun LibrarySectionSelector(
    selectedSection: LibrarySection,
    onSectionSelected: (LibrarySection) -> Unit,
    modifier: Modifier = Modifier
) {
    val sections = LibrarySection.entries
    PrimaryTabRow(
        selectedTabIndex = sections.indexOf(selectedSection),
        modifier = modifier.fillMaxWidth()
    ) {
        sections.forEach { section ->
            Tab(
                selected = section == selectedSection,
                onClick = { onSectionSelected(section) },
                text = {
                    Text(
                        text = when (section) {
                            LibrarySection.Songs -> stringResource(R.string.library_section_songs)
                            LibrarySection.Albums -> stringResource(R.string.library_section_albums)
                            LibrarySection.Artists -> stringResource(R.string.library_section_artists)
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
