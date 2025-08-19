package com.example.mycomposeapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class CardItem(
    val id: Int,
    val title: String,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCarouselScreen(
    modifier: Modifier = Modifier
) {
    val cardItems = remember {
        listOf(
            CardItem(1, "Card 1", "This is the first card with some sample content", Color(0xFF6200EE)),
            CardItem(2, "Card 2", "This is the second card with different content", Color(0xFF03DAC6)),
            CardItem(3, "Card 3", "This is the third card with more information", Color(0xFFFF6200)),
            CardItem(4, "Card 4", "This is the fourth card with unique content", Color(0xFF009688)),
            CardItem(5, "Card 5", "This is the fifth card with special details", Color(0xFFE91E63)),
            CardItem(6, "Card 6", "This is the sixth card with final content", Color(0xFF9C27B0))
        )
    }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val totalCards = cardItems.size
    
    // Calculate current card index based on scroll position
    val currentCardIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            
            if (visibleItems.isEmpty()) {
                0
            } else {
                // Find the item that's most visible (has the largest visible area)
                visibleItems.maxByOrNull { item ->
                    val itemStart = maxOf(item.offset, layoutInfo.viewportStartOffset)
                    val itemEnd = minOf(item.offset + item.size, layoutInfo.viewportEndOffset)
                    maxOf(0, itemEnd - itemStart) // visible area
                }?.index ?: 0
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Card Carousel",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Cards container - one card per screen
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(cardItems) { _, item ->
                CardTile(
                    item = item,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Dot indicator with arrows
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left arrow for indicator
            IconButton(
                onClick = {
                    if (currentCardIndex > 0) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(currentCardIndex - 1)
                        }
                    }
                },
                enabled = currentCardIndex > 0
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = if (currentCardIndex > 0) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            
            // Dot indicator
            DotIndicator(
                totalCards = totalCards,
                currentCardIndex = currentCardIndex,
                onCardSelected = { cardIndex ->
                    coroutineScope.launch {
                        listState.animateScrollToItem(cardIndex)
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            // Right arrow for indicator
            IconButton(
                onClick = {
                    if (currentCardIndex < totalCards - 1) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(currentCardIndex + 1)
                        }
                    }
                },
                enabled = currentCardIndex < totalCards - 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = if (currentCardIndex < totalCards - 1) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Current card info
        Text(
            text = "Card ${currentCardIndex + 1} of $totalCards",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CardTile(
    item: CardItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { /* Handle card click */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(item.color.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card header with colored circle
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(item.color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Card content
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                lineHeight = 16.sp,
                modifier = Modifier.weight(1f)
            )
            
            // Card footer
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DotIndicator(
    totalCards: Int,
    currentCardIndex: Int,
    onCardSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalCards) { index ->
            val isSelected = index == currentCardIndex
            Box(
                modifier = Modifier
                    .size(32.dp) // Adequate touch target
                    .semantics {
                        role = Role.Button
                        contentDescription = if (isSelected) {
                            "Card ${index + 1} of $totalCards, currently selected"
                        } else {
                            "Go to card ${index + 1} of $totalCards"
                        }
                    }
                    .clickable { onCardSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                // Selection background circle (larger)
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }
                
                // Dot indicator (smaller, centered)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardCarouselScreenPreview() {
    MaterialTheme {
        CardCarouselScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CardTilePreview() {
    MaterialTheme {
        CardTile(
            item = CardItem(
                id = 1,
                title = "Sample Card",
                description = "This is a sample card with some content to preview",
                color = Color(0xFF6200EE)
            ),
            modifier = Modifier.width(140.dp)
        )
    }
}
