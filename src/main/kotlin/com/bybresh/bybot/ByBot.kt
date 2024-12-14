package com.bybresh.bybot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val token = System.getenv("BOT_TOKEN").orEmpty()
private lateinit var jda: JDA

val log: Logger = LoggerFactory.getLogger("ByBot")

fun main() {
    log.warn("test")
    if (token.isEmpty()) {
        log.warn("BOT_TOKEN is not set")
    } else if (testDatabaseConnection()) {
        createTableIfDontExist()
        jda = JDABuilder.createDefault(token).build()
        jda.awaitReady()

        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate(CallTimeTracker(), 0, 30, TimeUnit.SECONDS)

        log.info("Bot is ready")
    }
}

private fun testDatabaseConnection(): Boolean {
    return Database.getConnection()?.use { connection ->
        connection.prepareStatement("SELECT 1").execute()
        log.info("Initial Database connection successful")
        true
    } ?: run {
        log.warn("Initial Database connection failed")
        false
    }
}

private fun createTableIfDontExist() {
    Database.getConnection()?.use { connection ->
        connection.prepareStatement(
            """
            CREATE TABLE IF NOT EXISTS users_call_time_global (
                guild_id LONG NOT NULL,
                user_id LONG NOT NULL,
                time INT NOT NULL,
                UNIQUE KEY (guild_id, user_id)
            )
            """.trimIndent()
        ).execute()
        connection.prepareStatement(
            """
            CREATE TABLE IF NOT EXISTS users_call_time_channel (
                guild_id LONG NOT NULL,
                channel_id LONG NOT NULL,
                user_id LONG NOT NULL,
                time INT NOT NULL,
                UNIQUE KEY (guild_id, channel_id, user_id)
            )
            """.trimIndent()
        ).execute()
        log.info("Tables created / confirmed")
    } ?: log.warn("Could not create tables")
}

class CallTimeTracker : Runnable {
    override fun run() {
        Database.getConnection()?.use { connection ->
            for (guild in jda.guilds) {
                for (channel in guild.voiceChannels) {
                    for (member in channel.members) {
                        connection.prepareStatement(
                            """
                            INSERT INTO users_call_time_global (guild_id, user_id, time)
                            VALUES (?, ?, 30)
                            ON DUPLICATE KEY UPDATE time = time + 30
                            """.trimIndent()
                        ).apply {
                            setLong(1, guild.idLong)
                            setLong(2, member.idLong)
                        }.execute()
                        connection.prepareStatement(
                            """
                            INSERT INTO users_call_time_channel (guild_id, channel_id, user_id, time)
                            VALUES (?, ?, ?, 30)
                            ON DUPLICATE KEY UPDATE time = time + 30
                            """.trimIndent()
                        ).apply {
                            setLong(1, guild.idLong)
                            setLong(2, channel.idLong)
                            setLong(3, member.idLong)
                        }.execute()
                    }
                }
            }
            connection.commit()
            log.debug("Call time updated")
        } ?: log.warn("Could not update call time")
    }
}