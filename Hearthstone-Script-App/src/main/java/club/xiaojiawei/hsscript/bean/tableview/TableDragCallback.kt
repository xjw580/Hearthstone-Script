package club.xiaojiawei.hsscript.bean.tableview

import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.SnapshotParameters
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.util.Callback
import kotlin.math.min

/**
 * 让表格实现行拖拽排序功能：tableView.setRowFactory(new TableDragCallback<>());
 * @author 肖嘉威
 * @date 2025/4/13 16:42
 */

open class TableDragCallback<P, R> : Callback<TableView<P?>, TableRow<R?>?> {
    protected fun updateOrder() {}

    override fun call(pTableView: TableView<P?>): TableRow<R?>? {
        val tableRow = TableRow<R?>()

        tableRow.onMousePressed = EventHandler { event: MouseEvent? -> tableRow.setCursor(Cursor.MOVE) }

        tableRow.onMouseExited = EventHandler { event: MouseEvent? -> tableRow.setCursor(Cursor.DEFAULT) }

        tableRow.onMouseReleased = EventHandler { event: MouseEvent? -> tableRow.setCursor(Cursor.DEFAULT) }

        tableRow.onDragDetected =
            EventHandler { event: MouseEvent? ->
                val dragBoard = tableRow.startDragAndDrop(*TransferMode.COPY_OR_MOVE)
                val clipboardContent = ClipboardContent()

                val snapshot =
                    tableRow.snapshot(
                        SnapshotParameters(),
                        WritableImage(tableRow.getWidth().toInt(), tableRow.getHeight().toInt()),
                    )
                val rawImage = SwingFXUtils.fromFXImage(snapshot, null)
                //            将图片缩小到原来的3/4，防止较大图片四周变透明的情况
                //            int newW = (rawImage.getWidth() >> 2) * 3, newH = (rawImage.getHeight() >> 2) * 3;
                //            BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
                //            newImage.getGraphics().drawImage(rawImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0 , 0, null);
                clipboardContent.putImage(SwingFXUtils.toFXImage(rawImage, null))
                clipboardContent.putString(tableRow.index.toString())
                dragBoard.setContent(clipboardContent)
            }

        tableRow.onDragOver =
            EventHandler { event: DragEvent? -> event!!.acceptTransferModes(*TransferMode.COPY_OR_MOVE) }

        tableRow.onDragDropped =
            EventHandler { event: DragEvent? ->
                val dragBoard = event!!.dragboard
                val sourceIndex = dragBoard.string.toInt()
                var targetIndex = tableRow.index
                targetIndex = min(targetIndex.toDouble(), (pTableView.getItems().size - 1).toDouble()).toInt()
                if (sourceIndex != targetIndex) {
                    val removed = pTableView.getItems().removeAt(sourceIndex)
                    pTableView.getItems().add(targetIndex, removed)
                    updateOrder()
                    pTableView.edit(-1, null)
                    pTableView.getSelectionModel().clearAndSelect(targetIndex)
                }
                event.setDropCompleted(true)
                dragged(sourceIndex, targetIndex)
            }
        return tableRow
    }

    open fun dragged(srcIndex: Int, destIndex: Int) {
    }
}
