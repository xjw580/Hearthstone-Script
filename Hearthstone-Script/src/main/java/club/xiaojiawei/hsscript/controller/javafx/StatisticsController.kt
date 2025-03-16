package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.controls.Date
import club.xiaojiawei.controls.ico.OfflineIco
import club.xiaojiawei.controls.ico.OnlineIco
import club.xiaojiawei.hsscript.statistics.Record
import club.xiaojiawei.hsscript.statistics.RecordDaoEx
import club.xiaojiawei.util.isTrue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.chart.*
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
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
class StatisticsController : Initializable {

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

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
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
        loadData()
    }

    private fun loadData() {
        val startDate: LocalDate = this.startDate.localDate ?: LocalDate.of(1970, 1, 1)
        val endDate: LocalDate = this.endDate.localDate ?: LocalDate.of(9999, 1, 1)
        loadData(startDate, endDate)
    }

    private fun loadData(startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) return

        val recordDao = RecordDaoEx.getRecordDao(LocalDate.now())
        val minDateTime = LocalDateTime.of(startDate.year, startDate.monthValue, startDate.dayOfMonth, 0, 0)
        val maxDateTime = LocalDateTime.of(endDate.year, endDate.monthValue, endDate.dayOfMonth, 0, 0).plusDays(1)
        val records = recordDao.query().filter {
            val endTime = it.endTime ?: return@filter false
            endTime.isAfter(minDateTime) && endTime.isBefore(maxDateTime)
        }
        initStrategyPane(records)
        initRunModePane(records)
        initTimePane(records)
        initDurationPane(records)
    }

    private fun initStrategyPane(records: List<Record>) {
        val strategyCount = records.groupBy { it.strategyId }
            .map { (strategyId, list) ->
                val strategyName = list.first().strategyName ?: "未知"
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
        strategyPane.children.setAll(pieChart)
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
        runModePane.children.setAll(pieChart)
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


        // 2️⃣ 创建 X 轴（小时）
        val xAxis = CategoryAxis().apply {
            label = "时间"
        }

        val maxCount = if (hourCount.isEmpty()) 0 else hourCount.maxBy { it.value }.value
        val minCount = if (hourCount.isEmpty()) 0 else hourCount.minBy { it.value }.value
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

        // 4️⃣ 创建折线图
        val lineChart = LineChart(xAxis, yAxis).apply {
            title = "活跃时间分布"
            isLegendVisible = false
        }

        // 5️⃣ 填充数据
        val series = XYChart.Series<String, Number>().apply {
            hourCount.forEach { (hour, count) ->
                data.add(XYChart.Data(hour, count))
            }
        }

        lineChart.data.add(series)

        timePane.children.setAll(lineChart)
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
        durationPane.children.setAll(barChart)
    }

    // 创建数值标签
    private fun createDataLabel(value: Number, unit: String = ""): StackPane {
        val label = Label(value.toString() + unit).apply {
            style = "-fx-font-size: 12px;-fx-padding: 1 0 0 0;"
        }
        return StackPane(label).apply {
            alignment = Pos.TOP_CENTER  // 让标签贴近柱子的顶部
        }
    }

    @FXML
    protected fun changeStatus(mouseEvent: MouseEvent) {
        bindIco.isVisible = !bindIco.isVisible
        unBindIco.isVisible = !unBindIco.isVisible
    }

}
