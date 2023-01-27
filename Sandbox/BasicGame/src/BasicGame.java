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
        SaxionApp.startGameLoop(new BasicGame(), screenWidth - 50, screenHeight - 100, 40);
    }

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screenSize.width - 50;
    int screenHeight = screenSize.height - 50;
    String currentScreen = "menu";
    Ship spaceship;
    ArrayList<Alien> alienShip;
    ArrayList<Laser> shootLaser;
    ArrayList<Asteroid> asteroids;
    ArrayList<Asteroid> asteroidsFromBottom;
    ArrayList<Asteroid> asteroidsFromLeft;
    ArrayList<Asteroid> asteroidsFromRight;
    ArrayList<Health> healthSpawn;
    ArrayList<Integer> finalScore = new ArrayList<>();
    boolean playerAlive = true;
    //asteroid spawn rate
    int spawnTimer = 2000;
    //Number of lives
    int lives;
    int score = 0;


    @Override
    public void init() {
        finalScore.add(0);
        SaxionApp.setTextDrawingColor(Color.WHITE);
    }

    @Override
    public void loop() {
        if (currentScreen.equals("menu")) {
            menu();
        }else if (currentScreen.equals("game")){
            game();
        } else if(currentScreen.equals("deathScreen")){
            deathScreen();
        }else if(currentScreen.equals("leaderboard")){
            leaderboard();
        }
    }

    public void menu() {
        SaxionApp.clear();
        //SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Destiny OST 26 Untold Legends.wav", true);
        SaxionApp.drawImage("Sandbox/BasicGame/src/Images/GameMenu.png", 0, 0, screenWidth, screenHeight);
    }

    public void game() {
        SaxionApp.clear();
        SaxionApp.drawImage("Sandbox/BasicGame/src/Images/background.png", 0, 0, screenWidth, screenHeight);
        SaxionApp.drawText("Lives: " + lives, 50, 50, 30);
        SaxionApp.drawText("Score: " + score, screenWidth - 250, 50, 30);

        //Screen Boundaries
        if (spaceship.x >= screenSize.width - 100) {
            spaceship.x = screenSize.width - 100;
        } else if (spaceship.x <= 0) {
            spaceship.x = 0;
        } else if (spaceship.y >= screenSize.height - 150) {
            spaceship.y = screenSize.height - 150;
        } else if (spaceship.y <= 0) {
            spaceship.y = 0;
        }

        // Draw Asteroids to move across screen
        drawAsteroid(asteroids, 2, 4);
        drawAsteroid(asteroidsFromBottom, 2, -4);
        drawAsteroid(asteroidsFromLeft, 4,4);
        drawAsteroid(asteroidsFromRight, -4, -5);
        //Draw alienShip
        for(Alien alien : alienShip){
            SaxionApp.drawImage(alien.imageFile,alien.a,alien.b,50,50);
            alien.b = alien.b+5;
            alien.boundingBox.x = alien.a;
            alien.boundingBox.y = alien.b;
        }
        //Asteroid out of bounds
        asteroidOutOfBounds(asteroids, screenWidth, screenHeight);
        asteroidOutOfBounds(asteroidsFromBottom, 0, -50);
        asteroidOutOfBounds(asteroidsFromRight, 0, -50);
        asteroidOutOfBounds(asteroidsFromLeft, screenWidth, screenHeight);

        //Draws health and moves it across screen
        for (Health health : healthSpawn) {
            SaxionApp.drawImage(health.filename, health.x, health.y, 50, 50);
            health.y = health.y + 4;
            health.boundingBox.x = health.x;
            health.boundingBox.y = health.y;
        }



        //Collision detection between ship and hearts
        for (int i = 0; i < healthSpawn.size(); i++) {
            Health hearts = healthSpawn.get(i);
            if (spaceship.boundingBox.intersects(hearts.boundingBox)) {
                lives = lives + 1;
                SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Explosion Sound Effect.wav");
                SaxionApp.drawImage("Sandbox/BasicGame/src/Images/explosion.png", hearts.x, hearts.y, 50, 50);
                healthSpawn.remove(hearts);
            }
        }

        //Collision detection from laser to asteroids
        laserShootDestroy(asteroids);
        laserShootDestroy(asteroidsFromBottom);
        laserShootDestroy(asteroidsFromLeft);
        laserShootDestroy(asteroidsFromRight);

        // Move spaceship to the direction
        spaceship.x += Math.cos(Math.toRadians(spaceship.a)) * spaceship.move;
        spaceship.y += Math.sin(Math.toRadians(spaceship.a)) * spaceship.move;
        spaceship.boundingBox.x = spaceship.x + 5;
        spaceship.boundingBox.y = spaceship.y + 5;
        // Rotate spaceship
        SaxionApp.transformRotate(spaceship.a, spaceship.x + 25, spaceship.y + 25);
        //Draw spaceship
        SaxionApp.drawImage(spaceship.imageFile, spaceship.x, spaceship.y, 50, 50);
        // Stop the spaceship after one move
        spaceship.move = 0;

        //Collision detection from asteroid to ship
        asteroidCollision(asteroids);
        asteroidCollision(asteroidsFromBottom);
        asteroidCollision(asteroidsFromLeft);
        asteroidCollision(asteroidsFromRight);
        laserOutOfBounds(shootLaser);
        alienShipCollision(alienShip);
        if (lives <= 0) {
            currentScreen = "deathScreen";
            playerAlive=false;
        }

    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) {
        if (currentScreen.equals("menu")) {
            menuKeyboardEvent(keyboardEvent);
        } else {
            gameKeyboardEvent(keyboardEvent);
        }
    }

    public void gameKeyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.isKeyPressed()) {
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_LEFT) {
                // rotation to left side
                spaceship.a = spaceship.a - 10;
            }
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_RIGHT) {
                // rotation to right side
                spaceship.a = spaceship.a + 10;
            }
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_UP) {
                spaceship.move = spaceship.move + 15;
            }
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE) {
                Laser bullets = new Laser();
                bullets.x = spaceship.x;
                bullets.y = spaceship.y;
                bullets.a = spaceship.a;
                bullets.move = spaceship.move + 10;
                bullets.boundingBox = new Rectangle(bullets.x, bullets.y, 40, 40);
                SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Laser Gun Sound Effect.wav");
                shootLaser.add(bullets);
            }
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_0) {
                SaxionApp.quit();
            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_ESCAPE){
                currentScreen = "menu";
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
                if (lives>0) {
                    Asteroid asteroid = new Asteroid();
                    asteroid.x = SaxionApp.getRandomValueBetween(0, SaxionApp.getWidth());
                    asteroid.y = -50;
                    asteroid.boundingBox = new Rectangle(asteroid.x, asteroid.y, 45, 54);
                    asteroids.add(asteroid);

                    Asteroid asteroidB = new Asteroid();
                    asteroidB.x = SaxionApp.getRandomValueBetween(0, SaxionApp.getWidth());
                    asteroidB.y = SaxionApp.getHeight() + 50;
                    asteroidB.boundingBox = new Rectangle(asteroidB.x, asteroidB.y, 45, 45);
                    asteroidsFromBottom.add(asteroidB);

                    Asteroid asteroidL = new Asteroid();
                    asteroidL.x = -50;
                    asteroidL.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidL.boundingBox = new Rectangle(asteroidL.x, asteroidL.y, 45, 45);
                    asteroidsFromLeft.add(asteroidL);

                    Asteroid asteroidR = new Asteroid();
                    asteroidR.x = SaxionApp.getWidth() + 50;
                    asteroidR.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidR.boundingBox = new Rectangle(asteroidR.x, asteroidR.y, 45, 45);
                    asteroidsFromRight.add(asteroidR);

                    Alien alien = new Alien();
                    alien.a = SaxionApp.getRandomValueBetween(0,SaxionApp.getWidth());;
                    alien.b = -50;
                    alien.boundingBox= new Rectangle(alien.a,alien.b,50,50);
                    alienShip.add(alien);
                }
            }
        };
        //Edit spawnTimer period to change how fast asteroids spawn
        timer.scheduleAtFixedRate(task, 0, spawnTimer);

    }

    public void menuKeyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.isKeyPressed()) {
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_1) {
                resetGame();
                currentScreen = "game";
            }
            if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_0) {
                SaxionApp.quit();
            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_2){
                currentScreen = "leaderboard";
            }
            if(keyboardEvent.getKeyCode() == KeyboardEvent.VK_ESCAPE){
                currentScreen = "menu";
            }
        }
    }

    public void resetGame() {
        //Sound and Start screen
        SaxionApp.clear();

        spawnTimer = 2000;
        //Number of lives
        score = 0;
        lives=2;
        playerAlive = true;
        // initialize the player, laser and objects
        spaceship = new Ship();
        spaceship.x = screenWidth / 2;
        spaceship.y = screenHeight / 2;
        spaceship.a = 270;
        spaceship.move = 0;
        spaceship.boundingBox = new Rectangle(spaceship.x, spaceship.y, 30, 30);
        // Note: <Asteroid> replaced with <>

        asteroids = new ArrayList<>();
        asteroidsFromBottom = new ArrayList<>();
        asteroidsFromLeft = new ArrayList<>();
        asteroidsFromRight = new ArrayList<>();
        setAsteroids(playerAlive, spawnTimer);
        alienShip = new ArrayList<>();
        healthSpawn = new ArrayList<>();
        setLives(playerAlive);
        shootLaser = new ArrayList<>();

    }

    //Detects asteroids collision with spaceship and removes asteroid
    public void asteroidCollision(ArrayList<Asteroid> asteroids) {
        for (int i = 0; i < asteroids.size(); i++) {
            Asteroid asteroidCollision = asteroids.get(i);
            if (spaceship.boundingBox.intersects(asteroidCollision.boundingBox)) {
                lives = lives - 1;
                SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Explosion Sound Effect.wav");
                SaxionApp.drawImage("Sandbox/BasicGame/src/Images/explosion.png", spaceship.x, spaceship.y, 50, 50);
                asteroids.remove(asteroidCollision);
            }
        }
    }

    public void alienShipCollision(ArrayList<Alien> alien) {
        for (int i = 0; i < alienShip.size(); i++) {
            Alien alienShipCollision = alien.get(i);
            if (spaceship.boundingBox.intersects(alienShipCollision.boundingBox)) {
                lives = lives - 1;
                SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Explosion Sound Effect.wav");
                SaxionApp.drawImage("Sandbox/BasicGame/src/Images/explosion.png", spaceship.x, spaceship.y, 50, 50);
                alien.remove(alienShipCollision);
            }
        }
    }


    //Collision detection between laser and asteroids, will remove image when hit
    public void laserShootDestroy(ArrayList<Asteroid> asteroids) {
        for (Laser shoots : shootLaser) {
            shoots.x += Math.cos(Math.toRadians(shoots.a)) * shoots.move;
            shoots.y += Math.sin(Math.toRadians(shoots.a)) * shoots.move;
            shoots.boundingBox.x = shoots.x;
            shoots.boundingBox.y = shoots.y;
            // Rotate laser with spaceship
            SaxionApp.transformRotate(shoots.a, shoots.x + 25, shoots.y + 25);
            SaxionApp.drawImage("Sandbox/BasicGame/src/Images/bluelaser.png", shoots.x, shoots.y, 50, 50);
            Asteroid asteroidCollision;
            for (int i = 0; i < asteroids.size(); i++) {
                asteroidCollision = asteroids.get(i);
                if (shoots.boundingBox.intersects(asteroidCollision.boundingBox)) {
                    score = score + 50;
                    SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Explosion Sound Effect.wav");
                    SaxionApp.drawImage("Sandbox/BasicGame/src/Images/explosion.png", shoots.x, shoots.y, 50, 50);
                    asteroids.remove(asteroidCollision);
                }
            }
        }

    }

    //Timer method to add lives to an array that will be used to draw image
    public void setLives(boolean playerAlive) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (lives>0) {
                    Health setLives = new Health();
                    setLives.x = SaxionApp.getRandomValueBetween(0, SaxionApp.getWidth());
                    setLives.y = -50;
                    setLives.boundingBox = new Rectangle(setLives.x, setLives.y, 45, 54);
                    healthSpawn.add(setLives);
                }

            }
        };
        int randomSpawn = SaxionApp.getRandomValueBetween(20000, 120000);
        //Edit spawnTimer period to change how fast asteroids spawn
        timer.scheduleAtFixedRate(task, 0, randomSpawn);
    }

    //Deletes asteroids chosen from array when they leave the screen bounds, x & y are the screen bounds
    public void asteroidOutOfBounds(ArrayList<Asteroid> a, int x, int y) {
        for (int i = 0; i < a.size(); i++) {
            Asteroid asteroid = a.get(i);
            // change variables to adjust when asteroids get deleted from array
            if (a == asteroidsFromLeft || a == asteroids) {
                if (asteroid.x > x || asteroid.y > y) {
                    asteroids.remove(asteroid);
                }
            }
            if (a == asteroidsFromBottom || a == asteroidsFromRight) {
                if (asteroid.x < x || asteroid.y < y) {
                    asteroids.remove(asteroid);
                }
            }
        }
    }

    public void drawAsteroid(ArrayList<Asteroid> a, int x, int y){
        for (Asteroid asteroid1 : a) {
            SaxionApp.drawImage(asteroid1.filename, asteroid1.x, asteroid1.y, 100, 100);
            asteroid1.x = asteroid1.x + x;
            asteroid1.y = asteroid1.y + y;
            asteroid1.boundingBox.x = asteroid1.x;
            asteroid1.boundingBox.y = asteroid1.y;
        }
    }


    public void laserOutOfBounds(ArrayList<Laser> a) {
        for (int i = 0; i < a.size(); i++) {
            Laser laser = a.get(i);
            // change variables to adjust when asteroids get deleted from array
            if (laser.x < 0 || laser.x > screenWidth || laser.y < 0 || laser.y > screenHeight) {
                a.remove(laser);
            }
        }
    }

    public void deathScreen(){
        SaxionApp.clear();
        SaxionApp.drawImage("Sandbox/BasicGame/src/Images/background.png", 0, 0, screenWidth, screenHeight);
        SaxionApp.drawText("GAME OVER!", screenWidth/2 - 75, screenHeight/2 -50, 30);
        SaxionApp.drawText("Your Score: " + score, screenWidth/2 - 100, screenHeight/2, 30);
        if(score > finalScore.get(0)) {
            finalScore.remove(0);
            finalScore.add(0, score);
        }
        int first = finalScore.get(0);

        SaxionApp.drawText("Top Score: " + first, screenWidth/2 - 90, screenHeight/2+50, 30);
        SaxionApp.drawText("Press ESC to return to main menu", screenWidth/2 - 225, screenHeight -200, 30);
    }
    public void leaderboard(){
        SaxionApp.clear();
        SaxionApp.drawImage("Sandbox/BasicGame/src/Images/background.png", 0, 0, screenWidth, screenHeight);
        SaxionApp.drawText("Top Score", screenWidth/2 - 80, screenHeight/2-50, 30);
        SaxionApp.drawText(" "+finalScore.get(0), screenWidth/2 - 80, screenHeight/2, 30);
        SaxionApp.drawText("Press ESC to return to main menu", screenWidth/2 - 225, screenHeight -200, 30);
    }


}




