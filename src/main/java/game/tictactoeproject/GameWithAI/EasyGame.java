package game.tictactoeproject.GameWithAI;
import game.tictactoeproject.Logic.GameState;
import game.tictactoeproject.Logic.GameLogic;
import game.tictactoeproject.Logic.Player;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.animation.*;
import javafx.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;
import javafx.scene.media.*;

import static game.tictactoeproject.Logic.GameLogic.createButton;

public class EasyGame extends Application {

    private final Scene aiMenuScene;
    String pathToSoundClick = "D:\\Java(Homework)\\TicTacToeProject\\src\\main\\java\\game\\tictactoeproject\\SoundTrack\\click.mp3";
    Media soundClick = new Media(new File(pathToSoundClick).toURI().toString());
    Image background_white = new Image("file:D:\\Java(Homework)\\TicTacToeProject\\src\\main\\java\\game\\tictactoeproject\\Background\\background_white.jpg");
    Image background_black = new Image("file:D:\\Java(Homework)\\TicTacToeProject\\src\\main\\java\\game\\tictactoeproject\\Background\\background_black.jpg");
    ImageView backgroundImageView = new ImageView(background_white);
    ImageView backgroundImageView1 = new ImageView(background_black);
    GaussianBlur blurEffect = new GaussianBlur(25);

    MediaPlayer mediaPlayerClick = new MediaPlayer(soundClick);
    private final boolean isDarkTheme;
    public EasyGame(Scene aiMenuScene, boolean isDarkTheme) {
        this.aiMenuScene = aiMenuScene;
        this.isDarkTheme = isDarkTheme;
    }

    private final char[][] board = new char[3][3];

