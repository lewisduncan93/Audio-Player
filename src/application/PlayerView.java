package application;

import java.io.File;
import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * PlayerView of the application which
 * is the front-end implementation using JavaFX.
 *
 * @author Lewis Duncan
 *
 */
public class PlayerView extends Application {

	private BorderPane borderPane;
	private Scene scene;
	private Slider volumeSlider, durationBar;
	private Button pausePlayButton, nextFileButton, prevFileButton, addFileButton, removeFileButton;
	private ListView<AudioFile> listView;
	private Label lName, lArtist, lAlbum, lSize, lDuration, lPlayCount, lDurationTime;

	public String name, artist, album, size, duration, playCount;
	private boolean buttonPlaying = false;
	private double currTime, rawDuration;
	private FileChooser fileChooser = new FileChooser();
	private Stage primaryStage;
	private ObservableList<AudioFile> data;
	private double endOfTrackTime = 99.9;



	private Player player;
	private Library library;
	private AudioFile audioFile;

	private Image playButtonImage = new Image(getClass().getResourceAsStream("images/playBtn.png"));
	private Image pauseButtonImage = new Image(getClass().getResourceAsStream("images/pauseBtn.png"));
	private ImageView imageViewPlay = new ImageView(playButtonImage);
	private ImageView imageViewPause = new ImageView(pauseButtonImage);

	public PlayerView() {
		player = new Player();
		library = new SQLiteLibrary();
		audioFile = new AudioFile();

	}

	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setTitle("Audio Player System");
		primaryStage.setResizable(false);
		primaryStage.setScene(getScene());

