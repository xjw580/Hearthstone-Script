package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscriptbase.config.EXTRA_THREAD_POOL
import club.xiaojiawei.controls.Date
import club.xiaojiawei.controls.ProgressModal
import club.xiaojiawei.controls.ico.OfflineIco
import club.xiaojiawei.controls.ico.OnlineIco
import club.xiaojiawei.hsscript.interfaces.StageHook
import club.xiaojiawei.hsscript.statistics.Record
import club.xiaojiawei.hsscript.statistics.RecordDaoEx
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.hsscriptbase.util.isTrue
import javafx.beans.property.DoubleProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.chart.*
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import javafx.util.StringConverter
import java.net.URL
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.max

/**
 * @author 肖嘉威
 * @date 2025/3/13 23:52
 */
class StatisticsController : Initializable, StageHook {

    data class StrategyItem(val id: String?, val name: String)

    @FXML
    protected lateinit var totalDuration: Text

    @FXML
    protected lateinit var totalEXP: Text

    @FXML
    protected lateinit var avgWR: Text

    @FXML
    protected lateinit var totalCount: Text

    @FXML
    protected lateinit var wrPane: StackPane

    @FXML
    protected lateinit var strategyComboBox: ComboBox<StrategyItem>

    @FXML
    protected lateinit var mainProgressModal: ProgressModal

    @FXML
    protected lateinit var unBindIco: OfflineIco

    @FXML
    protected lateinit var bindIco: OnlineIco

    @FXML
    protected lateinit var startDate: Date

    @FXML
    protected lateinit var endDate: Date

    @FXML
    protected lateinit var durationPane: StackPane

    @FXML
    protected lateinit var timePane: StackPane

    @FXML
    protected lateinit var runModePane: StackPane

    @FXML
    protected lateinit var strategyPane: StackPane

    @FXML
    protected lateinit var rootPane: StackPane

    private var progress: DoubleProperty? = null

