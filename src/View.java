import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.GroupLayout.Alignment;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Initializes game and allows user to interact with character and map
 * @author Alexander Miller
 * @author Lan Ngo
 * @author Joyce Wang
 * @author Wentao Zhou
 */
public class View extends javafx.application.Application {
	
	/**
	 * Scope Width
	 */
    private static final double WIDTH = 1200;
    
    /**
     * Scope Height
     */
    private static final double HEIGHT = 600;
    
    /**
     * controller used to communicate with model
     */
    private Controller controller;
    
    /**
     * stage used to display game
     */
    private Stage stage;
    
    /**
     * level user has chosen
     */
    private int level;
    
    /**
     * boolean true if user can jump, false otherwise
     */
    private boolean enableJump;
    
    /**
     * true if upanimation is playing, false otherwise
     */
    private boolean upPlaying;
    
    /**
     * true if downanimation is playing, false otherwise
     */
    private boolean downPlaying;
    
    /**
     * true if paused, false otherwise
     */
    private boolean isPaused;
    
    /**
     * Mediaplayer plays wind sounds
     */
    private MediaPlayer wind;
    
    /**
     * Mediaplayer plays game backtrack
     */
    private MediaPlayer backtrack;
    /**
     * Animation timer that controllers game
     */
    private AnimationTimer movement;
        
	/**
	 * Initializes beginning dialog prompting user to continue
	 * a saved game or start a new one
	 * @param stage Require stage used to display all visuals
	 */
	public void start(Stage stage) {
		controller = new Controller();
		this.stage = stage;
		this.stage.setTitle("A NEW AGE OF MARIO");		
		newGameMenu();
		this.stage.show();
		
		File music = new File(System.getProperty("user.dir") + "/Sounds/wind.mp3");
		Media media = new Media(music.toURI().toString()); 
	    wind = new MediaPlayer(media); 
	    wind.setAutoPlay(true);
	    wind.setStopTime(Duration.seconds(35));
	    wind.setCycleCount(AudioClip.INDEFINITE);
	    wind.setVolume(0.2);
	    wind.play();
	}
	
	/**
	 * Visual and handlers for user to chose to start a new game
	 * or continue a saved game
	 */
	public void newGameMenu() {
		// User Guidance option
		Text guidance = new Text("User Guidance");
		guidance.setFont(Font.font("Krungthep", 30));
		guidance.setFill(Color.WHITE);
		guidance.setTranslateY(140);
		
		// Creating text options
		Text newGame = new Text("New Game");
		newGame.setFont(Font.font("Krungthep", 60));
		newGame.setFill(Color.WHITE);
		
		Text cont = new Text("Continue");
		cont.setFont(Font.font("Krungthep", 60));
		cont.setFill(Color.WHITE);
		cont.setTranslateY(70);
		
		// Mouse handlers turn text red when user
		// hovers over an option
		// Turns text grey if user clicks on an option
		// then handles option
		// NEW GAME
		newGame.setOnMouseEntered(p-> {
			System.out.println(wind.isMute());
			newGame.setFill(Color.RED);
		});
		newGame.setOnMouseExited(p-> {
			newGame.setFill(Color.WHITE);
		});
		newGame.setOnMousePressed(p-> {
			newGame.setFill(Color.GREY);
		});
		newGame.setOnMouseClicked(e -> {
			displayMainMenu(true);
		});
		// CONTINUE SAVED GAME
		cont.setOnMouseEntered(p-> {
			cont.setFill(Color.RED);
		});
		cont.setOnMouseExited(p-> {
			cont.setFill(Color.WHITE);
		});
		cont.setOnMousePressed(p-> {
			cont.setFill(Color.GREY);
		});
		cont.setOnMouseClicked(e ->{
			level = controller.oldGame();
			if(level != 0) {
				launchLevel();
			}else {
				displayMainMenu(true);
			}
		});
		// USER GUIDANCE
		guidance.setOnMouseEntered(p-> {
			guidance.setFill(Color.RED);
		});
		guidance.setOnMouseExited(p-> {
			guidance.setFill(Color.WHITE);
		});
		guidance.setOnMousePressed(p-> {
			guidance.setFill(Color.GREY);
		});
		guidance.setOnMouseClicked(p -> {
			new UserGuidanceDialog();
		});
		
		Rectangle back = new Rectangle(0,0,1200,600);
		back.setFill(Color.BLACK);
		StackPane options = new StackPane(back,newGame,cont,guidance);
		stage.setScene(new Scene(options,1200,600));
	}
    	
