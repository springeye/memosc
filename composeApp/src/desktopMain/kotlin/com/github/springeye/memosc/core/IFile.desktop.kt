package com.github.springeye.memosc.core

import java.io.File

actual fun createIFile(path: String): IFile {
    return IFileImpl(path)
}
internal class IFileImpl(val path:String): IFile {
    val file=File(path)
    override fun readBytes(): ByteArray {
        return file.readBytes()
    }

    override val mimeType: String
        get() = java.nio.file.Files.probeContentType(file.toPath())
    override val filename: String
        get() = file.name

}