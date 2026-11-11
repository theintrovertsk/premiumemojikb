package com.sgcodexs.premiumkeyboard

import android.content.Context
import androidx.room.*

@Entity(tableName = "saved_emojis")
data class SavedEmoji(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val documentId: Long,
    val fallbackText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface EmojiDao {
    @Query("SELECT * FROM saved_emojis ORDER BY timestamp DESC")
    fun getAllEmojis(): List<SavedEmoji>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEmoji(emoji: SavedEmoji)
}

@Database(entities = [SavedEmoji::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emoji_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