	/**
	 * Creates and displays main menu
	 * @param newGame True if user desires to start a new game, false otherwise
	 */
	public void displayMainMenu(boolean newGame) {
		// Map 1 option
		Text levelOneText = new Text("Barren Desert");
		levelOneText.setFont(Font.font("Krungthep", 60));
		levelOneText.setFill(Color.WHITE);
		Rectangle mapImageOne = new Rectangle(0,0,1200,200);
		mapImageOne.setFill(new ImagePattern(new Image("file:map1/MapOverview.png")));
		Rectangle layerCastOne = new Rectangle(0,0, 1200, 200);
		layerCastOne.setFill(Color.BLACK);
		layerCastOne.setOpacity(.5);
		StackPane levelOne = new StackPane(mapImageOne,layerCastOne,levelOneText);
		// Map 2 option
		Text levelTwoText = new Text("Sandy Sea");
		levelTwoText.setFont(Font.font("Krungthep", 60));
		levelTwoText.setFill(Color.WHITE);
		Rectangle mapImageTwo = new Rectangle(0,0,1200,200);
		mapImageTwo.setFill(new ImagePattern(new Image("file:map2/MapOverview.png")));
		Rectangle layerCastTwo = new Rectangle(0,0, 1200, 200);
		layerCastTwo.setFill(Color.BLACK);
		layerCastTwo.setOpacity(.5);
		StackPane levelTwo = new StackPane(mapImageTwo,layerCastTwo,levelTwoText);
		// Map 3 option
		Text levelThreeText = new Text("Deadland");
		levelThreeText.setFont(Font.font("Krungthep", 60));
		levelThreeText.setFill(Color.WHITE);
		Rectangle mapImageThree = new Rectangle(0,0,1200,200);
		mapImageThree.setFill(new ImagePattern(new Image("file:map3/MapOverview.png")));
		Rectangle layerCastThree = new Rectangle(0,0, 1200, 200);
		layerCastThree.setFill(Color.BLACK);
		layerCastThree.setOpacity(.5);
		StackPane levelThree = new StackPane(mapImageThree,layerCastThree,levelThreeText);
		VBox levelSelection = new VBox(levelOne,levelTwo,levelThree);
		levelSelection.setPadding(new Insets(15));
		
		// Mouse handlers turn text red when user
		// hovers over an option
		// Turns text grey if user clicks on an option
		// then handles option
		// LEVEL ONE
		levelOne.setOnMouseEntered(p-> {
			levelOneText.setFill(Color.RED);
		});
		levelOne.setOnMouseExited(p-> {
			levelOneText.setFill(Color.WHITE);
		});
		levelOne.setOnMousePressed(p-> {
			levelOneText.setFill(Color.GREY);
		});
		levelOne.setOnMouseReleased(p-> {
			level = 1;
			if(newGame) {
				controller.newGame(1);
			}else {
				controller.differentLevel(1);
			}
			launchLevel();
		});
		// LEVEL TWO
		levelTwo.setOnMouseEntered(p-> {
			levelTwoText.setFill(Color.RED);
		});
		levelTwo.setOnMouseExited(p-> {
			levelTwoText.setFill(Color.WHITE);
		});
		levelTwo.setOnMousePressed(p-> {
			levelTwoText.setFill(Color.GREY);
		});
		levelTwo.setOnMouseReleased(p-> {
			level = 2;
			if(newGame) {
				controller.newGame(2);
			}else {
				controller.differentLevel(2);
			}
			launchLevel();
		});
		// LEVEL THREE
		levelThree.setOnMouseEntered(p-> {
			levelThreeText.setFill(Color.RED);
		});
		levelThree.setOnMouseExited(p-> {
			levelThreeText.setFill(Color.WHITE);
		});
		levelThree.setOnMousePressed(p-> {
			levelThreeText.setFill(Color.GREY);
		});
		levelThree.setOnMouseReleased( p-> {
			level = 3;
			if(newGame) {
				controller.newGame(3);
			}else {
				controller.differentLevel(3);
			}
			launchLevel();
		});
		
		this.stage.setScene(new Scene(levelSelection,WIDTH,HEIGHT));
		this.stage.setResizable(false);
	}
    	
