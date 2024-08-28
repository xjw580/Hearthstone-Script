package club.xiaojiawei.utils.main;


import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

    Window stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

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
            stage = show(primaryStage, textArea);
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
        primaryStage.setScene(new Scene(vBox, 450, 600));
        primaryStage.show();
    }

    private Window show(Stage stage, TextArea textArea) {
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
        double realH = (clientH) / outputScaleX;
        double realW = realH * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        Line hLine = new Line(0, 0, realW, 0);
        hLine.setFill(Color.BLACK);
        Line vLine = new Line(0, 0, 0, realH);
        vLine.setFill(Color.BLACK);
        AnchorPane rectangle = new AnchorPane();
        rectangle.setStyle("-fx-background-color: transparent;-fx-border-color: red;-fx-border-width: 2");
        AnchorPane anchorPane = new AnchorPane(rectangle);
        root.getChildren().addAll(hLine, vLine, circle, anchorPane);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.15)");
        root.setOnMousePressed(event -> {
            rectangle.setPrefWidth(0);
            rectangle.setPrefHeight(0);
            AnchorPane.setTopAnchor(rectangle, event.getSceneY());
            AnchorPane.setLeftAnchor(rectangle, event.getSceneX());
        });
        root.setOnMouseDragged(event -> {
            Double startY = AnchorPane.getTopAnchor(rectangle);
            Double startX = AnchorPane.getLeftAnchor(rectangle);
            rectangle.setPrefWidth(Math.min(event.getSceneX() - startX, realW - startX));
            rectangle.setPrefHeight(Math.min(event.getSceneY() - startY, realH - startY));
        });
        double middleH = realH / 2;
        double middleW = realW / 2;
        root.setOnMouseReleased(event -> {
            String msg = String.format("left: %.4f, right: %.4f, top: %.4f, bottom: %.4f\n",
                    (AnchorPane.getLeftAnchor(rectangle) - middleW) / realW,
                    (AnchorPane.getLeftAnchor(rectangle) + rectangle.getWidth() - middleW) / realW,
                    (AnchorPane.getTopAnchor(rectangle) - middleH) / realH,
                    (AnchorPane.getTopAnchor(rectangle) + rectangle.getHeight() - middleH) / realH
            );
            textArea.appendText(msg);
        });
        popup.getContent().addAll(stackPane);
        double titleH = 25D;
        popup.setX(((windowRECT.left + ((windowW - clientW) / 2D)) / outputScaleX) + ((clientW / outputScaleX - realW) / 2));
        popup.setY((windowRECT.top + ((windowH - clientH - titleH) / 2D) + titleH) / outputScaleX);
        popup.show(stage);
        return popup;
    }

}
