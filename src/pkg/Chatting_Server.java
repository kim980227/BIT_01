package pkg;


import serverThread.ServerConnectThread;

public class Chatting_Server{

    public static void main(String[] args) {
        ServerConnectThread connectThread = new ServerConnectThread();
        connectThread.start();
//        launch();
    }

//    @Override
//    public void start(Stage arg0) throws Exception {
//        // TODO Auto-generated method stub
//        VBox root = new VBox();//Vertical Box
//        root.setPrefSize(400,300);// VBox의 크기. 가로 400 세로 300.
//        ConnectThread connectThread = new ConnectThread();
//        //TODO START
//        Button btn1 = new Button("서버오픈");
//        btn1.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent arg0) {
//                connectThread.start();
//            }
//        });
//
//        Button btn2 = new Button("데이터 전송");
//        root.getChildren().addAll(btn1,btn2);
//        //TODO FINISH
//        Scene scene = new Scene(root);//장면이 찍힐 화면이 root이다.
//        arg0.setScene(scene);
//        arg0.setTitle("서버");
//        arg0.show();// 창 띄우는거.
//    }
}