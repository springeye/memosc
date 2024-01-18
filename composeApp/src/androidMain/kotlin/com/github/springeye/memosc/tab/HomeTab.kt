package com.github.springeye.memosc.tab

import AppModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownColor
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.github.springeye.memosc.components.CardItem
import components.ITextField
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import model.Memo
import model.Resource
import org.intellij.markdown.ast.ASTNode
import ui.home.MemoModel
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.min

object HomeTab : Tab {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {

        val model = getScreenModel<MemoModel>()
        val lazyPagingItems = model.pager.flow.collectAsLazyPagingItems()
        val memos = model.memos
        val refreshing = lazyPagingItems.loadState.refresh == LoadStateLoading
        val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
            lazyPagingItems.refresh()
        })
            Column(modifier = Modifier.fillMaxWidth().pullRefresh(pullRefreshState)) {
                PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.CenterHorizontally))
                Box(Modifier.padding(top = 20.dp)) {
                    EditMemo()
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(lazyPagingItems.itemCount) { index ->
                        val it = lazyPagingItems[index]!!
                        Box(Modifier.padding(top = 10.dp)) {
                            CardItem(
                                radius = 10.dp,
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = Color.White,
                                borderWidth = 1.dp,
                                hoverBorderColor = Color(0xccCCCCCC),
                            ) {
                                Column {
                                    val dateTime =
                                        Instant.fromEpochSeconds(it.createdTs)
                                            .toLocalDateTime(
                                                TimeZone.currentSystemDefault()
                                            ).toJavaLocalDateTime()
                                            .format(DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss"))
                                    Row(modifier = Modifier) {
                                        Text(
                                            dateTime,
                                            style = TextStyle.Default.copy(
                                                color = Color(
                                                    156,
                                                    163,
                                                    175
                                                )
                                            )
                                        )
                                        Spacer(Modifier.weight(1F))
                                        ItemEditMenu(it)
                                    }

                                    val components = markdownComponents(
                                        codeBlock = codeBlockComponent,
                                        codeFence = codeFenceBlockComponent
                                    )

                                    Markdown(
                                        it.content, modifier = Modifier.fillMaxWidth(),
                                        components = components,
                                        colors = markdownColor(codeText = Color.Black),
                                        //                                typography = markdownTypography(code = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace, color = Color.Black))
                                    )
                                    MemoResourceContent(memo = it);
                                }
                            }
                        }
                    }
                }

            }


    }

    private val codeBlockComponent: MarkdownComponent = {
        MarkdownCodeBlock(it.content, it.node)
    }
    private val codeFenceBlockComponent: MarkdownComponent = {
        MarkdownCodeFenceBlock(it.content, it.node)
    }

    @Composable
    internal fun MarkdownCodeFenceBlock(
        content: String,
        node: ASTNode
    ) {
        // CODE_FENCE_START, FENCE_LANG, {content}, CODE_FENCE_END
        if (node.children.size >= 3) {

            val start = node.children[2].startOffset
            val end = node.children[node.children.size - 2].endOffset
            val langStart = node.children[1].startOffset
            val langEnd = node.children[1].endOffset
            val lang = content.substring(langStart, langEnd)
            MarkdownCode(content.subSequence(start, end).toString().replaceIndent(), lang)
        } else {
            // invalid code block, skipping
        }
    }

    @Composable
    internal fun MarkdownCodeBlock(
        content: String,
        node: ASTNode
    ) {
        val start = node.children[0].startOffset
        val end = node.children[node.children.size - 1].endOffset
        MarkdownCode(content.subSequence(start, end).toString().replaceIndent(), "txt")
    }

    @Composable
    private fun MarkdownCode(
        code: String,
        lang: String,
        style: TextStyle = LocalMarkdownTypography.current.code
    ) {
        val color = LocalMarkdownColors.current.codeText
        var language = lang
        language = language.replace("^golang$".toRegex(), "go")
//        language=language.replace("^java$".toRegex(),"kotlin")
        val backgroundCodeColor = LocalMarkdownColors.current.codeBackground
        Surface(
            color = backgroundCodeColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
        ) {

            val syn = SyntaxLanguage.getByName(language)
            if (syn != null) {
                val highlights = Highlights.Builder()
                    .code(code)
                    .theme(SyntaxThemes.darcula())
                    .language(syn)
                    .build()
                    .getHighlights()
                Text(
                    text = buildAnnotatedString {
                        append(code)
                        highlights
                            .filterIsInstance<ColorHighlight>()
                            .forEach {
                                addStyle(
                                    SpanStyle(color = Color(it.rgb).copy(alpha = 1f)),
                                    start = it.location.start,
                                    end = it.location.end,
                                )
                            }

                        highlights
                            .filterIsInstance<BoldHighlight>()
                            .forEach {
                                addStyle(
                                    SpanStyle(fontWeight = FontWeight.Bold),
                                    start = it.location.start,
                                    end = it.location.end,
                                )
                            }
                    },
                    color = color,
                    modifier = Modifier.horizontalScroll(rememberScrollState()).padding(8.dp),
                    style = style
                )
            } else {

                val parser = remember { PrettifyParser() }
                var themeState by remember { mutableStateOf(CodeThemeType.Monokai) }
                val theme = remember(themeState) { themeState.theme }
                val parsedCode: AnnotatedString = remember {
                    if (!parser.isSupport(language)) {
                        AnnotatedString(code)
                    } else {
                        parseCodeAsAnnotatedString(
                            parser = parser,
                            theme = theme,
                            lang = language,
                            code = code
                        )
                    }
                }

                Text(
                    parsedCode,
                    color = color,
                    modifier = Modifier.horizontalScroll(rememberScrollState()).padding(8.dp),
                    style = style
                )
            }

        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val title = "主页"
            val icon = rememberVectorPainter(Icons.Outlined.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Preview
    @Composable
    fun EditMemo() {
        val model = getScreenModel<MemoModel>()
        val settings by getScreenModel<AppModel>().state.collectAsState()
        CardItem(
            radius = 10.dp,
            borderColor = Color(0xccCCCCCC),
            borderWidth = 1.dp,
            paddingValues = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
            hoverBorderColor = Color(0xccCCCCCC),
        ) {
            Column(Modifier.fillMaxWidth().wrapContentHeight()) {
                ITextField(
                    model.content,
                    onValueChange = {
                        model.content = it
                    },
                    minLines = 2,
                    maxLines = 10,
                    placeholder = {
                        Text(
                            "任何想法",
                            style = TextStyle.Default.copy(color = Color(156, 163, 175))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(), // Here I have decreased the height
                    shape = RoundedCornerShape(0.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                        ),

                    )
                var showFilePicker by remember { mutableStateOf(false) }
                Row {
                    Icon(Icons.Default.Tag, "")
                    Icon(Icons.Default.Photo, "", Modifier.padding(start = 10.dp).clickable {
                        showFilePicker = true
                    })
                    Icon(Icons.Default.Attachment, "", Modifier.padding(start = 10.dp))
                }
                Row {


                    val fileType = listOf("jpg", "png")
                    FilePicker(show = showFilePicker, fileExtensions = fileType) { file ->
                        showFilePicker = false
//                        println(file?.path)
                        file?.path?.let { model.upload(it) }
                        // do something with the file
                    }
                }
                Divider(Modifier.padding(top = 10.dp, bottom = 10.dp))

                FlowRow() {
                    model.resources.forEach { item ->
                        AnimatedContent(targetState = item) {s->
                            ResourceItem(s, settings.host)
                        }
                    }
                }
                Row(Modifier.padding(vertical = 10.dp)) {
                    VisibilityButton()
                    Spacer(Modifier.weight(1f))
                    SubmitButton(model.content.isNotEmpty())
                }

            }
        }
    }

    @Composable
    fun ResourceItem(item: Resource, host: String) {
        val model = getScreenModel<MemoModel>()

        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 5.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KamelImage(
                asyncPainterResource(item.uri(host)),
                "",
                Modifier.size(20.dp)

            )
            Text(
                item.filename,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 5.dp)
            )
            Icon(Icons.Default.Close, "", modifier = Modifier.clickable {
                scope.launch {
                    model.removeResource(item)

                }

            })
        }
    }

    @Composable
    fun SubmitButton(enable: Boolean) {
        val model = getScreenModel<MemoModel>()
        Button(
            enabled = enable,
            onClick = {
                model.submit()
            },
            contentPadding = PaddingValues(horizontal = 10.dp),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                .height(30.dp)
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("保存")
                Icon(
                    Icons.Filled.Send, "",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 5.dp)
                )
            }
        }
    }

    @Composable
    fun MemoResourceIcons(memo: Memo) {
        val settings by getScreenModel<AppModel>().state.collectAsState()
        memo.resourceList?.let { resourceList ->
            val cols = min(3, resourceList.size)
            val imageList = resourceList.filter { it.type.startsWith("image/") }
            if (imageList.isNotEmpty()) {
                val rows = ceil(imageList.size.toFloat() / cols).toInt()
                for (rowIndex in 0 until rows) {
                    Row {
                        for (colIndex in 0 until cols) {
                            val index = rowIndex * cols + colIndex
                            if (index < imageList.size) {
                                Box(modifier = Modifier.fillMaxWidth(1f / (cols - colIndex))) {
                                    val uri = imageList[index].uri(settings.host)
                                    KamelImage(
                                        asyncPainterResource(uri),
                                        onLoading = {
                                            Text("加载中")
                                        },
                                        onFailure = {
                                            it.printStackTrace()
                                            Text("加载失败")
                                        },
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.fillMaxWidth(1f / cols))
                            }
                        }
                    }
                }
            }
            resourceList.filterNot { it.type.startsWith("image/") }.forEach { resource ->
                Attachment(resource)
            }
        }
    }

    @Composable
    fun MemoResourceContent(memo: Memo) {
        val settings by getScreenModel<AppModel>().state.collectAsState()
        memo.resourceList?.let { resourceList ->
            val cols = min(3, resourceList.size)
            val imageList = resourceList.filter { it.type.startsWith("image/") }
            if (imageList.isNotEmpty()) {
                val rows = ceil(imageList.size.toFloat() / cols).toInt()
                for (rowIndex in 0 until rows) {
                    Row {
                        for (colIndex in 0 until cols) {
                            val index = rowIndex * cols + colIndex
                            if (index < imageList.size) {
                                Box(modifier = Modifier.fillMaxWidth(1f / (cols - colIndex))) {
                                    val uri = imageList[index].uri(settings.host)
                                    KamelImage(
                                        asyncPainterResource(uri),
                                        onLoading = {
                                            Text("加载中")
                                        },
                                        onFailure = {
                                            it.printStackTrace()
                                            Text("加载失败")
                                        },
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.fillMaxWidth(1f / cols))
                            }
                        }
                    }
                }
            }
            resourceList.filterNot { it.type.startsWith("image/") }.forEach { resource ->
                Attachment(resource)
            }
        }
    }

    @Composable
    fun Attachment(
        resource: Resource
    ) {
        val uriHandler = LocalUriHandler.current
        val settings by getScreenModel<AppModel>().state.collectAsState()

        AssistChip(
            modifier = Modifier.padding(bottom = 10.dp),
            onClick = {
                uriHandler.openUri(resource.uri(settings.host))
            },
            label = { Text(resource.filename) },
            leadingIcon = {
                androidx.compose.material3.Icon(
                    Icons.Outlined.Attachment,
                    contentDescription = "附件",
                    Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }

    @Composable
    fun VisibilityButton() {
        var expanded by remember { mutableStateOf(false) }
        Box(Modifier.clip(RoundedCornerShape(5.dp))
            .background(Color(0x99eeeeee))
            .clickable {
                expanded = !expanded
            }.padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                val tint = Color(156, 163, 175)
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "More",
                    modifier = Modifier.size(20.dp),
                    tint = tint
                )
                Text(
                    "私有",
                    style = TextStyle.Default.copy(color = tint),
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "More",
                    modifier = Modifier.size(20.dp),
                    tint = tint


                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Load") },
                    onClick = { }
                )
                DropdownMenuItem(
                    text = { Text("Save") },
                    onClick = { }
                )
            }
        }
    }

    @Composable
    fun ItemEditMenu(memo: Memo) {
        var expanded by remember { mutableStateOf(false) }
        val model = getScreenModel<MemoModel>()
        Box() {
            Icon(Icons.Default.MoreVert, "More", Modifier.clickable {
                expanded = !expanded
            })
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        model.setEdit(memo)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Save") },
                    onClick = { }
                )
            }
        }
    }
}