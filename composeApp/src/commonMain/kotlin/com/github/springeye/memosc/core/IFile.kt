package com.github.springeye.memosc.core
interface IFile{
    fun readBytes():ByteArray
    val mimeType:String
    val filename:String
}
expect fun createIFile(path:String):IFile