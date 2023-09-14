package application;
	
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Main extends Application {
	
	
	/*Code class to show the number of seconds the 
	/player has to look at the set of visible tile.*/
	private static final long DURATION_SECONDS = 3;
	
	//the start time of the start time.
	private ScheduledExecutorService timerThread = Executors.newSingleThreadScheduledExecutor();
	
	private VBox root;
	
	private Pane tilePane;
	
	private List<TileView> tileSequence = new ArrayList<>();
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setScene(new  Scene(createcontent()));
		stage.show();
	}
	
	@Override 
	public void stop() throws Exception {
		timerThread.shutdownNow();
	}
	private Parent createcontent() {
		root = new VBox();
		root.setPrefSize(1280, 720 + 100);
		
		
		var button = new Button("Start");
		button.setOnAction(e -> startGame());	

		root.getChildren().addAll(new Pane(), button);
		
		return root;
	}
	//This will allow us to repeat the game over and over again
	private void startGame() {
		
		tilePane = populateGrid();
		
		root.getChildren().set(0, tilePane);
		
		timerThread.schedule(() -> {
			tilePane.getChildren()
			.stream()
			.map(n ->  (TileView)n)
			.forEach(TileView::hide);
			
		}, DURATION_SECONDS, TimeUnit.SECONDS); 
	}
	//if the player start game the tile numbers will be visible, lets make its invisible first.
	private  Pane  populateGrid() {
		
		var Pane = new Pane();
		Pane.setPrefSize(1280, 720);
		
		Random random = new Random();
		
		List<Point2D> usedpoints = new ArrayList<>();
		
		// input any number of integer in i <= ?. to make the guessing fun.. 
	for (int i = 1; i <= 9; i++) {
		int randomX = random.nextInt(1280 / 80);
		int randomY = random.nextInt(720 / 80);
		
		Point2D p = new Point2D(randomX, randomY);
		
		while (usedpoints.contains(p)) {
			randomX = random.nextInt(1280 / 80);
			randomY = random.nextInt(720 / 80);
			
			p = new Point2D(randomX, randomY);
		}
		
		usedpoints.add(p);
		
		var tile = new TileView(Integer.toString(i));
		
		tile.setTranslateX(randomX * 80);
		tile.setTranslateY(randomY * 80);
		tile.setOnMouseClicked(e ->{
			//exiting the application.
			if(tileSequence.isEmpty()) {
				System.out.println(" Game Over!");
				/*System.exit(1);*/	return; 
			}
			
			//checking if we get all tiles correctly.
			var correctTile = tileSequence.remove(0);
				if (tile == correctTile) {
					tile.show();	
			} else {
				tileSequence.clear();
				System.out.print(" Abandone all hope ");
			}
			
		});
			Pane.getChildren().add(tile);
			//contains the number of tiles used in the game.
			tileSequence.add(tile);
			
		}
		return Pane;
	}
	// this class preview the tile set for the game.
	private static class TileView extends StackPane {
		
		private Text text;
		
		
		TileView(String content) {
			var border = new Rectangle(64, 64, null);
			border.setStroke(Color.BLACK);
			border.setStrokeWidth(4);
			border.setStrokeType(StrokeType.INSIDE);
			
			
			text = new Text(content);
			text.setFont(Font.font(40));
			
			
			getChildren().addAll(border, text);
			// otherwise the mouse will go through the tile
			setPickOnBounds(true);
		
		}
		// making the mouse visible when clicking.
		void hide() {
			text.setVisible(false);
		}
		
		void show() {
			text.setVisible(true);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
