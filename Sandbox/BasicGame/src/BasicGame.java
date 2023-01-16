import nl.saxion.app.SaxionApp;
import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BasicGame implements GameLoop {

    public static void main(String[] args) {
        //Used to identify any size of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        SaxionApp.startGameLoop(new BasicGame(), screenWidth-50, screenHeight-100, 40);
    }

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width;
    int screenHeight = screenSize.height;
    Ship spaceship;
    ArrayList<Asteroid> asteroids;
    ArrayList<Asteroid> asteroidsFromBottom;
    ArrayList<Asteroid> asteroidsFromLeft;
    ArrayList<Asteroid> asteroidsFromRight;
    boolean playerAlive = true;
    //asteroid spawn rate
    int spawnTimer = 2000;
    //Number of lives
    int lives = 5;
    int score = 0;

    @Override
    public void init() {
            //Sound and Start screen
            SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Destiny OST 26 Untold Legends.wav", true);
            SaxionApp.drawImage("Sandbox/BasicGame/src/Images/GameMenu.png", 0, 0, screenWidth, screenHeight);
            SaxionApp.pause();


                SaxionApp.clear();
                // initialize the player, laser and objects
                spaceship = new Ship();
                spaceship.x = 250;
                spaceship.y = 350;
                spaceship.a = 10;
                spaceship.move = 0;
                // Note: <Asteroid> replaced with <>
                asteroids = new ArrayList<>();
                asteroidsFromBottom = new ArrayList<>();
                asteroidsFromLeft = new ArrayList<>();
                asteroidsFromRight = new ArrayList<>();
                setAsteroids(playerAlive, spawnTimer);
            }


    @Override
    public void loop() {

        SaxionApp.clear();
        SaxionApp.drawImage("Sandbox/BasicGame/src/Images/background.png", 0, 0, screenWidth, screenHeight);
        SaxionApp.drawText("Lives: " + lives, 50, 50, 30);
        // Draw Asteroids to move across screen
        for (Asteroid asteroid1 : asteroids) {
            SaxionApp.drawImage(asteroid1.filename, asteroid1.x, asteroid1.y, 100, 100);
            asteroid1.x = asteroid1.x + 2;
            asteroid1.y = asteroid1.y + 4;
            asteroid1.boundingBox.x = asteroid1.x;
            asteroid1.boundingBox.y = asteroid1.y;
        }
        for (Asteroid asteroid2 : asteroidsFromBottom) {
            SaxionApp.drawImage(asteroid2.filename, asteroid2.x, asteroid2.y, 100, 100);
            asteroid2.x = asteroid2.x + 2;
            asteroid2.y = asteroid2.y - 4;
            asteroid2.boundingBox.x = asteroid2.x;
            asteroid2.boundingBox.y = asteroid2.y;
        }
        for (Asteroid asteroidL : asteroidsFromLeft) {
            SaxionApp.drawImage(asteroidL.filename, asteroidL.x, asteroidL.y, 100, 100);
            asteroidL.x = asteroidL.x + 4;
            asteroidL.y = asteroidL.y + 2;
            asteroidL.boundingBox.x = asteroidL.x;
            asteroidL.boundingBox.y = asteroidL.y;

        }
        for (Asteroid asteroidR : asteroidsFromRight) {
            SaxionApp.drawImage(asteroidR.filename, asteroidR.x, asteroidR.y, 100, 100);
            asteroidR.x = asteroidR.x - 4;
            asteroidR.y = asteroidR.y - 5;
            asteroidR.boundingBox.x = asteroidR.x;
            asteroidR.boundingBox.y = asteroidR.y;
        }
        if (spaceship.x >= 850) {
            spaceship.x = 850;
        }
        if (spaceship.x <= 0) {
            spaceship.x = 0;
        }
        if (spaceship.y >= 530) {
            spaceship.y = 530;
        }
        if (spaceship.y <= 0) {
            spaceship.y = 0;
        }

        // Move spaceship to the direction
        spaceship.x += Math.cos(Math.toRadians(spaceship.a)) * spaceship.move;
        spaceship.y += Math.sin(Math.toRadians(spaceship.a)) * spaceship.move;
        // Rotate spaceship
        SaxionApp.transformRotate(spaceship.a, spaceship.x + 25, spaceship.y + 25);
        //Draw spaceship
        SaxionApp.drawImage(spaceship.imageFile, spaceship.x, spaceship.y, 50, 50);
        // Stop the spaceship after one move
        spaceship.move = 0;

        //Collision detection from asteroid to ship
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroidCollision = asteroids.get(i);
            if (spaceship.boundingBox.intersects(asteroidCollision.boundingBox)) {
                lives = lives - 1;
                SaxionApp.drawImage("Sandbox/BasicGame/Images/explosion.png", spaceship.x, spaceship.y, 50, 50);
                asteroids.remove(asteroidCollision);
            }
        }

            //Deletes asteroids from top from array when they leave the screen bounds
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid asteroid = asteroids.get(j);
                // change variables to adjust when asteroids get deleted from array
                if (asteroid.x > screenWidth || asteroid.y > screenHeight) {
                    asteroids.remove(asteroid);
                }
            }
                for (int k = 0; k < asteroids.size(); k++) {
                    Asteroid asteroid = asteroidsFromBottom.get(k);
                    // change variables to adjust when asteroids get deleted from array
                    if (asteroid.x < 0 || asteroid.y < 0) {
                        asteroids.remove(asteroid);
                    }
                }
                for (int y = 0; y < asteroids.size(); y++) {
                    Asteroid asteroid = asteroidsFromRight.get(y);
                    // change variables to adjust when asteroids get deleted from array
                    if (asteroid.x < screenWidth || asteroid.y < 0) {
                        asteroids.remove(asteroid);
                    }
                }
                for (int x = 0; x < asteroids.size(); x++) {
                    Asteroid asteroid = asteroidsFromLeft.get(x);
                    // change variables to adjust when asteroids get deleted from array
                    if (asteroid.x > screenWidth || asteroid.y > screenHeight || asteroid.y < 0) {
                        asteroids.remove(asteroid);
                    }
                }
            }




    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.isKeyPressed()){
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_LEFT){
                // rotation to left side
                spaceship.a=spaceship.a-10;
            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_RIGHT){
                // rotation to right side
                spaceship.a=spaceship.a+10;
            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_UP){
                spaceship.move =spaceship.move+15;

            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_0){
                SaxionApp.quit();
            }


        }
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) {

    }
    public void setAsteroids(boolean playerAlive, int spawnTimer) {
        //Timer required to add asteroid to arraylist after n seconds
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(playerAlive) {
                    Asteroid asteroid = new Asteroid();
                    asteroid.x = SaxionApp.getRandomValueBetween(0, SaxionApp.getWidth());
                    asteroid.y = -50;
                    asteroid.boundingBox = new Rectangle(asteroid.x + 5, asteroid.y + 5, 40, 40);
                    asteroids.add(asteroid);
                }if(playerAlive){
                    Asteroid asteroidB = new Asteroid();
                    asteroidB.x = SaxionApp.getRandomValueBetween(0,SaxionApp.getWidth());
                    asteroidB.y = SaxionApp.getHeight()+50;
                    asteroidB.boundingBox = new Rectangle(asteroidB.x + 5, asteroidB.y + 5, 40, 40);
                    asteroidsFromBottom.add(asteroidB);
                }
                if(playerAlive){
                    Asteroid asteroidL = new Asteroid();
                    asteroidL.x = -50;
                    asteroidL.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidL.boundingBox = new Rectangle(asteroidL.x + 5, asteroidL.y + 5, 40, 40);
                    asteroidsFromLeft.add(asteroidL);
                }
                if(playerAlive){
                    Asteroid asteroidR = new Asteroid();
                    asteroidR.x = SaxionApp.getWidth()+50;
                    asteroidR.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidR.boundingBox = new Rectangle(asteroidR.x + 5, asteroidR.y + 5, 40, 40);
                    asteroidsFromRight.add(asteroidR);
                }

            }
        };
        //Edit spawnTimer period to change how fast asteroids spawn
        timer.scheduleAtFixedRate(task,0,spawnTimer);

    }
}

