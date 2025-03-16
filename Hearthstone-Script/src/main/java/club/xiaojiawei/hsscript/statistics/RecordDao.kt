package club.xiaojiawei.hsscript.statistics

import club.xiaojiawei.enums.RunModeEnum
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author 肖嘉威
 * @date 2025/3/14 0:17
 */


/**
 * Record实体类，对应records表
 */
data class Record(
    val id: Int? = null,
    val strategyId: String? = null,
    val strategyName: String? = null,
    val runMode: RunModeEnum? = null,  // 原mode字段，已改名为runMode
    val result: Boolean? = null,
    val experience: Int? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null
)

/**
 * records表的数据访问对象 - 仅使用JdbcTemplate
 */
class RecordDao(dbPath: String) {

    private val format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    private val jdbcTemplate: JdbcTemplate

    init {
        // 创建数据源
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.sqlite.JDBC")
        dataSource.url = "jdbc:sqlite:$dbPath"

        // 创建JdbcTemplate
        jdbcTemplate = JdbcTemplate(dataSource)

        // 初始化数据库
        init()
    }

    companion object {
        private const val TABLE_NAME = "records"

        // SQL语句
        private const val SQL_CREATE = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                strategy_id TEXT NOT NULL,
                strategy_name TEXT NOT NULL,
                run_mode TEXT NOT NULL,
                result INTEGER NOT NULL,
                experience INTEGER NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL
            )
        """

        private const val SQL_INSERT = """
            INSERT INTO $TABLE_NAME (
                strategy_id, strategy_name, run_mode, result, 
                experience, start_time, end_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """

        private const val SQL_UPDATE = """
            UPDATE $TABLE_NAME SET 
                strategy_id = ?, 
                strategy_name = ?, 
                run_mode = ?, 
                result = ?,
                experience = ?, 
                start_time = ?, 
                end_time = ?
            WHERE id = ?
        """

        private const val SQL_DELETE = "DELETE FROM $TABLE_NAME WHERE id = ?"

        private const val SQL_FIND_BY_ID = "SELECT * FROM $TABLE_NAME WHERE id = ?"

        private const val SQL_FIND_ALL = "SELECT * FROM $TABLE_NAME"
    }

    /**
     * 初始化数据库，如果表不存在则创建
     */
    private fun init() {
        jdbcTemplate.execute(SQL_CREATE)
    }

    /**
     * 将ResultSet行映射到Record对象
     */
    private val recordMapper = RowMapper<Record> { rs: ResultSet, _: Int ->
        Record(
            id = rs.getInt("id"),
            strategyId = rs.getString("strategy_id"),
            strategyName = rs.getString("strategy_name"),
            runMode = RunModeEnum.fromString(rs.getString("run_mode")),
            result = rs.getBoolean("result"),
            experience = rs.getInt("experience"),
            startTime = rs.getString("start_time")?.let { LocalDateTime.parse(it, format) },
            endTime = rs.getString("end_time")?.let { LocalDateTime.parse(it, format) }
        )
    }

    /**
     * 插入新记录
     * @param record 要插入的记录
     * @return 带有ID的已插入记录
     */
    fun insert(record: Record): Record {
        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({ connection: Connection ->
            val ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, record.strategyId)
            ps.setString(2, record.strategyName)
            ps.setString(3, record.runMode?.name)
            ps.setBoolean(4, record.result?:false)
            ps.setInt(5, record.experience ?: 0)
            ps.setString(6, format.format(record.startTime))
            ps.setString(7, format.format(record.endTime))
            ps
        }, keyHolder)

        val id = keyHolder.key?.toInt() ?: throw RuntimeException("获取生成的ID失败")

        return record.copy(id = id)
    }

    /**
     * 更新现有记录
     * @param record 要更新的记录
     * @return 受影响的行数
     */
    fun update(record: Record): Int {
        return jdbcTemplate.update(
            SQL_UPDATE,
            record.strategyId,
            record.strategyName,
            record.runMode,  // 使用runMode代替mode
            record.result,
            record.experience,
            record.startTime,
            record.endTime,
            record.id
        )
    }

    /**
     * 通过ID删除记录
     * @param id 要删除的记录ID
     * @return 受影响的行数
     */
    fun deleteById(id: Int): Int {
        return jdbcTemplate.update(SQL_DELETE, id)
    }

    /**
     * 通过ID查找记录
     * @param id 要查找的记录ID
     * @return 找到的记录或null
     */
    fun findById(id: Int): Record? {
        return try {
            jdbcTemplate.queryForObject(SQL_FIND_BY_ID, recordMapper, id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 查找所有记录
     * @return 所有记录列表
     */
    fun findAll(): List<Record> {
        return jdbcTemplate.query(SQL_FIND_ALL, recordMapper)
    }

    /**
     * 根据提供的记录的非空字段查询记录
     * @param record 带有查询条件的记录
     * @return 匹配的记录列表
     */
    fun query(record: Record? = null): List<Record> {
        val conditions = ArrayList<String>()
        val params = ArrayList<Any>()

        record?.let {
            record.id?.let {
                conditions.add("id = ?")
                params.add(it)
            }

            record.strategyId?.let {
                conditions.add("strategy_id = ?")
                params.add(it)
            }

            record.strategyName?.let {
                conditions.add("strategy_name = ?")
                params.add(it)
            }

            record.runMode?.let {  // 使用runMode代替mode
                conditions.add("run_mode = ?")
                params.add(it)
            }

            record.result?.let {
                conditions.add("result = ?")
                params.add(it)
            }

            record.experience?.let {
                conditions.add("experience = ?")
                params.add(it)
            }

            record.startTime?.let {
                conditions.add("start_time = ?")
                params.add(it)
            }

            record.endTime?.let {
                conditions.add("end_time = ?")
                params.add(it)
            }
        }

        var sql = SQL_FIND_ALL
        if (conditions.isNotEmpty()) {
            sql += " WHERE " + conditions.joinToString(" AND ")
        }

        return jdbcTemplate.query(sql, recordMapper, *params.toArray())
    }

    /**
     * 更复杂查询示例 - 根据日期范围查找记录
     * @param startDate 开始日期（ISO 8601格式）
     * @param endDate 结束日期（ISO 8601格式）
     * @return 匹配的记录列表
     */
    fun findByDateRange(startDate: String, endDate: String): List<Record> {
        val sql = "$SQL_FIND_ALL WHERE start_time >= ? AND end_time <= ?"
        return jdbcTemplate.query(sql, recordMapper, startDate, endDate)
    }

    /**
     * 更复杂查询示例 - 根据策略和结果查找记录
     * @param strategyId 策略ID
     * @param result 结果（例如"成功"或"失败"）
     * @return 匹配的记录列表
     */
    fun findByStrategyAndResult(strategyId: Int, result: String): List<Record> {
        val sql = "$SQL_FIND_ALL WHERE strategy_id = ? AND result = ?"
        return jdbcTemplate.query(sql, recordMapper, strategyId, result)
    }
}