	/**
	 * Once a level is chosen, game is rendered and handlers are generated for 
	 * user input
	 */
	public void launchLevel() {
		// Rendering game
		StackPane allLayers = controller.getAllLayers();
		Scene scene = new Scene(allLayers,WIDTH,HEIGHT);
		stage.setScene(scene);
		// Playing filler music
		Media media = null;
		if(level == 1) {
			File music = new File(System.getProperty("user.dir") + "/Sounds/ISawTheLight.mp3");
			media = new Media(music.toURI().toString()); 
		}else if(level == 2) {
			File music = new File(System.getProperty("user.dir") + "/Sounds/AngelChild.m4a");
			media = new Media(music.toURI().toString()); 
		}else {
			File music = new File(System.getProperty("user.dir") + "/Sounds/ThemChanges.m4a");
			media = new Media(music.toURI().toString()); 
		}
		
		backtrack = new MediaPlayer(media); 
		backtrack.setAutoPlay(true);
		backtrack.play();
		backtrack.setVolume(.5);
	      
	    isPaused = false;
		// When a user presses a key, the key code is added to a set. The animation
		// timer (which is running every 100 nano seconds or so) will check for specific
		// keys in set (up left right A) and proceed with the corresponding action. When 
		// a player releases a key, the key code is then removed from the set and no longer in
		// action.		
		Set<KeyCode> keyInput = new HashSet<>();
		scene.setOnKeyPressed(e -> {
			keyInput.add(e.getCode());
		});
		scene.setOnKeyReleased(e ->{
			keyInput.remove(e.getCode());
			if(e.getCode().equals(KeyCode.RIGHT) || e.getCode().equals(KeyCode.LEFT)) {
				controller.endMove();
			}
			if(e.getCode().equals(KeyCode.SPACE)) {
				isPaused = !isPaused;
			}
		});
		// Jump requires extra boolean flag in order to keep user from "double jumping".
		// If enableJump=true, and a user clicks UP, enableJump is set to false a jump 
		// animation will play and once it is complete it sets enableJump back to true.		
		enableJump = true;
		
		// DownAnimation used for simulating Mario falling. Stops when Mario hits the
		// floor or collides with an impassable object
		downPlaying = false;
		AnimationTimer DownAnimation = new AnimationTimer() {
			public int tick = 0;
			private int dy;
			private void stopDown() {
				tick = 0;
				enableJump = true;
				downPlaying = false;
				this.stop();
			}
			@Override
			public void handle(long time) {
				dy = tick + 1;
				if(!controller.bottomCollision(time)) {
					stopDown();
				}else {
					controller.move(0,dy);
					tick++;
				}
				
			}
		};
		
		// UpAnimation used for simulating Mario jumping. Stops when Mario hits
		// peak of jump (or collides with an impassable object) then triggers the
		// DownAnimation for the fall
		upPlaying = false;
		AnimationTimer UpAnimation = new AnimationTimer() {
			int tick = 0;
			int dy;
			private void stopUp() {
				tick = 0;
				upPlaying = false;
				this.stop();
			}
			@Override
			public void handle(long time) {
				dy = tick - 30;
				if(tick > 30 || !controller.topCollision(time)) {
					stopUp();
					downPlaying = true;
					DownAnimation.start();
				}else {
					controller.move(0,dy);
					tick++;
				}
			}
		};
		
		// Handles collisions, left/right/up/down movement and enemy movement
		movement = new AnimationTimer() {
			boolean jumpStop = false;
			boolean fallStop = false;
			StackPane pauseScreen;
			@Override
			public void handle(long nanoTime) {
				// Paused
				if(isPaused) {
					if(upPlaying) {
						UpAnimation.stop();
						jumpStop = true;
					}
					if(downPlaying) {
						DownAnimation.stop();
						fallStop = true;
					}
					if(pauseScreen == null) {
						pauseScreen = setPause(allLayers);
					}
				// Not Paused
				}else {
					// Setting correct animations
					if(pauseScreen != null) {
						allLayers.getChildren().remove(pauseScreen);
						pauseScreen = null;
					}
					if(jumpStop) {
						UpAnimation.start();
						jumpStop = false;
					}
					if(fallStop) {
						DownAnimation.start();
						fallStop = false;
					}
					// Showing lose box
	   				if(controller.Lose()) {
						gameOver(false,allLayers);
						this.stop();
						return;
					}
	   				// Showing win box 
					if(controller.Win()) {
						this.stop();
						level++;
						if (level <= 3) {
							backtrack.stop();
							controller.differentLevel(level);
							launchLevel();
						} else {
							gameOver(true,allLayers);
						}
					}
					
					// If mario is attacking, disable all other moves
					if(controller.isAttacking()) {
						keyInput.remove(KeyCode.RIGHT);
						keyInput.remove(KeyCode.LEFT);
						keyInput.remove(KeyCode.UP);
						keyInput.remove(KeyCode.A);
					}
					
					// If mario is not already attack start attacking
					if(keyInput.contains(KeyCode.S)) {
						keyInput.remove(KeyCode.RIGHT);
						keyInput.remove(KeyCode.LEFT);
						keyInput.remove(KeyCode.UP);
						keyInput.remove(KeyCode.A);
						if(!controller.isAttacking()) {
							controller.attack();
						}
					}
					// Moves Enemies
					controller.moveEnemies(nanoTime);
					
					// Detects and handles horizontal and top collisions
					controller.leftCollision(nanoTime);
					controller.rightCollision(nanoTime);
					controller.topCollision(nanoTime);
					// Detects bottom collisions and starts fall animation when
					// Mario falls of a platform
					if(controller.bottomCollision(nanoTime)) {
						enableJump = false;
						downPlaying = true;
						DownAnimation.start();
					}
					
					// Moves character
					if(keyInput.contains(KeyCode.LEFT)) {
						controller.move(-5, 0);
					}
					if(keyInput.contains(KeyCode.RIGHT)) {
						controller.move(5, 0);
					}	
					if(keyInput.contains(KeyCode.UP) && enableJump) {
						enableJump = false;
						upPlaying = true;
						UpAnimation.start();
					}
					
					// Triggers character fireball
					if(keyInput.contains(KeyCode.A)) {
						controller.fireBall(nanoTime);
					}
				}
			}
		};
		movement.start();
		
	}	
	/**
	 * Handles when game is over. Displays victory/loss message and buttons to
	 * start again or go to main menu
	 * @param victory True if win, false otherwise
	 * @param allLayers Layers to add visual on top of
	 */
	public void gameOver(boolean victory,StackPane allLayers) {
		// Creating game over box visual
		StackPane gameOver = new StackPane();
		Rectangle rect = new Rectangle(200,200, 800, 500);
		rect.setFill(Color.BLACK);
		rect.setOpacity(.5);
		
		Text text = new Text();
		if(victory) {
			text.setText("YOU WIN!");
		}else {
			text.setText("GAME OVER");
		}
		text.setFont(Font.font("Krungthep", 60));
		text.setFill(Color.WHITE);
		
		Text retry = new Text("Play again?");
		retry.setFont(Font.font("Krungthep", 30));
		retry.setFill(Color.WHITE);
		retry.setTranslateY(70);
		
		Text menu = new Text("Main Menu");
		menu.setFont(Font.font("Krungthep", 30));
		menu.setFill(Color.WHITE);
		menu.setTranslateY(110);
		
		gameOver.getChildren().addAll(rect,text,retry,menu);
		allLayers.getChildren().add(gameOver);
		
		// Mouse handlers turn text red when user
		// hovers over an option
		// Turns text grey if user clicks on an option
		// then handles option
		// RETRY
		retry.setOnMouseEntered(p-> {
			retry.setFill(Color.RED);
		});
		retry.setOnMouseExited(p-> {
			retry.setFill(Color.WHITE);
		});
		retry.setOnMousePressed(p-> {
			retry.setFill(Color.GREY);
		});
		retry.setOnMouseClicked(e -> {
			backtrack.stop();
			retry.setFill(Color.WHITE);
			controller.differentLevel(level);
			launchLevel();
			allLayers.getChildren().remove(gameOver);
		});
		// MENU
		menu.setOnMouseEntered(p-> {
			menu.setFill(Color.RED);
		});
		menu.setOnMouseExited(p-> {
			menu.setFill(Color.WHITE);
		});
		menu.setOnMousePressed(p-> {
			menu.setFill(Color.GREY);
		});
		menu.setOnMouseClicked(e ->{
			backtrack.stop();
			menu.setFill(Color.WHITE);
			// If user won, use their current mario
			if(victory) {
				displayMainMenu(false);
			// If user lost, generate new mario
			}else {
				displayMainMenu(true);
			}
			allLayers.getChildren().remove(gameOver);
		});
	}
	
