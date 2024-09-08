package club.xiaojiawei.utils.main;


import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import static com.sun.jna.platform.win32.User32.INSTANCE;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/28 10:25
 */
public class MeasureApplication extends Application {

    private static Window stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        startStage(primaryStage);
    }

    public static void startStage(Stage showStage){
        VBox vBox = new VBox() {{
            setAlignment(Pos.CENTER);
            setStyle("-fx-padding: 10;-fx-spacing: 20");
        }};
        HBox btnPane = new HBox() {{
            setStyle("-fx-spacing: 20;-fx-alignment: center");
        }};
        Button showBtn = new Button("显示");
        TextArea textArea = new TextArea();
        textArea.setMinHeight(500);
        showBtn.setOnAction(event -> {
            if (stage != null) {
                stage.hide();
            }
            stage = show(showStage, textArea);
        });
        Button hideBtn = new Button("隐藏");
        hideBtn.setOnAction(event -> {
            if (stage != null) {
                stage.hide();
            }
        });
        Button clearBtn = new Button("清空");
        clearBtn.setOnAction(event -> {
            textArea.setText("");
        });
        btnPane.getChildren().addAll(showBtn, hideBtn, clearBtn);
        vBox.getChildren().addAll(textArea, btnPane);
        showStage.setScene(new Scene(vBox, 450, 600));
        String title = "GameRectUtil";
        showStage.setTitle(title);
        showStage.show();
        SystemDll.INSTANCE.topWindowForTitle(title, true);
    }

    private static Window show(Stage stage, TextArea textArea) {
        WinDef.HWND hwnd = SystemUtil.findGameHWND();
        if (hwnd == null) {
            System.out.println("null");
            return null;
        }
        SystemUtil.frontWindow(hwnd);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WinDef.RECT clientRECT = new WinDef.RECT();
        WinDef.RECT windowRECT = new WinDef.RECT();
        INSTANCE.GetClientRect(hwnd, clientRECT);
        INSTANCE.GetWindowRect(hwnd, windowRECT);
        int clientW = clientRECT.right - clientRECT.left;
        int clientH = clientRECT.bottom - clientRECT.top;
        int windowW = windowRECT.right - windowRECT.left;
        int windowH = windowRECT.bottom - windowRECT.top;
        Popup popup = new Popup();
        popup.setWidth(clientW);
        popup.setHeight(clientH);
        StackPane root = new StackPane();
        StackPane stackPane = new StackPane(root);
        Circle circle = new Circle(1.5, Color.RED);
        double outputScaleX = Screen.getPrimary().getOutputScaleX();
        double usableH = (clientH) / outputScaleX;
        double usableW = usableH * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        Line hLine = new Line(0, 0, usableW, 0);
        hLine.setFill(Color.BLACK);
        Line vLine = new Line(0, 0, 0, usableH);
        vLine.setFill(Color.BLACK);
        AnchorPane rectangle = new AnchorPane();
        rectangle.setStyle("-fx-background-color: transparent;-fx-border-color: red;-fx-border-width: 2");
        AnchorPane anchorPane = new AnchorPane(rectangle);
        root.getChildren().addAll(hLine, vLine, circle, anchorPane);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.2);-fx-border-color: white");
        root.setOnMousePressed(event -> {
            stage.requestFocus();
            rectangle.setPrefWidth(0);
            rectangle.setPrefHeight(0);
            AnchorPane.setTopAnchor(rectangle, event.getSceneY());
            AnchorPane.setLeftAnchor(rectangle, event.getSceneX());
        });
        root.setOnMouseDragged(event -> {
            Double startY = AnchorPane.getTopAnchor(rectangle);
            Double startX = AnchorPane.getLeftAnchor(rectangle);
            rectangle.setPrefWidth(Math.min(event.getSceneX() - startX, usableW - startX));
            rectangle.setPrefHeight(Math.min(event.getSceneY() - startY, usableH - startY));
        });
        double middleH = usableH / 2;
        double middleW = usableW / 2;
        Runnable runnable = () -> {
            String msg = String.format("public static final GameRect RECT = new GameRect(%.4fD, %.4fD, %.4fD, %.4fD);\n",
                    (AnchorPane.getLeftAnchor(rectangle) - middleW) / usableW,
                    (AnchorPane.getLeftAnchor(rectangle) + rectangle.getWidth() - middleW) / usableW,
                    (AnchorPane.getTopAnchor(rectangle) - middleH) / usableH,
                    (AnchorPane.getTopAnchor(rectangle) + rectangle.getHeight() - middleH) / usableH
            );
            System.out.println(msg);
            textArea.appendText(msg);
        };
        popup.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                controlDown = true;
            }else if (event.getCode() == KeyCode.ESCAPE){
                if (popup.isShowing()){
                    popup.hide();
                }else {
                    popup.show(stage);
                }
            }else if (event.getCode() == KeyCode.SHIFT) {
                shiftDown = true;
            }else if (shiftDown) {
                if (event.getCode() == KeyCode.RIGHT) {
                    AnchorPane.setLeftAnchor(rectangle, Math.min(AnchorPane.getLeftAnchor(rectangle) + 5, usableW - rectangle.getWidth()));
                }else if (event.getCode() == KeyCode.LEFT) {
                    AnchorPane.setLeftAnchor(rectangle, Math.max(AnchorPane.getLeftAnchor(rectangle) - 5, 0));
                }else if (event.getCode() == KeyCode.UP) {
                    AnchorPane.setTopAnchor(rectangle, Math.max(AnchorPane.getTopAnchor(rectangle) - 5, 0));
                }else if (event.getCode() == KeyCode.DOWN) {
                    AnchorPane.setTopAnchor(rectangle, Math.min(AnchorPane.getTopAnchor(rectangle) + 5, usableH - rectangle.getHeight()));
                }else if (event.getCode() == KeyCode.ENTER) {
                    runnable.run();
                }
            }else if (controlDown) {
                if (event.getCode() == KeyCode.RIGHT) {
                    if (AnchorPane.getLeftAnchor(rectangle) + rectangle.getWidth() >= usableW){
                        AnchorPane.setLeftAnchor(rectangle, AnchorPane.getLeftAnchor(rectangle) - 1);
                    }
                    rectangle.setPrefWidth(rectangle.getWidth() + 1);
                }else if (event.getCode() == KeyCode.LEFT) {
                    rectangle.setPrefWidth(rectangle.getWidth() - 1);
                }else if (event.getCode() == KeyCode.UP) {
                    if (AnchorPane.getTopAnchor(rectangle) + rectangle.getHeight() >= usableH){
                        System.out.println("up");
                        AnchorPane.setTopAnchor(rectangle, AnchorPane.getTopAnchor(rectangle) - 1);
                    }
                    rectangle.setPrefHeight(rectangle.getHeight() + 1);
                }else if (event.getCode() == KeyCode.DOWN) {
                    rectangle.setPrefHeight(rectangle.getHeight() - 1);
                }
            }if (event.getCode() == KeyCode.RIGHT) {
                AnchorPane.setLeftAnchor(rectangle, Math.min(AnchorPane.getLeftAnchor(rectangle) + 1, usableW - rectangle.getWidth()));
            }else if (event.getCode() == KeyCode.LEFT) {
                AnchorPane.setLeftAnchor(rectangle, Math.max(AnchorPane.getLeftAnchor(rectangle) - 1, 0));
            }else if (event.getCode() == KeyCode.UP) {
                AnchorPane.setTopAnchor(rectangle, Math.max(AnchorPane.getTopAnchor(rectangle) - 1, 0));
            }else if (event.getCode() == KeyCode.DOWN) {
                AnchorPane.setTopAnchor(rectangle, Math.min(AnchorPane.getTopAnchor(rectangle) + 1, usableH - rectangle.getHeight()));
            }
        });
        popup.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                controlDown = false;
            }else if (event.getCode() == KeyCode.SHIFT) {
                shiftDown = false;
            }
        });
        popup.getContent().addAll(stackPane);
        double titleH = 25D;
        popup.setX(((windowRECT.left + ((windowW - clientW) / 2D)) / outputScaleX) + ((clientW / outputScaleX - usableW) / 2));
        popup.setY((windowRECT.top + ((windowH - clientH - titleH) / 2D) + titleH) / outputScaleX);
        popup.show(stage);
        return popup;
    }

    private static boolean controlDown;
    private static boolean shiftDown;

}