		primaryStage.getIcons().add(new Image("/application/images/appIcon.png"));

		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				primaryStage.close();
				System.out.println("Program closed");
			}
		});
	}

	private Scene getScene() {

		borderPane = new BorderPane();
		borderPane.setStyle("-fx-background-color: Black");
		scene = new Scene(borderPane, 700, 200);
		scene.setFill(Color.BLACK);
		scene.getStylesheets().add("application/stylesheet.css");
		borderPane.setLeft(getFileInfoView());
		borderPane.setRight(getLibraryView());
		borderPane.setBottom(getToolBar());

		return scene;
	}


	public VBox getFileInfoView() {
		VBox vbox = new VBox(10);
		vbox.setStyle("-fx-background-color: Transparent; -fx-border-color: red; -fx-border-width: 1px");
		vbox.setPadding(new Insets(0, 200, 0, 0));
		vbox.alignmentProperty().isBound();

		vbox.getChildren().addAll(getName(), getArtist(), getAlbum(), getSize(), getDuration(), getPlayCount());

		return vbox;
	}

	private HBox getLibraryView() {
		HBox hbox = new HBox(0);

		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setAlignment(Pos.CENTER_LEFT);

		hbox.setStyle("-fx-background-color: Transparent; -fx-border-color: Red; -fx-border-width: 1px");


		hbox.getChildren().addAll(getLibrary());

		return hbox;
	}

	private HBox getToolBar() {

		HBox toolBar = new HBox(0);
		toolBar.setPadding(new Insets(-20, 10, -1, 20));
		toolBar.setAlignment(Pos.CENTER);
		toolBar.alignmentProperty().isBound();
		toolBar.setSpacing(10);
		toolBar.setStyle("-fx-background-color: black; -fx-border-color: Red; -fx-border-width: 2px");

		toolBar.getChildren().addAll(getDurationTime(), getDurationBar(), getPrevFileButton(), getPausePlayButton(), getNextFileButton(),
				getAddFileButton(), getRemoveFileButton(), getVolumeIcon(), getVolumeSlider());

		return toolBar;
	}

	public Button getPausePlayButton() {


		pausePlayButton = new Button();
		pausePlayButton.setShape(new Circle());
		pausePlayButton.setStyle("-fx-background-color: Transparent; -fx-padding: 0");
		pausePlayButton.setGraphic(imageViewPlay);
		pausePlayButton.setTooltip(
			    new Tooltip("Pause/Play selected audio file")
			);

		pausePlayButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				AudioFile af = listView.getSelectionModel().getSelectedItem();
				if (buttonPlaying == false) {
					player.playAudioFile(af);
					Button pausePlayButton = (Button) e.getSource();
					pausePlayButton.setGraphic(imageViewPause);
					System.out.println("Playing");
					buttonPlaying = true;
					player.setMetaData(af);
				}

				else if (buttonPlaying == true) {
					player.pauseAudioFile();
					Button pausePlayButton = (Button) e.getSource();
					pausePlayButton.setGraphic(imageViewPlay);
					System.out.println("Paused");
					buttonPlaying = false;
					player.setMetaData(af);
				}
			}
		});

		return pausePlayButton;
	}

	public Button getNextFileButton() {

		Image nextButtonImage = new Image(getClass().getResourceAsStream("images/nextFileBtn.png"));
		ImageView imageViewNext = new ImageView(nextButtonImage);

		imageViewNext.setFitHeight(25);
		imageViewNext.setFitWidth(40);

		nextFileButton = new Button("");
		nextFileButton.setStyle("-fx-background-color: Transparent; -fx-padding: 0");
		nextFileButton.setMaxSize(1, 1);
		nextFileButton.setGraphic(imageViewNext);
		nextFileButton.setTooltip(
			    new Tooltip("Next audio file")
			);

		nextFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Next file");
				AudioFile currentAf = listView.getSelectionModel().getSelectedItem();
				int nextIndex = data.indexOf(currentAf) + 1;
				System.out.println(nextIndex);
				if (nextIndex < data.size() ) {
					AudioFile newAf = data.get(nextIndex);
					System.out.println("playing next song " + newAf);
					player.playAudioFile(newAf);
					listView.getSelectionModel().select(newAf);
					buttonPlaying = true;
					pausePlayButton.setGraphic(imageViewPause);

				}
			}

		});

		return nextFileButton;
	}

	public Button getPrevFileButton() {

		Image prevButtonImage = new Image(getClass().getResourceAsStream("images/prevFileBtn.png"));
		ImageView imageViewPrev = new ImageView(prevButtonImage);

		imageViewPrev.setFitHeight(25);
		imageViewPrev.setFitWidth(40);

		prevFileButton = new Button();
		prevFileButton.setStyle("-fx-background-color: Transparent; -fx-padding: 0");
		prevFileButton.setMaxSize(1, 1);
		prevFileButton.setGraphic(imageViewPrev);
		prevFileButton.setTooltip(
			    new Tooltip("Previous audio file")
			);

		prevFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Previous file");
				AudioFile currentAf = listView.getSelectionModel().getSelectedItem();
				int prevIndex = data.indexOf(currentAf) - 1;
				System.out.println(prevIndex);
				if (prevIndex >= 0) {
					AudioFile newAf = data.get(prevIndex);
					System.out.println(newAf);
					player.playAudioFile(newAf);
					listView.getSelectionModel().select(newAf);
					buttonPlaying = true;
					pausePlayButton.setGraphic(imageViewPause);
				}
			}

		});

		return prevFileButton;
	}

	public Button getAddFileButton() {

		Image addButtonImage = new Image(getClass().getResourceAsStream("images/addFileBtn.png"));
		ImageView imageViewAdd = new ImageView(addButtonImage);

		imageViewAdd.setFitHeight(20);
		imageViewAdd.setFitWidth(20);

		addFileButton = new Button();
		addFileButton.setStyle("-fx-background-color: Transparent; -fx-padding: 0");
		addFileButton.setMaxSize(1, 1);
		addFileButton.setGraphic(imageViewAdd);
		addFileButton.setTooltip(
			    new Tooltip("Add an audio file")
			);

		addFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Add file");

				File file = fileChooser.showOpenDialog(primaryStage);
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
				fileChooser.getExtensionFilters().add(extFilter);
				if (file != null) {
					try {
						AudioFile af = MetadataExtractor.createAudioFileFromFile(file);
						data.add(af);
						library.add(af);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});

		return addFileButton;

	}

	public Button getRemoveFileButton() {

		Image removeButtonImage = new Image(getClass().getResourceAsStream("images/removeFileBtn.png"));
		ImageView imageViewRemove = new ImageView(removeButtonImage);

		imageViewRemove.setFitHeight(20);
		imageViewRemove.setFitWidth(20);

		removeFileButton = new Button();
		removeFileButton.setStyle("-fx-background-color: Transparent; -fx-padding: 0");
		removeFileButton.setMaxSize(1, 1);
		removeFileButton.setGraphic(imageViewRemove);
		removeFileButton.setTooltip(
			    new Tooltip("Remove selected audio file")
			);

		removeFileButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				System.out.println("Remove file");
				AudioFile af = listView.getSelectionModel().getSelectedItem();
				data.remove(af);
				library.remove(af);

			}

		});

		return removeFileButton;

	}

	public Slider getDurationBar() {

		durationBar = new Slider();
		durationBar.setMinSize(200, 0);
		durationBar.setMaxSize(225, 0);
		durationBar.setMax(100.0);
		durationBar.getStyleClass().add("slider-durationBar");

		durationBar.valueProperty().addListener(new ChangeListener<Number>() {
		public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
			if (player.isPlaying()) {
					double duration = player.getTotalDuration();
					double seekTime = (newVal.doubleValue() * duration) / 100.0;
					System.out.println("duration = " + duration + "; seek time = " + seekTime);

					player.seek(seekTime);

				}
			}
		});


		player.addDurationChangeListener(new ChangeListener<Duration>() {
			public void changed(ObservableValue<? extends Duration> ov, Duration oldVal, Duration newVal) {
				rawDuration = player.getTotalDuration();
				currTime = (newVal.toMillis() / rawDuration) * 100.0;
				durationBar.setValue(currTime);

				System.out.println(currTime);

				if(currTime >= endOfTrackTime){
					pausePlayButton.setGraphic(imageViewPlay);
					buttonPlaying = false;

					System.out.println("Next file");
					AudioFile currentAf = listView.getSelectionModel().getSelectedItem();
					int nextIndex = data.indexOf(currentAf) + 1;
					System.out.println(nextIndex);
				if (nextIndex < data.size() ) {
					AudioFile newAf = data.get(nextIndex);
					System.out.println("playing next song " + newAf);
					player.playAudioFile(newAf);
					listView.getSelectionModel().select(newAf);
					buttonPlaying = true;
					pausePlayButton.setGraphic(imageViewPause);
				}
			}}
		});


		return durationBar;

	}

	public ImageView getVolumeIcon() {

		Image volumeIconImage = new Image(getClass().getResourceAsStream("images/volumeIconFull.png"));
		ImageView imageViewVolumeIcon = new ImageView(volumeIconImage);

		return imageViewVolumeIcon;
	}

	public Slider getVolumeSlider() {

		volumeSlider = new Slider();
		volumeSlider.setValue(100);
		volumeSlider.setMaxSize(100, 0);
		volumeSlider.setMin(0);
		volumeSlider.setMax(1);
		volumeSlider.getStyleClass().add("slider-volumeSlider");

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
				System.out.println((float) newVal.doubleValue());
				player.setVolume((float) newVal.doubleValue());

			}
		});

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {


            }
		});

		return volumeSlider;
	}

	public ScrollPane getLibrary() {

		listView = new ListView<AudioFile>();
		ScrollPane scrollPane = new ScrollPane();

	    data = FXCollections.observableArrayList(library.getAll());

		listView.getStyleClass().add("list-view");
		listView.setMinWidth(260);
		listView.setMaxWidth(260);

		listView.setItems(data);

		scrollPane.getStyleClass().add("scroll-pane");
		scrollPane.prefWidthProperty().bind(listView.widthProperty());
		scrollPane.prefHeightProperty().bind(listView.heightProperty());
		scrollPane.setContent(listView);

		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		return scrollPane;
	}

	public Label getDurationTime(){




		lDurationTime = new Label();

		lDurationTime.setTextFill(Color.web("#e80000"));


		return lDurationTime;
	}

	public Label getName(){

		lName = new Label();
		lName.setTextFill(Color.web("#e80000"));
		lName.setText(lName.getText()+"Name: "+ audioFile.name);

		return lName;
	}

	public Label getArtist(){

		lArtist = new Label();
		lArtist.setTextFill(Color.web("#e80000"));
		lArtist.setText(lArtist.getText()+"Artist: ");


		return lArtist;
	}

	public Label getAlbum(){

		lAlbum = new Label();
		lAlbum.setTextFill(Color.web("#e80000"));
		lAlbum.setText(lAlbum.getText()+"Album: ");

		return lAlbum;
	}

	public Label getSize(){

		lSize = new Label();
		lSize.setTextFill(Color.web("#e80000"));
		lSize.setText(lSize.getText()+"Size: ");

		return lSize;
	}

	public Label getDuration(){

		lDuration = new Label();
		lDuration.setTextFill(Color.web("#e80000"));
		lDuration.setText(lDuration.getText()+"Duration: ");

		return lDuration;
	}

	public Label getPlayCount(){

		lPlayCount = new Label();
		lPlayCount.setTextFill(Color.web("#e80000"));
		lPlayCount.setText(lPlayCount.getText()+"Play count: ");

		return lPlayCount;
	}


	public static void main(String[] args) {
		launch(args);
	}
}
