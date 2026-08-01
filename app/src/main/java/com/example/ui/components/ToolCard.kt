package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiTool
import com.example.data.model.Language
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun ToolCard(
    tool: AiTool,
    language: Language,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (language == Language.BN) tool.titleBn else tool.titleEn
    val description = if (language == Language.BN) tool.descriptionBn else tool.descriptionEn

    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        label = "heartColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("tool_card_${tool.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Icon + Tag Badge + Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon Container
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryIndigo.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getMaterialIconByName(tool.iconName),
                        contentDescription = tool.titleEn,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Tag Badge
                    val tag = tool.tags.firstOrNull() ?: "popular"
                    val (tagLabel, tagColor) = when (tag) {
                        "trending" -> Pair(if (language == Language.BN) "ট্রেন্ডিং" else "Trending", WarningAmber)
                        "newest" -> Pair(if (language == Language.BN) "নতুন" else "New", SuccessGreen)
                        else -> Pair(if (language == Language.BN) "জনপ্রিয়" else "Popular", PrimaryCyan)
                    }

                    Surface(
                        shape = CircleShape,
                        color = tagColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = tagLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("favorite_toggle_${tool.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = heartColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tool Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tool Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getMaterialIconByName(name: String): ImageVector {
    return when (name) {
        "Edit" -> Icons.Default.Edit
        "Article" -> Icons.Default.Article
        "Email" -> Icons.Default.Email
        "AutoStories" -> Icons.Default.AutoStories
        "HistoryEdu" -> Icons.Default.HistoryEdu
        "Spellcheck" -> Icons.Default.Spellcheck
        "Badge" -> Icons.Default.Badge
        "MailOutline" -> Icons.Default.MailOutline
        "FormatQuote" -> Icons.Default.FormatQuote
        "Autorenew" -> Icons.Default.Autorenew
        "ShoppingBag" -> Icons.Default.ShoppingBag
        "School" -> Icons.Default.School
        "MenuBook" -> Icons.Default.MenuBook
        "Functions" -> Icons.Default.Functions
        "Compress" -> Icons.Default.Compress
        "CheckCircleOutline" -> Icons.Default.CheckCircleOutline
        "Quiz" -> Icons.Default.Quiz
        "Style" -> Icons.Default.Style
        "CalendarToday" -> Icons.Default.CalendarToday
        "Slideshow" -> Icons.Default.Slideshow
        "NoteAlt" -> Icons.Default.NoteAlt
        "Psychology" -> Icons.Default.Psychology
        "PlayArrow" -> Icons.Default.PlayArrow
        "VideoCameraFront" -> Icons.Default.VideoCameraFront
        "Lightbulb" -> Icons.Default.Lightbulb
        "Title" -> Icons.Default.Title
        "Tag" -> Icons.Default.Tag
        "Description" -> Icons.Default.Description
        "SmartDisplay" -> Icons.Default.SmartDisplay
        "Subscriptions" -> Icons.Default.Subscriptions
        "Anchor" -> Icons.Default.Anchor
        "Numbers" -> Icons.Default.Numbers
        "Mic" -> Icons.Default.Mic
        "BusinessCenter" -> Icons.Default.BusinessCenter
        "Store" -> Icons.Default.Store
        "Campaign" -> Icons.Default.Campaign
        "RecordVoiceOver" -> Icons.Default.RecordVoiceOver
        "AdsClick" -> Icons.Default.AdsClick
        "PhotoCamera" -> Icons.Default.PhotoCamera
        "Assignment" -> Icons.Default.Assignment
        "ReceiptLong" -> Icons.Default.ReceiptLong
        "MarkEmailRead" -> Icons.Default.MarkEmailRead
        "HelpOutline" -> Icons.Default.HelpOutline
        "QuestionAnswer" -> Icons.Default.QuestionAnswer
        "Code" -> Icons.Default.Code
        "BugReport" -> Icons.Default.BugReport
        "FindInPage" -> Icons.Default.FindInPage
        "Html" -> Icons.Default.Html
        "Css" -> Icons.Default.Css
        "Javascript" -> Icons.Default.Javascript
        "Terminal" -> Icons.Default.Terminal
        "Storage" -> Icons.Default.Storage
        "Pattern" -> Icons.Default.Pattern
        "Api" -> Icons.Default.Api
        "Share" -> Icons.Default.Share
        "Facebook" -> Icons.Default.Facebook
        "CameraAlt" -> Icons.Default.CameraAlt
        "WorkHistory" -> Icons.Default.WorkHistory
        "AccountCircle" -> Icons.Default.AccountCircle
        "ChatBubbleOutline" -> Icons.Default.ChatBubbleOutline
        "VideoLibrary" -> Icons.Default.VideoLibrary
        "Poll" -> Icons.Default.Poll
        "EmojiEmotions" -> Icons.Default.EmojiEmotions
        "TrendingUp" -> Icons.Default.TrendingUp
        "Image" -> Icons.Default.Image
        "Brush" -> Icons.Default.Brush
        "Palette" -> Icons.Default.Palette
        "Wallpaper" -> Icons.Default.Wallpaper
        "FeaturedVideo" -> Icons.Default.FeaturedVideo
        "ShoppingBasket" -> Icons.Default.ShoppingBasket
        "CropOriginal" -> Icons.Default.CropOriginal
        "Face" -> Icons.Default.Face
        "StickyNote2" -> Icons.Default.StickyNote2
        "Apps" -> Icons.Default.Apps
        "Work" -> Icons.Default.Work
        "CompassCalibration" -> Icons.Default.CompassCalibration
        "AttachMoney" -> Icons.Default.AttachMoney
        "Person" -> Icons.Default.Person
        "FolderSpecial" -> Icons.Default.FolderSpecial
        "Mail" -> Icons.Default.Mail
        "AltRoute" -> Icons.Default.AltRoute
        "TrackChanges" -> Icons.Default.TrackChanges
        "ContactSupport" -> Icons.Default.ContactSupport
        "Schedule" -> Icons.Default.Schedule
        "FlightTakeoff" -> Icons.Default.FlightTakeoff
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "Restaurant" -> Icons.Default.Restaurant
        "AccountBalanceWallet" -> Icons.Default.AccountBalanceWallet
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "SoupKitchen" -> Icons.Default.SoupKitchen
        "CardGiftcard" -> Icons.Default.CardGiftcard
        "Checklist" -> Icons.Default.Checklist
        "Today" -> Icons.Default.Today
        "Celebration" -> Icons.Default.Celebration
        "SentimentSatisfied" -> Icons.Default.SentimentSatisfied
        "SentimentVerySatisfied" -> Icons.Default.SentimentVerySatisfied
        "Help" -> Icons.Default.Help
        "Casino" -> Icons.Default.Casino
        "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
        "AutoAwesome" -> Icons.Default.AutoAwesome
        "Face5" -> Icons.Default.Face5
        "SentimentSatisfiedAlt" -> Icons.Default.SentimentSatisfiedAlt
        "VideogameAsset" -> Icons.Default.VideogameAsset
        "Favorite" -> Icons.Default.Favorite
        "AutoMode" -> Icons.Default.AutoMode
        else -> Icons.Default.AutoAwesome
    }
}