    private var isInit = false

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        progress = mainProgressModal.show()
    }

    override fun onShowing() {
        if (isInit) return
        isInit = true
        strategyComboBox.converter = object : StringConverter<StrategyItem>() {
            override fun toString(`object`: StrategyItem?): String {
                return `object`?.name ?: ""
            }

            override fun fromString(string: String?): StrategyItem? {
                return null
            }
        }
        startDate.localDate = LocalDate.now()
        endDate.localDate = LocalDate.now()
        startDate.dateProperty().addListener { observable, oldValue, newValue ->
            if (bindIco.isVisible) {
                val noChange = endDate.date == startDate.date
                endDate.dateProperty().set(newValue)
                if (noChange) {
                    loadData()
                }
            } else {
                loadData()
            }
        }
        endDate.dateProperty().addListener { observable, oldValue, newValue ->
            if (bindIco.isVisible) {
                val noChange = startDate.date == startDate.date
                startDate.dateProperty().set(newValue)
                if (noChange) {
                    loadData()
                }
            } else {
                loadData()
            }
        }
        strategyComboBox.items.setAll(StrategyItem(null, "所有"))
        runUI {
            strategyComboBox.selectionModel.selectFirst()
            strategyComboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                val strategyItem = newValue ?: return@addListener
                val strategyId = strategyItem.id
                val records = queryRecord(calcStartDate(), calcEndDate()).filter {
                    var res = false
                    do {
                        val id = it.strategyId ?: break
                        strategyId?.let {
                            if (strategyId == id) {
                                res = true
                            }
                        } ?: let {
                            res = true
                        }
                    } while (false)
                    res
                }
                progress = mainProgressModal.show()
                EXTRA_THREAD_POOL.submit {
                    initTimePane(records)
                    initDurationPane(records)
                    runUI {
                        mainProgressModal.hide(progress)
                    }
                }
            }
        }
        loadData()
    }

    private fun loadData() {
        val startDate: LocalDate = calcStartDate()
        val endDate: LocalDate = calcEndDate()
        loadData(startDate, endDate)
    }

    private fun loadData(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) return

        EXTRA_THREAD_POOL.submit {
            val records = queryRecord(startDate, endDate)
            initStrategyPane(records)
            initRunModePane(records)
            initTimePane(records)
            initDurationPane(records)
            initWRPane(records)
            initSummarizePane(records)
            runUI {
                mainProgressModal.hide(progress)
            }
        }
    }

    private fun calcStartDate(): LocalDate {
        return this.startDate.localDate ?: LocalDate.of(1970, 1, 1)
    }

    private fun calcEndDate(): LocalDate {
        return this.endDate.localDate ?: LocalDate.of(9999, 1, 1)
    }

    private fun queryRecord(startDate: LocalDate, endDate: LocalDate): List<Record> {
        val recordDao = RecordDaoEx.RECORD_DAO
        val minDateTime = LocalDateTime.of(startDate.year, startDate.monthValue, startDate.dayOfMonth, 0, 0)
        val maxDateTime = LocalDateTime.of(endDate.year, endDate.monthValue, endDate.dayOfMonth, 0, 0).plusDays(1)
        val records = recordDao.queryByDateRange(minDateTime, maxDateTime).filter {
            val endTime = it.endTime ?: return@filter false
            endTime.isAfter(minDateTime) && endTime.isBefore(maxDateTime)
        }
        return records
    }

    private fun initStrategyPane(records: List<Record>) {
        if (strategyComboBox.items.size > 1) {
            strategyComboBox.items.remove(1, strategyComboBox.items.size)
        }
        val strategyCount = records.groupBy { it.strategyId }
            .map { (strategyId, list) ->
                val strategyName = list.first().strategyName ?: "未知"
                strategyComboBox.items.add(StrategyItem(strategyId, strategyName))
                PieChart.Data("${strategyName}\t${list.size}次", list.size.toDouble())
            }
        val pieChart = PieChart().apply {
            title = "策略占比"
            strategyCount.isNotEmpty().isTrue {
                data.addAll(strategyCount)
            }
            isClockwise = true
            labelsVisible = true
            startAngle = 90.0
        }
        runUI {
            strategyPane.children.setAll(pieChart)
        }
    }

    private fun initRunModePane(records: List<Record>) {
        val strategyCount = records.groupBy { it.runMode }
            .map { (runMode, list) ->
                val runModeComment = runMode?.comment ?: "未知"
                PieChart.Data("${runModeComment}\t${list.size}次", list.size.toDouble())
            }
        val pieChart = PieChart().apply {
            title = "模式占比"
            strategyCount.isNotEmpty().isTrue {
                data.addAll(strategyCount)
            }
            isClockwise = true
            labelsVisible = true
            startAngle = 90.0
        }
        runUI {
            runModePane.children.setAll(pieChart)
        }
    }

    private fun removeLeadingZeros(value: String): String {
        return value.replaceFirst("^0+(\\d+)".toRegex(), "$1")
    }

    private fun initTimePane(records: List<Record>) {
        // 1️⃣ 统计每个小时的次数
        val pattern = DateTimeFormatter.ofPattern("HH")
        val tempHourCount =
            records.groupingBy { it.endTime?.format(pattern)?.let { it + "点" } ?: "Unknown" }
                .eachCount()
                .toSortedMap(compareBy { it })
        val hourCount = mutableMapOf<String, Int>()
        for (entry in tempHourCount) {
            hourCount[removeLeadingZeros(entry.key)] = entry.value
        }

        // 2️⃣ 创建完整的 0 到 23 小时列表
        val allHours = (0..23).map { "${it}点" }
        val completeHourCount = mutableMapOf<String, Int>()
        allHours.forEach { hour ->
            completeHourCount[hour] = hourCount[hour] ?: 0 // 如果 hourCount 中没有该小时的数据，则默认为 0
        }

        // 3️⃣ 创建 X 轴（小时）
        val xAxis = CategoryAxis().apply {
            label = "时间"
            categories.addAll(allHours) // 设置完整的 X 轴分类
        }

        val maxCount = if (completeHourCount.isEmpty()) 0 else completeHourCount.maxBy { it.value }.value
        val minCount = if (completeHourCount.isEmpty()) 0 else completeHourCount.minBy { it.value }.value
        val diffCount = maxCount - minCount + 2
        var tick = 1
        for (i in 1 until Int.MAX_VALUE) {
            if (diffCount / i <= 10) {
                tick = i
                break
            }
        }

        // 4️⃣ 创建 Y 轴（次数）
        val yAxis = NumberAxis().apply {
            label = "局数"
            tickUnit = tick.toDouble()
            isAutoRanging = false
            lowerBound = max(minCount.toDouble() - 1, 0.0)
            upperBound = maxCount.toDouble() + 1
            isMinorTickVisible = false
        }

        // 5️⃣ 创建折线图
        val lineChart = LineChart(xAxis, yAxis).apply {
            title = "活跃时间分布"
            isLegendVisible = false
        }

        // 6️⃣ 填充数据
        val series = XYChart.Series<String, Number>().apply {
            completeHourCount.forEach { (hour, count) ->
                data.add(XYChart.Data(hour, count))
            }
        }

        lineChart.data.add(series)

        runUI {
            timePane.children.setAll(lineChart)
        }
    }

    fun initDurationPane(records: List<Record>) {
        val durationList = records
            .mapNotNull {
                it.startTime?.let { s -> it.endTime?.let { e -> Duration.between(s, e).toMinutes().toInt() } }
            }
        // 1️⃣ 计算每条记录的时长（分钟）
        val durationCounts = durationList
            .groupingBy { it }  // 以分钟为分组单位
            .eachCount()
            .toSortedMap(compareBy { it })  // 确保分钟排序

        // 2️⃣ 创建 X 轴（时长分钟）
        val xAxis = CategoryAxis().apply {
            label = "时长（分钟）"
        }

        val maxCount = if (durationCounts.isEmpty()) 0 else durationCounts.maxBy { it.value }.value
        val minCount = if (durationCounts.isEmpty()) 0 else durationCounts.minBy { it.value }.value
        val diffCount = maxCount - minCount + 2
        var tick = 1
        for (i in 1 until Int.MAX_VALUE) {
            if (diffCount / i <= 10) {
                tick = i
                break
            }
        }

        // 3️⃣ 创建 Y 轴（次数）
        val yAxis = NumberAxis().apply {
            label = "局数"
            tickUnit = tick.toDouble()
            isAutoRanging = false
            lowerBound = max(minCount.toDouble() - 1, 0.0)
            upperBound = maxCount.toDouble() + 1
            isMinorTickVisible = false
        }


        // 4️⃣ 创建柱状图
        val barChart = BarChart(xAxis, yAxis).apply {
            title = "每局时长统计"
            isLegendVisible = false
        }
        barChart.barGap = 1.0

        // 5️⃣ 填充数据
        val series = XYChart.Series<String, Number>().apply {
            durationCounts.forEach { (minutes, count) ->
                val xyData: XYChart.Data<String, Number> = XYChart.Data(minutes.toString(), count)
                data.add(xyData.apply { node = createDataLabel(count) })
            }
        }

        barChart.data.add(series)

        runUI {
            durationPane.children.setAll(barChart)
        }
    }

    fun initWRPane(records: List<Record>) {
// 1️⃣ 统计胜率
        val strategyWinRates = records
            .groupBy { it.strategyId to it.strategyName }  // 按 strategyId & strategyName 分组
            .mapValues { (_, games) ->
                val totalGames = games.size
                val winGames = games.count { it.result == true }
                val winRate = if (totalGames > 0) (winGames.toDouble() / totalGames) * 100 else 0.0
                winRate
            }

        // 2️⃣ 创建 X 轴（策略名）
        val xAxis = CategoryAxis().apply {
            label = "策略"
        }

        // 3️⃣ 创建 Y 轴（胜率 %）
        val yAxis = NumberAxis(0.0, 100.0, 10.0).apply {
            label = "胜率 (%)"
            isAutoRanging = false
        }

        // 4️⃣ 创建柱状图
        val barChart = BarChart(xAxis, yAxis).apply {
            title = "策略胜率对比"
            isLegendVisible = false
        }

        // 5️⃣ 填充数据
        val series = XYChart.Series<String, Number>().apply {
            name = "WR"  // 胜率 (Win Rate)

            strategyWinRates.forEach { (strategy, winRate) ->
                val strategyName = strategy.second ?: "Unknown"
                val data: XYChart.Data<String, Number> = XYChart.Data(strategyName, winRate)
                data.node = createPercentDataLabel(winRate)
                this.data.add(data)
            }
        }

        barChart.data.add(series)

        runUI { wrPane.children.setAll(barChart) }
    }

    private fun initSummarizePane(records: List<Record>) {
        // 1️⃣ 总计局数
        val totalGames = records.size

        // 2️⃣ 计算胜场数
        val winGames = records.count { it.result == true }

        // 3️⃣ 平均胜率
        val winRate = (if (totalGames > 0) "%.2f".format((winGames.toDouble() / totalGames) * 100) else "0.00") + "%"

        // 4️⃣ 总计经验
        val totalExperience = records.sumOf { it.experience ?: 0 }

        // 5️⃣ 计算总计时长
        val totalMinutes = records.sumOf {
            val duration = if (it.startTime != null && it.endTime != null) {
                Duration.between(it.startTime, it.endTime).toMinutes()
            } else {
                0
            }
            duration
        }

        // 转换为小时和分钟
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        totalCount.text = totalGames.toString()
        avgWR.text = winRate
        totalEXP.text = totalExperience.toString()
        totalDuration.text = "${hours}小时${minutes}分钟"
    }

    private fun createPercentDataLabel(value: Number): StackPane {
        val label = Label("${"%.2f".format(value)}%").apply {
            style = "-fx-font-size: 12px;-fx-padding: 1 0 0 0;"
        }
        return StackPane(label).apply {
            alignment = Pos.TOP_CENTER
        }
    }


    private fun createDataLabel(value: Number, unit: String = ""): StackPane {
        val label = Label(value.toString() + unit).apply {
            style = "-fx-font-size: 12px;-fx-padding: 1 0 0 0;"
        }
        return StackPane(label).apply {
            alignment = Pos.TOP_CENTER
        }
    }

    @FXML
    protected fun changeStatus(mouseEvent: MouseEvent) {
        bindIco.isVisible = !bindIco.isVisible
        unBindIco.isVisible = !unBindIco.isVisible
    }

}
