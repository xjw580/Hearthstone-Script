package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.hsscript.bean.DBCard;
import club.xiaojiawei.hsscript.bean.WeightCard;
import club.xiaojiawei.hsscript.data.PathDataKt;
import club.xiaojiawei.hsscript.utils.CardUtil;
import club.xiaojiawei.hsscript.utils.DBUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
public class WeightSettingsController implements Initializable {

//    private static final Logger log = LoggerFactory.getLogger(WeightSettingsController.class);

    @FXML
    protected NumberField limit;
    @FXML
    protected NumberField offset;
    @FXML
    protected StackPane rootPane;
    @FXML
    protected TableView<DBCard> cardTable;
    @FXML
    protected TableColumn<DBCard, Number> noCol;
    @FXML
    protected TableColumn<DBCard, String> cardIdCol;
    @FXML
    protected TableColumn<DBCard, String> nameCol;
    @FXML
    protected TableColumn<DBCard, Number> attackCol;
    @FXML
    protected TableColumn<DBCard, Number> healthCol;
    @FXML
    protected TableColumn<DBCard, Number> costCol;
    @FXML
    protected TableColumn<DBCard, String> textCol;
    @FXML
    protected TableColumn<DBCard, String> typeCol;
    @FXML
    protected TableColumn<DBCard, String> cardSetCol;
    @FXML
    protected TableView<WeightCard> weightTable;
    @FXML
    protected TableColumn<WeightCard, String> weightCardIdCol;
    @FXML
    protected TableColumn<WeightCard, String> weightNameCol;
    @FXML
    protected TableColumn<WeightCard, Number> weightCol;
    @FXML
    protected NotificationManager<String> notificationManager;
    @FXML
    protected FilterField searchCardField;

    private int currentOffset = 0;

    private static final Path WEIGHT_CONFIG_PATH = Path.of(PathDataKt.getCONFIG_PATH(), "card.weight");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        addListener();
        var cards = CardUtil.INSTANCE.getCardWeightCache();
        if (cards != null) {
            HashSet<WeightCard> weightCards = new HashSet<>(weightTable.getItems());
            for (WeightCard card : cards) {
                if (!weightCards.contains(card)) {
                    weightTable.getItems().add(card);
                }
            }
        }
    }

    private void addListener() {
        searchCardField.setOnFilterAction(text -> {
            if (text == null || text.isEmpty()) {
                cardTable.getItems().clear();
                return;
            }
            int limit;
            int offset;
            if (this.limit.getText().isBlank()) {
                limit = Integer.parseInt(this.limit.getPromptText());
            } else {
                limit = Integer.parseInt(this.limit.getText());
            }
            if (this.offset.getText().isBlank()) {
                offset = Integer.parseInt(this.offset.getPromptText());
            } else {
                offset = Integer.parseInt(this.offset.getText());
            }
            currentOffset = offset;
            cardTable.getItems().setAll(DBUtil.INSTANCE.queryCardByName(text, limit, offset));
        });
        weightTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                searchCardField.setText(newValue.getName());
            }
        });
    }

    private void initTable() {
        cardTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        noCol.setCellValueFactory(param -> {
            ObservableList<DBCard> items = param.getTableView().getItems();
            int index = IntStream.range(0, items.size()).filter(i -> items.get(i) == param.getValue()).findFirst().orElse(-2);
            return new SimpleIntegerProperty(index + 1 + currentOffset);
        });
        cardIdCol.setCellValueFactory(new PropertyValueFactory<>("cardId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        attackCol.setCellValueFactory(new PropertyValueFactory<>("attack"));
        healthCol.setCellValueFactory(new PropertyValueFactory<>("health"));
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
        textCol.setCellValueFactory(new PropertyValueFactory<>("text"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        cardSetCol.setCellValueFactory(new PropertyValueFactory<>("cardSet"));

        weightTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        weightTable.setEditable(true);
        weightCardIdCol.setCellValueFactory(new PropertyValueFactory<>("cardId"));
        weightNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        weightCol.setCellValueFactory(o -> o.getValue().getWeightProperty());
        StringConverter<Number> stringConverter = new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number == null ? "" : number.toString();
            }

            @Override
            public Number fromString(String s) {
                return s == null || s.isBlank() ? 0 : Double.parseDouble(s);
            }
        };
        weightCol.setCellFactory(weightCardNumberTableColumn -> new TextFieldTableCell<>(stringConverter) {
            @Override
            public void commitEdit(Number number) {
                super.commitEdit(number);
                saveWeightConfig();
                notificationManager.showSuccess("修改权重成功", 2);
            }
        });
    }

    private void readWeightConfig(Path weigthPath) {
        List<WeightCard> cards = CardUtil.INSTANCE.readWeightConfig(weigthPath);
        HashSet<WeightCard> weightCards = new HashSet<>(weightTable.getItems());
        for (WeightCard card : cards) {
            if (!weightCards.contains(card)) {
                weightTable.getItems().add(card);
            }
        }
    }

    private void readWeightConfig() {
        readWeightConfig(WEIGHT_CONFIG_PATH);
    }

    private void saveWeightConfig(Path weigthPath) {
        CardUtil.INSTANCE.saveWeightConfig(weightTable.getItems(), weigthPath);
        CardUtil.INSTANCE.reloadCardWeight(weightTable.getItems());
    }

    private void saveWeightConfig() {
        saveWeightConfig(WEIGHT_CONFIG_PATH);
    }

    @FXML
    protected void importConfig(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择要导入的权重文件");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight");
        chooser.getExtensionFilters().add(extFilter);

        List<File> files = chooser.showOpenMultipleDialog(rootPane.getScene().getWindow());
        if (files == null || files.isEmpty()) {
            notificationManager.showInfo("未选择导入路径，导入取消", 2);
            return;
        }
        for (File file : files) {
            readWeightConfig(file.toPath());
        }
        saveWeightConfig();
        notificationManager.showSuccess("导入成功", 2);
    }

    @FXML
    protected void exportConfig(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("导出至");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("权重文件 (*.weight)", "*.weight");
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file == null) {
            notificationManager.showInfo("未选择导出路径，导出取消", 2);
            return;
        }
        saveWeightConfig(file.toPath());
        notificationManager.showSuccess("导出成功", 2);
    }

    @FXML
    protected void addWeight(ActionEvent actionEvent) {
        ObservableList<DBCard> selectedItems = cardTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("左边数据表没有选中行", 2);
            return;
        }
        ArrayList<DBCard> list = new ArrayList<>(selectedItems);
        HashSet<WeightCard> weightSet = new HashSet<>(weightTable.getItems());
        for (DBCard dbCard : list) {
            WeightCard weightCard = new WeightCard(dbCard.getCardId(), dbCard.getName(), 1.0);
            if (!weightSet.contains(weightCard)) {
                weightTable.getItems().add(weightCard);
            }
        }
        saveWeightConfig();
        notificationManager.showSuccess("添加成功", 2);
    }

    @FXML
    protected void removeWeight(ActionEvent actionEvent) {
        ObservableList<WeightCard> selectedItems = weightTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            notificationManager.showInfo("右边权重表没有选中行", 2);
            return;
        }
        ArrayList<WeightCard> weightCards = new ArrayList<>(selectedItems);
        weightTable.getSelectionModel().clearSelection();
        weightTable.getItems().removeAll(weightCards);
        saveWeightConfig();
        notificationManager.showSuccess("移除成功", 2);
    }
}