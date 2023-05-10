package pkg;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class Draw extends Application {
    // 그림그리기
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane, 800, 500);
    Canvas canvas = new Canvas(800, 500); // 캔버스(도화지) 생성 및 화면 크기 설정
    GraphicsContext gc;
    ColorPicker cp = new ColorPicker(); // 색깔 바꾸기
    Slider slider = new Slider(); // 굵기 바꾸기
    Label lable = new Label(); // 슬라이더 객체 구분
    GridPane grid = new GridPane();


    @Override
    public void start(Stage arg0) throws Exception {
        AtomicInteger count = new AtomicInteger();
        try {
            arg0.setScene(scene);
            arg0.show(); // 화면 띄우기

            // 저장 버튼 생성
            Button saveButton = new Button("Save");
            saveButton.setOnAction(e -> saveDrawing());

            gc = canvas.getGraphicsContext2D(); // 그림을 그리는 객체(연필같은 느낌)
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);

            pane.getChildren().addAll(canvas, cp,slider, grid); // 화면을 레이아웃 쌓듯이 하는듯? 도화지, 연필, 등등을 레이아웃을 쌓아올림
            cp.setValue(Color.BLACK); // 연필색깔 초기화
            // 연필 색깔 바꾸기
            cp.setOnAction(e -> {
                gc.setStroke(cp.getValue());
            });
            // 연필 굵기 바꾸기
            slider.setMin(10);
            slider.setShowTickLabels(true); // 굵기 숫자 표기
            slider.setShowTickMarks(true); // 구분선 표기
            slider.valueProperty().addListener(e->{
                double vaule = slider.getValue();
                String str = String.format("%.1f", vaule); // 소수점 한자리 수 출력 포맷
                lable.setText(str);
                gc.setLineWidth(vaule);

            }); // 리스너를 통해 값을 계속해서 듣는다. 값이 바뀌면 람다식 실행
            grid.addRow(0, cp,slider, lable, saveButton); // 화면에 표시
            grid.setHgap(20); // 객체 간 거리 설정
            grid.setAlignment(Pos.TOP_CENTER); // 좌표 설정
            grid.setPadding(new Insets(20,0,0,0)); // 패딩(여백) 주기

            scene.setOnMousePressed(e -> {
                gc.beginPath(); // 그림을 그릴꺼야! 선언
                gc.lineTo(e.getSceneX(), e.getSceneY()); // x,y 축 설정
                gc.stroke(); // 실제 그리는 함수
            });
            scene.setOnMouseDragged(e -> {
                gc.lineTo(e.getSceneX(), e.getSceneY());
                gc.stroke();

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1 반환 : 저장 0 반환 : 저장x
    private int saveDrawing() {
        try {
            String savePath = "./image"; // Save location
            File directory = new File(savePath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
            }
            String fileName = "Random or MD5";
            File file = new File(directory, fileName); // Specify the desired file name and extension

            // Convert the canvas content to an image
            javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);

            // Save the image to the specified file
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    public void stageClose(Stage stage){
        stage.close();
    }


    public static void main(String[] args) {
        launch();
    }
}
