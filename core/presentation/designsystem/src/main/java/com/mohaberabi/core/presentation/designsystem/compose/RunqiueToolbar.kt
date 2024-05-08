package com.mohaberabi.core.presentation.designsystem.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohaberabi.core.presentation.designsystem.R
import com.mohaberabi.core.presentation.designsystem.theme.AnalyticsIcon
import com.mohaberabi.core.presentation.designsystem.theme.ArrowLeftIcon
import com.mohaberabi.core.presentation.designsystem.theme.LogoIcon
import com.mohaberabi.core.presentation.designsystem.theme.Poppins
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueGreen
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.presentation.util.DropDownItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuniqueToolbar(
    modifier: Modifier = Modifier,
    showBackButton: Boolean,
    title: String,
    items: List<DropDownItem> = emptyList(),
    onMenuItemClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    scrollBehaviour: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    startContent: (@Composable () -> Unit)? = null,

    ) {

    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }



    TopAppBar(

        title = {

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                startContent?.invoke()

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = Poppins,
                )
            }
        },
        scrollBehavior = scrollBehaviour,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = ArrowLeftIcon, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        },
        actions = {
            if (items.isNotEmpty()) {
                Box() {

                    DropdownMenu(
                        expanded = isDropDownOpen,
                        onDismissRequest = {
                            isDropDownOpen = false
                        },
                    ) {

                        items.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        onMenuItemClick(index)

                                    }
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {


                                Icon(imageVector = item.icon, contentDescription = item.title)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.title)
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            isDropDownOpen = !isDropDownOpen
                        },
                    ) {

                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(R.string.open_drop_down)
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)

@Preview(showBackground = false)
@Composable
fun RuniquToolBarPreview() {

    RuniqueTheme {

        RuniqueToolbar(
            showBackButton = true,


            items = listOf(
                DropDownItem(AnalyticsIcon, "Analytics")
            ),
            title = "Runique",
            modifier = Modifier.fillMaxWidth(),
            startContent = {
                Icon(
                    imageVector = LogoIcon, tint = RuniqueGreen,
                    modifier = Modifier.size(35.dp),
                    contentDescription = null
                )
            }


        )
    }

}


