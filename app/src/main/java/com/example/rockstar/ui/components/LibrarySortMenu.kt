package com.example.rockstar.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.rockstar.R
import com.example.rockstar.viewmodel.LibrarySortOrder

@Composable
fun LibrarySortMenu(
    selectedSortOrder: LibrarySortOrder,
    isAscending: Boolean,
    onSortOrderSelected: (LibrarySortOrder) -> Unit,
    onToggleDirection: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = stringResource(R.string.cd_sort_library))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LibrarySortOrder.entries.forEach { sortOrder ->
                DropdownMenuItem(
                    text = { Text(sortOrder.label()) },
                    onClick = {
                        onSortOrderSelected(sortOrder)
                        expanded = false
                    },
                    trailingIcon = if (sortOrder == selectedSortOrder) {
                        {
                            Icon(
                                imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null
                            )
                        }
                    } else null
                )
            }
            DropdownMenuItem(
                text = { Text(if (isAscending) stringResource(R.string.sort_descending) else stringResource(R.string.sort_ascending)) },
                onClick = {
                    onToggleDirection()
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun LibrarySortOrder.label(): String = when (this) {
    LibrarySortOrder.Title -> stringResource(R.string.sort_title)
    LibrarySortOrder.Artist -> stringResource(R.string.sort_artist)
    LibrarySortOrder.Album -> stringResource(R.string.sort_album)
    LibrarySortOrder.DateAdded -> stringResource(R.string.sort_date_added)
    LibrarySortOrder.Duration -> stringResource(R.string.sort_duration)
    LibrarySortOrder.Year -> stringResource(R.string.sort_year)
}