    private String nickname;
    private boolean gameOver = false;
    boolean isBotTurn = false;
    private final Label statusLabel = new Label("Твій хід.");
    private final Label playerScoreLabel = new Label(nickname + ": 0");
    private final Label computerScoreLabel = new Label("Бот: 0");
    Player player = new Player(nickname, 'X');
    Player currentPlayer = player;
    Player computer = new Player("Бот", 'O');
    public int playerScore = 0;
    DropShadow shadow = new DropShadow();
    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
    Timeline timeline1 = new Timeline();
    private int computerScore = 0;
    private void resetGame(Button[][] buttons) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '\u0000';
                buttons[i][j].setText(" ");
            }
        }
        currentPlayer = player;
        gameOver = false;
        isBotTurn = false;
        statusLabel.setText("Твій хід.");


    }

    public void saveGameResult() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("game_results_vs_computer.txt", true))) {
            if (getGameState(board)==GameState.DRAW) { // check if the game is a draw
                writer.write("Draw between " + nickname + " and " + computer.getName() + "\n");
            } else {
                String opponentName = (currentPlayer.getSign() == 'X') ? computer.getName() : nickname;
                writer.write(currentPlayer.getName() + " won the game against " + opponentName + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Введіть свій нікнейм");
        dialog.setHeaderText(null);
        dialog.setContentText("Нікнейм:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            nickname = result.get();
            playerScoreLabel.setText(nickname + ": 0");
            player = new Player(nickname, 'X');
            currentPlayer = player;
        }
    }
    public GameState getGameState(char[][] board) {
        if (GameLogic.checkWinner(board)) {
            if(currentPlayer.getSign() == 'X') {
                return GameState.X_WON;
            } else {
                return GameState.O_WON;
            }

        } else if (GameLogic.isDraw(board)) {
            return GameState.DRAW;
        } else {
            return GameState.IN_PROGRESS;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        startGame();
        GridPane grid = new GridPane();
        Button[][] buttons = new Button[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(" ");
                button.setMinSize(200, 200);
                button.setFont(Font.font("Arial", FontWeight.BOLD, 72));

                button.setEffect(shadow);

                int finalI = i;
                int finalJ = j;
                button.setOnAction(event -> {
                    mediaPlayerClick.setVolume(0.2);
                    mediaPlayerClick.stop();
                    mediaPlayerClick.play();
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(1),
                            ae -> mediaPlayerClick.play()));
                    timeline.play();
                    if (!gameOver && board[finalI][finalJ] == '\u0000' && !isBotTurn) {
                        button.setText(String.valueOf(currentPlayer.getSign()));
                        board[finalI][finalJ] = currentPlayer.getSign();
                        if(isDarkTheme){
                            button.setEffect(new Glow(2));
                            button.setStyle("-fx-text-fill: red;-fx-background-color: black;-fx-border-color: white;");
                        }
                        if (getGameState(board)==GameState.X_WON || getGameState(board)==GameState.O_WON) {
                            statusLabel.setText(currentPlayer.getName() + " переміг!");
                            if (currentPlayer.getSign() == 'X') {
                                playerScore++;
                                playerScoreLabel.setText(nickname +": " + playerScore);

                            } else {
                                computerScore++;
                                computerScoreLabel.setText("Бот: " + computerScore);
                            }
                            gameOver = true;
                            saveGameResult();
                        } else if (getGameState(board)==GameState.DRAW) {
                            statusLabel.setText("Нічия!");
                            gameOver = true;
                            saveGameResult();

                            ;
                        } else {
                            currentPlayer = (currentPlayer == player) ? computer : player;
                            if (currentPlayer.getSign() == 'O') {

                                isBotTurn = true;
                                timeline1.getKeyFrames().addAll(
                                        new KeyFrame(Duration.seconds(0), event1 -> statusLabel.setText("Бот думає.")),
                                        new KeyFrame(Duration.seconds(0.5), event1 -> statusLabel.setText("Бот думає..")),
                                        new KeyFrame(Duration.seconds(1), event1 -> statusLabel.setText("Бот думає...")),
                                        new KeyFrame(Duration.seconds(1.5), event1 -> statusLabel.setText("Бот думає."))

                                );

                                timeline1.setCycleCount(Animation.INDEFINITE);
                                timeline1.play();
                                pause.setOnFinished(event1 -> {

                                    List<int[]> freeCells = new ArrayList<>();
                                    for (int k = 0; k < 3; k++) {
                                        for (int l = 0; l < 3; l++) {
                                            if (board[k][l] == '\u0000') {
                                                freeCells.add(new int[]{k, l});
                                            }
                                        }
                                    }
                                    int[] cell = freeCells.get(new Random().nextInt(freeCells.size()));
                                    int bestMoveI = cell[0];
                                    int bestMoveJ = cell[1];
                                    mediaPlayerClick.setVolume(0.2);
                                    mediaPlayerClick.stop();
                                    mediaPlayerClick.play();
                                    Timeline timeline2 = new Timeline(new KeyFrame(
                                            Duration.millis(1),
                                            ae -> mediaPlayerClick.play()));
                                    timeline2.play();
                                    board[bestMoveI][bestMoveJ] = currentPlayer.getSign();
                                    buttons[bestMoveI][bestMoveJ].setText(String.valueOf(currentPlayer.getSign()));
                                    timeline1.stop();
                                    if (isDarkTheme) {
                                        buttons[bestMoveI][bestMoveJ].setEffect(new Glow(2));
                                        buttons[bestMoveI][bestMoveJ].setStyle("-fx-text-fill: blue; -fx-background-color: black; -fx-border-color: white;");
                                    }
                                    if (getGameState(board) == GameState.X_WON || getGameState(board) == GameState.O_WON) {
                                        statusLabel.setText(currentPlayer.getName() + " переміг!");
                                        if (currentPlayer.getSign() == 'X') {
                                            playerScore++;
                                            playerScoreLabel.setText("Гравець: " + playerScore);

                                        } else {
                                            computerScore++;
                                            computerScoreLabel.setText("Бот: " + computerScore);

                                        }
                                        gameOver = true;
                                        saveGameResult();
                                    } else if (getGameState(board) == GameState.DRAW) {
                                        statusLabel.setText("Нічия!");
                                        gameOver = true;
                                        saveGameResult();


                                    } else {
                                        currentPlayer = (currentPlayer == player) ? computer : player;
                                        statusLabel.setText("Бот зробив хід. Тепер твоя черга.");
                                        isBotTurn = false;
                                    }
                                });
                                pause.play();
                            }
                        }
                    }
                });
                grid.add(button, i, j);
                buttons[i][j] = button;
            }
        }

        Button resetButton = createButton("Рестарт", event -> {
                    mediaPlayerClick.setVolume(0.2);
                    mediaPlayerClick.stop();
                    mediaPlayerClick.play();
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(1),
                            ae -> mediaPlayerClick.play()));
                    timeline.play();
                    gameOver = false;
                    currentPlayer = player;
                    statusLabel.setText("Твоя черга.");
                    resetGame(buttons);
                });
        resetButton.setEffect(shadow);
        Button resetScoreButton = createButton("Обнулення", event -> {
            mediaPlayerClick.setVolume(0.2);
            mediaPlayerClick.stop();
            mediaPlayerClick.play();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(1),
                    ae -> mediaPlayerClick.play()));
            timeline.play();
            playerScore = 0;
            computerScore = 0;
            playerScoreLabel.setText(nickname + ": " + playerScore);
            computerScoreLabel.setText("Бот: " + computerScore);
        });

        Button backButton = createButton("Назад", event -> {
            mediaPlayerClick.setVolume(0.2);
            mediaPlayerClick.stop();
            mediaPlayerClick.play();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(1),
                    ae -> mediaPlayerClick.play()));
            timeline.play();
            primaryStage.setScene(aiMenuScene);
            primaryStage.show();
        });

        Label titleLabel = new Label("Хрестики-нулики");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        playerScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        computerScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(10);
        infoBox.setMinWidth(300);
        infoBox.getChildren().addAll(backButton,titleLabel, statusLabel, playerScoreLabel, computerScoreLabel, resetButton, resetScoreButton);

        HBox gameBox = new HBox();
        gameBox.setAlignment(Pos.CENTER);
        gameBox.setSpacing(30);
        gameBox.getChildren().addAll(grid);

        BorderPane border = new BorderPane();
        BorderPane.setMargin(gameBox, new Insets(10, 0, 0, 0));
        border.setTop(infoBox);
        border.setCenter(gameBox);

        backgroundImageView.setEffect(blurEffect);
        backgroundImageView1.setEffect(blurEffect);

        StackPane root = new StackPane(backgroundImageView, border);

        Scene scene = new Scene(root, 740, 960);
        if (isDarkTheme) {
            root.getChildren().setAll(backgroundImageView1, border);
            titleLabel.setStyle("-fx-text-fill: white");
            statusLabel.setStyle("-fx-text-fill: white");
            playerScoreLabel.setStyle("-fx-text-fill: white");
            computerScoreLabel.setStyle("-fx-text-fill: white");
            resetButton.setStyle("-fx-text-fill: white;-fx-background-color: black;-fx-border-color: white");
            resetScoreButton.setStyle("-fx-text-fill: white;-fx-background-color: black;-fx-border-color: white");
            backButton.setStyle("-fx-text-fill: white;-fx-background-color: black;-fx-border-color: white");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setStyle("-fx-background-color: black; -fx-border-color: white;");

                }
            }
        }

        //set shadow effects for all buttons
        resetButton.setEffect(shadow);
        resetScoreButton.setEffect(shadow);
        backButton.setEffect(shadow);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(300);
        primaryStage.show();
    }


}