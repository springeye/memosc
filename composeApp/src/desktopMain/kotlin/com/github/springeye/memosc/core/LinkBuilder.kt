package com.github.springeye.memosc.core

import java.net.URI


class LinkBuilder {
    protected var scheme: String?
    protected var host: String?
    protected var port: Int
    protected var args: MutableMap<String, String?>  = HashMap<String, String?>()
    protected var path: String?
    protected var hash: String?

    constructor() : this(null, null, 0, java.util.HashMap<String, String>(), null, null)
    protected constructor(other: LinkBuilder) {
        scheme = other.scheme
        host = other.host
        port = other.port
        args = HashMap<String, String?>()
        args.putAll(other.args)
        path = other.path
        hash = other.hash
    }

    protected constructor(
        schema: String?,
        host: String?,
        port: Int,
        args: Map<String, String?>?,
        path: String?,
        hash: String?
    ) {
        scheme = schema
        this.host = host
        this.port = port
        this.args = java.util.HashMap<String, String?>()
        if (args != null) {
            this.args!!.putAll(args)
        }
        this.path = path
        this.hash = hash
    }

    constructor(url: URI) {
        val query: String = url.rawQuery
        for (argLine in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()) {
            if (argLine.isNotEmpty()) {
                val i = argLine.indexOf('=')
                if (i != -1) {
                    args[argLine.substring(0, i)] = argLine.substring(i + 1)
                } else {
                    args[argLine] = null
                }
            }
        }
        scheme = url.getScheme()
        host = url.getHost()
        port = url.getPort()
        path = url.getRawPath()
        hash = url.getRawFragment()
    }

    fun url(url: URI): LinkBuilder {
        return LinkBuilder(url)
    }

    fun scheme(schema: String?): LinkBuilder {
        return LinkBuilder(schema, host, port, args, path, hash)
    }

    fun host(host: String): LinkBuilder {
        if (host.indexOf('/') != -1) {
            throw java.lang.IllegalArgumentException("Wrong host name: $host")
        }
        return LinkBuilder(scheme, host, port, args, path, hash)
    }

    fun port(port: Int): LinkBuilder {
        return LinkBuilder(scheme, host, port, args, path, hash)
    }

    fun hash(hash: String?): LinkBuilder {
        return LinkBuilder(scheme, host, port, args, path, hash)
    }

    fun path(path: String?): LinkBuilder {
        return LinkBuilder(scheme, host, port, args, path, hash)
    }

    @JvmOverloads
    fun arg(name: String, value: Any? = null): LinkBuilder {
        val newArgs: MutableMap<String, String?> = java.util.HashMap<String, String?>(args)
        newArgs[name] = value?.toString()
        return LinkBuilder(scheme, host, port, newArgs, path, hash)
    }

    fun build(): String {
        val buf: java.lang.StringBuilder = java.lang.StringBuilder()
        if (scheme != null) {
            buf.append(scheme)
        }
        buf.append("://")
        if (host != null) {
            buf.append(host)
        }
        if (port > 0 && "https" != scheme) {
            buf.append(':').append(port)
        }
        if (path != null) {
            if (path!![0] != '/') {
                buf.append('/')
            }
            buf.append(path)
        } else if (args!!.size > 0 || hash != null) {
            buf.append('/')
        }
        if (args!!.size > 0) {
            buf.append('?')
            var first = true
            for ((key, value) in args!!) {
                if (!first) {
                    buf.append('&')
                } else {
                    first = false
                }
                buf.append(java.net.URLEncoder.encode(key, "UTF-8"))
                if (value != null && value.length > 0) {
                    buf.append("=").append(java.net.URLEncoder.encode(value, "UTF-8"))
                }
            }
        }
        if (hash != null) {
            buf.append('#').append(hash)
        }
        return buf.toString()
    }

    override fun toString(): String {
        return build()
    }
}