	/**
	 * Generates pause screen and handlers for pause options (saving/main menu)
	 * @param allLayers Layer to add visual on top of
	 * @return StackPane of pause screen
	 */
	public StackPane setPause(StackPane allLayers) {
		// Creating pauseScreen
		StackPane pauseScreen = new StackPane();
		Rectangle rect = new Rectangle(200,200, 800, 500);
		rect.setFill(Color.BLACK);
		rect.setOpacity(.5);
		
		Text text = new Text("GAME PAUSED");
		text.setFont(Font.font("Krungthep", 60));
		text.setFill(Color.WHITE);
		
		Text save = new Text("Save");
		save.setFont(Font.font("Krungthep", 30));
		save.setFill(Color.WHITE);
		save.setTranslateY(70);
		
		Text menu = new Text("Main Menu");
		menu.setFont(Font.font("Krungthep", 30));
		menu.setFill(Color.WHITE);
		menu.setTranslateY(110);
		
		pauseScreen.getChildren().addAll(rect,text,save,menu);
		allLayers.getChildren().add(pauseScreen);
		
		// Mouse handlers turn text red when user
		// hovers over an option
		// Turns text grey if user clicks on an option
		// then handles option
		// SAVE
		save.setOnMouseEntered(p-> {
			save.setFill(Color.RED);
		});
		save.setOnMouseExited(p-> {
			save.setFill(Color.WHITE);
		});
		save.setOnMousePressed(p-> {
			save.setFill(Color.GREY);
		});
		save.setOnMouseClicked(e -> {
			controller.saveGame();
		});
		// MENU
		menu.setOnMouseEntered(p-> {
			menu.setFill(Color.RED);
		});
		menu.setOnMouseExited(p-> {
			menu.setFill(Color.WHITE);
		});
		menu.setOnMousePressed(p-> {
			menu.setFill(Color.GREY);
		});
		menu.setOnMouseClicked(e ->{
			backtrack.stop();
			movement.stop();
			displayMainMenu(false);
			allLayers.getChildren().remove(pauseScreen);
		});
		return pauseScreen;
	}
} 