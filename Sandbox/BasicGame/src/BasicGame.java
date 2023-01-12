import nl.saxion.app.SaxionApp;
import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;
import nl.saxion.app.interaction.MouseEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BasicGame implements GameLoop {

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 1000, 1000, 40);
    }

    Laser shootlaser;
    ArrayList<Asteroid> asteroids;
    ArrayList<Asteroid> asteroidsFromBottom;
    ArrayList<Asteroid> asteroidsFromLeft;
    ArrayList<Asteroid> asteroidsFromRight;
    boolean playerAlive = true;
    //asteroid spawn rate
    int spawnTimer = 2000;

    @Override
    public void init() {
            //Sound and Start screen
            SaxionApp.playSound("Sandbox/BasicGame/src/Sounds/Destiny OST 26 Untold Legends.wav", true);
            SaxionApp.drawImage("Sandbox/BasicGame/src/Images/StartScreen.png", 0, 0, 900, 580);
            SaxionApp.pause();

            shootlaser = new Laser();
            shootlaser.x = spaceship.x + 10;
            shootlaser.y = spaceship.y;
            shootlaser.a = spaceship.a;

            asteroids = new ArrayList<Asteroid>();
            asteroidsFromBottom = new ArrayList<Asteroid>();
            asteroidsFromLeft = new ArrayList<Asteroid>();
            asteroidsFromRight = new ArrayList<Asteroid>();
            setAsteroids(playerAlive, spawnTimer);
    }

    @Override
    public void loop() {

            SaxionApp.clear();
            SaxionApp.drawImage("Sandbox/BasicGame/src/Images/background.png", 0, 0, 1500, 1000);
            // Draw Asteroids to move across screen
            for(Asteroid asteroid1 : asteroids){
                SaxionApp.drawImage(asteroid1.filename, asteroid1.x, asteroid1.y, 100, 100);
                asteroid1.x = asteroid1.x + 2;
                asteroid1.y = asteroid1.y + 4;
            }
            for(Asteroid asteroid2 : asteroidsFromBottom){
                SaxionApp.drawImage(asteroid2.filename, asteroid2.x, asteroid2.y, 100, 100);
                asteroid2.x = asteroid2.x + 2;
                asteroid2.y = asteroid2.y - 4;
            }
            for(Asteroid asteroidL : asteroidsFromLeft){
                SaxionApp.drawImage(asteroidL.filename, asteroidL.x, asteroidL.y, 100, 100);
                asteroidL.x = asteroidL.x + 4;
                asteroidL.y = asteroidL.y + 2;
            }
            for(Asteroid asteroidR : asteroidsFromRight){
                SaxionApp.drawImage(asteroidR.filename, asteroidR.x, asteroidR.y, 100, 100);
                asteroidR.x = asteroidR.x - 4;
                asteroidR.y = asteroidR.y - 5;
            }
    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) {

    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) {

        if(mouseEvent.isLeftMouseButton()){
            int n = 1;
            SaxionApp.drawImage(shootlaser.laserImage, shootlaser.x, shootlaser.y, 10, 10);
            for(int i=0; i<n; i++){
                if(shootlaser.x > 500 || shootlaser.x < 0){
                    SaxionApp.clear();
                    if(shootlaser.y > 500 || shootlaser.y < 0){
                        SaxionApp.clear();
                    }
                }
            }
        }

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
                    asteroids.add(asteroid);
                }if(playerAlive){
                    Asteroid asteroidB = new Asteroid();
                    asteroidB.x = SaxionApp.getRandomValueBetween(0,SaxionApp.getWidth());
                    asteroidB.y = SaxionApp.getHeight()+50;
                    asteroidsFromBottom.add(asteroidB);
                }
                if(playerAlive){
                    Asteroid asteroidL = new Asteroid();
                    asteroidL.x = -50;
                    asteroidL.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidsFromLeft.add(asteroidL);
                }
                if(playerAlive){
                    Asteroid asteroidR = new Asteroid();
                    asteroidR.x = SaxionApp.getWidth()+50;
                    asteroidR.y = SaxionApp.getRandomValueBetween(0, SaxionApp.getHeight());
                    asteroidsFromRight.add(asteroidR);
                }

            }
        };
        //Edit spawnTimer period to change how fast asteroids spawn
        timer.scheduleAtFixedRate(task,0,spawnTimer);

    }
}

