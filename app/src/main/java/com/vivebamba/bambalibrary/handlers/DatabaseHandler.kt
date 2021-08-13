package com.vivebamba.bambalibrary.handlers

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.vivebamba.bambalibrary.Message
import java.util.*
import kotlin.collections.ArrayList


class DatabaseHandler(
    context: Context, name: String?,
    factory: SQLiteDatabase.CursorFactory?, version: Int
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MESSAGES_TABLE = ("CREATE TABLE " +
                TABLE_MESSAGES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_USER_CELLPHONE + " TEXT," + COLUMN_MESSAGE + " TEXT," + COLUMN_TIME + " TEXT" + ")")
        db.execSQL(CREATE_MESSAGES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    fun addMessage(message: Message) {
        val values = ContentValues()
        values.put(COLUMN_USER_CELLPHONE, message.user)
        values.put(COLUMN_MESSAGE, message.message)
        values.put(COLUMN_TIME, message.time.toString())

        val db = this.writableDatabase

        db.insert(TABLE_MESSAGES, null, values)
        db.close()
    }

    fun getAllMessages(): ArrayList<Message> {
        val messages = ArrayList<Message>()

        val MESSAGES_SELECT_QUERY = java.lang.String.format(
            "SELECT * FROM %s",
            TABLE_MESSAGES
        )

        val db = readableDatabase
        val cursor = db.rawQuery(MESSAGES_SELECT_QUERY, null)
        try {
            if (cursor!!.moveToFirst()) {
                do {
                    val messageUser = cursor.getString(cursor.getColumnIndex(COLUMN_USER_CELLPHONE))
                    val messageText = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE))
                    val messageTime = cursor.getString(cursor.getColumnIndex(COLUMN_TIME))
                    val message = Message(messageUser, messageText, messageTime.toLong())
                    messages.add(message)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error while trying to get posts from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return messages
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "bambaMessagesDB.db"
        const val TABLE_MESSAGES = "messages"

        const val COLUMN_ID = "_id"
        const val COLUMN_USER_CELLPHONE = "user_cellphone"
        const val COLUMN_MESSAGE = "message"
        const val COLUMN_TIME = "send_at"
    }
}