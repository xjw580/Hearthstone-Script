package club.xiaojiawei.hsscript.bean.tableview

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.util.Callback
import java.util.function.IntPredicate
import java.util.stream.IntStream

/**
 * 序号
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/6/20 15:51
 */
class NumCallback<S> : Callback<TableColumn.CellDataFeatures<S?, Number?>?, ObservableValue<Number?>?> {

    override fun call(p0: TableColumn.CellDataFeatures<S?, Number?>?): ObservableValue<Number?>? {
        val items = p0?.getTableView()?.getItems() ?: return null
        val index = IntStream.range(0, items.size).filter(IntPredicate { i: Int -> items.get(i) === p0.getValue() })
            .findFirst().orElse(-2)
        return SimpleIntegerProperty(index + 1)
    }
}