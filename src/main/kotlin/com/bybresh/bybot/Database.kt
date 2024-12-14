package com.bybresh.bybot

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object Database {

    private val dataSource: HikariDataSource? by lazy {
        try {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = "jdbc:mysql://" + System.getenv("DB_URL").orEmpty() + "/" + System.getenv("DB_NAME").orEmpty()
                username = System.getenv("DB_USER").orEmpty()
                password = System.getenv("DB_PASSWORD").orEmpty()
                maximumPoolSize = 10
                isAutoCommit = false
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            }
            HikariDataSource(hikariConfig)
        } catch (e: Exception) {
            null
        }
    }

    fun getConnection(): Connection? = dataSource?.connection ?: run {
        log.warn("Database connection failed")
        null
    }
}