package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.War;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.bean.MultiLikeTrieNode;
import club.xiaojiawei.hsscript.data.GameRationConst;
import club.xiaojiawei.hsscript.interfaces.StageHook;
import club.xiaojiawei.hsscript.utils.CardLikeTrieUtil;
import club.xiaojiawei.hsscript.utils.GameDataAnalysisUtil;
import club.xiaojiawei.status.WarStatusKt;
import club.xiaojiawei.util.DeckStrategyUtil;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2025/1/21 12:52
 */
public class GameDataAnalysisController implements Initializable, StageHook {

    private static final Logger log = LoggerFactory.getLogger(GameDataAnalysisController.class);
    @FXML
    private TextField filterField;
    @FXML
    private VBox outerPane;
    @FXML
    private VBox topPane;
    @FXML
    private TextField outputField;
    @FXML
    private Canvas cardCanvas;
    @FXML
    private TextField inputField;
    @FXML
    private StackPane rootPane;
    @FXML
    private Canvas warCanvas;
    @FXML
    private Switch analysisSwitch;

    private double canvasWidth, canvasHeight;

    private double calcHeight(double width) {
        return width / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    private AnimationTimer warAnimationTimer;

    private AnimationTimer cardAnimationTimer;

    private Popup tipPopup;

    private ObservableList<Node> inputTipList;

    private Card inputCard;

    private boolean banTip;

        private final War war = WarStatusKt.getWAR();
//    private final War war = DeckStrategyUtil.INSTANCE.createMCTSWar();

    private final MultiLikeTrieNode<Method> methodRoot = CardLikeTrieUtil.INSTANCE.getRoot();


    private void initCardCanvas() {
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                outputCardMsg();
                event.consume();
            }
        });
        inputField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                handleTip();
            } else {
                hideTipPopup();
            }
        });
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleTip();
        });
        List<Method> methods = methodRoot.get("");
        Font font = new Font("Arial", 15);
        cardAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (analysisSwitch.getStatus() && inputCard != null) {
                    StringBuilder text = new StringBuilder("\n");
                    String methodName = "";
                    String filterText = filterField.getText().toLowerCase();
                    for (Method method : methods) {
                        try {
                            methodName = method.getName();
                            if (!methodName.toLowerCase().contains(filterText)) continue;
                            if (methodName.startsWith("get")) {
                                methodName = methodName.replace("get", "");
                            }
                            Object result = invokeMethod(method, inputCard);
                            text.append(methodName).append(": ").append(result).append("\n");
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            System.out.println(methodName);
                        }
                    }
//                    outputArea.setText(text.toString());
                    double height = Math.min(outerPane.getHeight(), rootPane.getHeight()) - topPane.getHeight();
                    if (height <= 0 || canvasWidth <= 0) return;
                    cardCanvas.setWidth(canvasWidth);
                    cardCanvas.setHeight(height);
                    GraphicsContext context = cardCanvas.getGraphicsContext2D();
                    context.clearRect(0, 0, canvasWidth, height);
                    context.setFont(font);
                    context.fillText(text.toString(), 0, 0);
                }
            }
        };
        cardAnimationTimer.start();
    }

    private void handleTip() {
        String text = inputField.getText();
        if (banTip) return;
        if (handleInputCard() == null || !text.contains(".")) {
            hideTipPopup();
            return;
        }
        text = text.trim();
        int index = text.trim().indexOf(".");
        if (index == -1) return;
        String methodName;
        if (index == text.length() - 1) {
            methodName = "";
        } else {
            methodName = text.substring(index + 1);
        }
        List<Method> methods = methodRoot.get(methodName);
        showTipPopup(methods);
    }

    @Nullable
    private Method handleInputMethod() {
        String text = inputField.getText().trim();
        int index = text.indexOf(".");
        if (index == -1 || index == text.length() - 1) return null;
        String methodName = text.substring(index + 1).toLowerCase();
        List<Method> methods = methodRoot.get(methodName);
        for (Method method : methods) {
            String name = method.getName().toLowerCase();
            name = name.replace("get", "");
            if (Objects.equals(name, methodName)) return method;
        }
        return null;
    }

    @Nullable
    private Card handleInputCard() {
        String text = inputField.getText().trim();
        String[] split = text.split("[.]");
        String entityId;
        int length = split.length;
        if (length == 0) {
            entityId = text;
        } else {
            entityId = split[0];
        }
        return inputCard = war.getCardMap().get(entityId);
    }

    private void calcOutput(@NotNull Method inputMethod) {
        if (inputCard == null) return;
        try {
            Object result = invokeMethod(inputMethod, inputCard);
            if (result != null) {
                outputField.setText(result.toString());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeMethod(Method inputMethod, Card card) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = inputMethod.getParameterTypes();
        Object result;
        if (parameterTypes.length > 0) {
            Object[] params = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Object param;
                if (parameterType == int.class || parameterType == long.class || parameterType == double.class) {
                    param = 0;
                } else if (parameterType == boolean.class) {
                    param = false;
                } else {
                    return null;
                }
                params[i] = param;
            }
            result = inputMethod.invoke(card, params);
        } else {
            result = inputMethod.invoke(card);
        }
        return result;
    }

    private void hideTipPopup() {
        if (tipPopup != null) tipPopup.hide();
    }

    private void showTipPopup(List<Method> methods) {
        if (tipPopup == null) {
            tipPopup = new Popup();
            tipPopup.setAutoFix(false);
            tipPopup.setAutoHide(false);
            VBox vBox = new VBox() {{
                setStyle("-fx-padding: 5");
            }};
            inputTipList = vBox.getChildren();
            ScrollPane scrollPane = new ScrollPane(vBox) {{
                setStyle("-fx-effect: default-common-effect;-fx-padding: 0;-fx-background-insets: 0");
                getStyleClass().addAll("radius-ui");
            }};
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setMaxHeight(500);
            tipPopup.getContent().add(scrollPane);
        }
        inputTipList.clear();
        methods.forEach(method -> {
            String methodName = method.getName().contains("get") ? method.getName().replace("get", "") : method.getName();
            var pane = new StackPane(new Label(methodName)) {{
                setStyle("-fx-padding: 5 10 5 5");
                getStyleClass().addAll("radius-ui", "bg-hover-ui");
                setOnMouseClicked(event -> {
                    banTip = true;
                    inputField.setText(inputField.getText().split("\\.")[0] + "." + methodName);
                    calcOutput(method);
                    hideTipPopup();
                    banTip = false;
                });
            }};
            inputTipList.add(pane);
        });
        tipPopup.setWidth(inputField.getWidth());
        Bounds bounds = inputField.localToScreen(inputField.getBoundsInLocal());
        tipPopup.setAnchorX(bounds.getMinX());
        tipPopup.setAnchorY(bounds.getMaxY());
        tipPopup.show(rootPane.getScene().getWindow());
    }

    private void initWarCanvas() {
        double padding = rootPane.getPadding().getLeft() + rootPane.getPadding().getRight();
        canvasWidth = 800;
        canvasHeight = calcHeight(canvasWidth);
        rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.intValue() - padding;
            canvasWidth = newWidth;
            canvasHeight = calcHeight(newWidth);
        });
        GameDataAnalysisUtil analysisUtil = GameDataAnalysisUtil.INSTANCE;
        analysisUtil.init(warCanvas);
        warAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (analysisSwitch.getStatus()) {
                    warCanvas.setWidth(canvasWidth);
                    warCanvas.setHeight(canvasHeight);
                    analysisUtil.draw(war, warCanvas);
                }
            }
        };
        warAnimationTimer.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initWarCanvas();
        initCardCanvas();
    }


    @Override
    public void onShown() {
    }

    @Override
    public void onShowing() {
    }

    @Override
    public void onHidden() {
    }

    @Override
    public void onHiding() {
        if (warAnimationTimer != null) {
            warAnimationTimer.stop();
        }
        if (cardAnimationTimer != null) {
            cardAnimationTimer.stop();
        }
    }

    @Override
    public void onCloseRequest(@NotNull WindowEvent event) {
    }

    @FXML
    protected void outputCardMsg() {
        if (handleInputCard() == null) return;

        Method method = handleInputMethod();
        if (method == null) return;

        calcOutput(method);

    }
}
