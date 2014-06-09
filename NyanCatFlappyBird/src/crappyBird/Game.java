package crappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Game extends JPanel {

	static int HEIGHT = 800; // height of the window
	static int WIDTH = 600; // width of the window
	BirdMan birdy = new BirdMan(); // makes a new bird
	Wall wall = new Wall(WIDTH); // makes the first wall you see
	Wall wall2 = new Wall(WIDTH + (WIDTH / 2)); // makes the second wall you see
	static int score = 0; // the score (how many walls you've passed)
	int scrollX = 0; // scrolls the background
	static boolean dead = false; // used to reset the walls
	static String deathMessage = ""; // "you died, try again";
	ArrayList<Integer> xSpots = new ArrayList<Integer>();
	ArrayList<Integer> ySpots = new ArrayList<Integer>();
	static boolean deadAnimating = false;

	// grabs the background from Imgur
	BufferedImage img = null;
	BufferedImage img2 = null;
	Animation deathAnimation = new Animation();
	BufferedImage img3 = null;
	{
		try {
			img = ImageIO
					.read(new URL(
							"https://lh5.googleusercontent.com/-X_Xaf8-kPDk/U0A0Kbcz2FI/AAAAAAAAAJs/RzGN0bVhriw/s150-c/photo.jpg"));

			img2 = ImageIO
					.read(new URL(
							"http://media2.doink.com/thumbnail/7bdddc0c-b720-4689-ab8b-5bbda14cf18f/thumbnail.png"));
			img3 = ImageIO.read((new java.net.URL(getClass().getResource(
					"GR-000-smoke01.png"), "GR-000-smoke01.png")));
		} catch (IOException e) {
			System.out.println("WRONG BACKGROUND"); // prints "WRONG BACKGROUND"
													// if there is an issue
													// obtaining the background
		}
	}

	public void animate() {
		deathAnimation.update(1);
	}

	public Game() {

		BufferedImage[] animation = new BufferedImage[16];
		// this mouseAdapter just listens for clicks, whereupon it then tells
		// the bird to jump
		int o = 0;
		for (int k = 0; k < img3.getHeight(); k += img3.getHeight() / 4)
			for (int i = 0; i < img3.getWidth(); i += img3.getWidth() / 4)
				animation[o++] = img3.getSubimage(i, k, img3.getWidth() / 4,
						img3.getHeight() / 4);
		for (int i = 0; i < 16; i++) {
			deathAnimation.addFrame(animation[i], 10);
		}
		this.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0) {
				birdy.jump();
			}

		});

	}

	@SuppressWarnings("static-access")
	public void paint(Graphics g) {
		super.paint(g);

		if (!deadAnimating) {
			for (int i = 0; i < HEIGHT; i += img.getHeight())
				for (int k = 0; k < WIDTH * 3; k += img.getWidth())
					g.drawImage(img, scrollX + k, i, null); // there are two
															// backgrounds so
			// you get that seamless
			// transition, this is the first

			for (int i = 0; i < HEIGHT; i += img.getHeight())
				for (int k = 0; k < WIDTH * 3; k += img.getWidth())
					g.drawImage(img, scrollX + k + 1800, i, null); // number 2,
																	// exactly
																	// one
																	// background
																	// length
																	// away
																	// (1800
																	// pixels)

			int i = 0;
			for (int j = xSpots.size() - 1; j >= 0; j--) {
				g.drawImage(img2, xSpots.get(j), ySpots.get(i), birdy.DIAMETER,
						birdy.DIAMETER, null);
				i++;

			}

			wall.paint(g); // paints the first wall
			wall2.paint(g); // the second wall
			birdy.paint(g); // the wee little birdy
			xSpots.add(birdy.X - (xSpots.size() * 7));
			ySpots.add(birdy.y);
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
			g.drawString("" + score, WIDTH / 2, 700);

		}

		else if (deadAnimating) {
			for (int i = 0; i < HEIGHT; i += img.getHeight())
				for (int k = 0; k < WIDTH * 3; k += img.getWidth())
					g.drawImage(img, k, i, null); // there are two
													// backgrounds so
			// you get that seamless
			// transition, this is the first

			wall.paint(g); // paints the first wall
			wall2.paint(g); // the second wall
			xSpots.clear();
			ySpots.clear();
			g.setFont(new Font("comicsans", Font.BOLD, 40));
			g.drawString(deathMessage, 200, 200); // paints "" if the player has
													// not
													// just died, paints
													// "you died, try again" if
													// the
													// user just died

			g.drawImage(deathAnimation.getImage(), birdy.X, birdy.y, null);
			animate();
			if (deathAnimation.getFrameNum() == 15) {
				animate();
				deadAnimating = false;
				score = 0;
				wall.died();
				wall2.died();
				birdy.reset();

			}
		}

	}

	@SuppressWarnings("static-access")
	public void move() {
		if (!deadAnimating) {
			wall.move(); // moves the first wall
			wall2.move(); // moves the second wall
			birdy.move(); // moves the wee little birdy
			//
		}
		scrollX += Wall.speed; // scrolls the wee little background

		if (scrollX == -1800) // this loops the background around after it's
								// done
			scrollX = 0;

		if (dead) { // this block essentially pushes the walls back 600 pixels
					// on birdy death
			wall.x = 600;
			wall2.x = 600 + (WIDTH / 2);
			dead = false;
		}

		if ((wall.x == BirdMan.X) || (wall2.x == BirdMan.X)) // Increments the
																// score when
																// the player
																// passes a wall
			score();
	}

	public static void score() {
		score += 1;
	}

}