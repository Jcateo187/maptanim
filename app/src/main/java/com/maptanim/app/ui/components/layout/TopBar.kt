package com.maptanim.app.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maptanim.app.ui.components.avatar.ProfileAvatar
import com.maptanim.app.ui.components.floating.FloatingNotification
import com.maptanim.app.ui.components.floating.FloatingSettings
import com.maptanim.app.ui.components.top.FarmName
import com.maptanim.app.ui.components.top.MiniStatistics

@Composable
fun TopBar(

    modifier: Modifier = Modifier,

    onProfileClick: () -> Unit = {},

    onNotificationClick: () -> Unit = {},

    onSettingsClick: () -> Unit = {}

) {

    Row(

        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),

        horizontalArrangement = Arrangement.SpaceBetween,

        verticalAlignment = Alignment.CenterVertically

    ) {

        ProfileAvatar(
            onClick = onProfileClick
        )

        FarmName()

        MiniStatistics()

        Row(

            modifier = Modifier.padding(end = 8.dp),

            horizontalArrangement = Arrangement.spacedBy(20.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            FloatingNotification(
                onClick = onNotificationClick
            )

            FloatingSettings(
                onClick = onSettingsClick
            )

        }

    }

}