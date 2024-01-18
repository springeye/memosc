package com.github.springeye.memosc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.LazyPagingItems
import com.github.springeye.memosc.model.Memo
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.components.MarkdownComponent
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.markdownColor
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.intellij.markdown.ast.ASTNode
import java.time.format.DateTimeFormatter

@Composable
fun MemoList(lazyPagingItems: LazyPagingItems<Memo>,host:String,onEdit: (memo:Memo?) -> Unit={ },onPin: (memo: Memo) -> Unit,onRemove: (memo: Memo) -> Unit) {
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
                            ItemEditMenu(it,onEdit,onRemove,onPin)
                        }

                        val components = markdownComponents(
                            codeBlock = codeBlockComponent,
                            codeFence = codeFenceBlockComponent
                        )

                        com.mikepenz.markdown.compose.Markdown(
                            it.content, modifier = Modifier.fillMaxWidth(),
                            components = components,
                            colors = markdownColor(codeText = Color.Black),
                            //                                typography = markdownTypography(code = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace, color = Color.Black))
                        )
                        MemoResourceContent(memo = it,host);
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

@Composable
fun ItemEditMenu(memo: Memo,onEdit: (memo:Memo?) -> Unit={ },onRemove: (memo:Memo) -> Unit={ },onPin: (memo:Memo) -> Unit={ }) {
    var expanded by remember { mutableStateOf(false) }
    Box() {
        Icon(Icons.Default.MoreVert, "More", Modifier.clickable {
            expanded = !expanded
        })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("编辑") },
                onClick = {
                    onEdit(memo)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("置顶") },
                onClick = {
                    onPin(memo)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("删除", style = TextStyle(color = Color.Red)) },
                onClick = {
                    onRemove(memo)
                    expanded = false
                },

            )
        }
    }
}