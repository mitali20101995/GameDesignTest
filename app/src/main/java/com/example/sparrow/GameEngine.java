package com.example.sparrow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameEngine extends SurfaceView implements Runnable {
    private final String TAG = "SPARROW";

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;

    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    // Screen resolution varaibles
    private int screenWidth;
    private int screenHeight;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;

    // SPRITES
    Square bullet;
    int SQUARE_WIDTH = 100;
    Point cagePosition;


    Square enemy;

    Sprite player;
    Sprite sparrow;
    Sprite cat;
    //Sprite cage;

    ArrayList<Square> bullets = new ArrayList<Square>();

    // GAME STATS
    int score = 0;

    final int DISTANCE_FROM_BOTTOM = 850;
    final int CAGE_WIDTH = 250;
    final int CAGE_HEIGHT = 200;

    boolean movingRightCat = true;
    boolean movingRightCage = true;
    final int CAGE_SPEED = 150;
    final int CAT_SPEED = 140;

    public GameEngine(Context context, int screenW, int screenH) {
        super(context);

        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        // set screen height and width
        this.screenWidth = screenW;
        this.screenHeight = screenH;




        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);

        // set initial position
        cagePosition = new Point();



        // initalize sprites
        this.player = new Sprite(this.getContext(), 100, 700, R.drawable.player64);
        this.sparrow = new Sprite(this.getContext(), 500, 200, R.drawable.bird64);
        this.cat = new Sprite(this.getContext(), 1574,664,R.drawable.cat64);

    }

    @Override
    public void run() {
        while (gameIsRunning == true) {
            updateGame();    // updating positions of stuff
            redrawSprites(); // drawing the stuff
            controlFPS();
        }
    }

    // Game Loop methods
    public void updateGame() {

            int catX = this.cat.getxPosition();

            //CAGE
            if (movingRightCage == true)
            {
                            cagePosition.x = cagePosition.x + CAGE_SPEED;
            }

            else
                {
                            cagePosition.x = cagePosition.x - CAGE_SPEED;
                }

            //  Collision detection code
            if (cagePosition.x > screenWidth) {
                            Log.d(TAG, "Cage reached right of screen. Changing direction!");
                            movingRightCage = false;
            }

            if (cagePosition.x < 0) {
                            Log.d(TAG, "Cage reached left of screen. Changing direction!");
                            movingRightCage = true;
            }


            Log.d(TAG, "Cage x-position: " + cagePosition.x);



            //cat
            if (movingRightCat == true)
            {
                catX = catX + CAT_SPEED;
            }
            else
            {
                catX = catX - CAT_SPEED;
            }
            //  Collision detection code
            if (catX > (screenWidth / 3)) {
                Log.d(TAG, "Cat reached right of screen. Changing direction!");
                movingRightCat = false;
            }

            if (catx < 0) {
                Log.d(TAG, "Cat reached left of screen. Changing direction!");
                movingRightCat = true;
            }
            Log.d(TAG, "Cat x-position: " + catX);
    }


    public void outputVisibleArea() {
        Log.d(TAG, "DEBUG: The visible area of the screen is:");
        Log.d(TAG, "DEBUG: Maximum w,h = " + this.screenWidth +  "," + this.screenHeight);
        Log.d(TAG, "DEBUG: Visible w,h =" + VISIBLE_RIGHT + "," + VISIBLE_BOTTOM);
        Log.d(TAG, "-------------------------------------");
    }



    public void redrawSprites() {
        if (holder.getSurface().isValid()) {

            // initialize the canvas
            canvas = holder.lockCanvas();
            // --------------------------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,255,255,255));

            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);

            // --------------------------------------------------------
            // draw boundaries of the visible space of app
            // --------------------------------------------------------
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.argb(255, 0, 128, 0));

            canvas.drawRect(VISIBLE_LEFT, VISIBLE_TOP, VISIBLE_RIGHT, VISIBLE_BOTTOM, paintbrush);
            this.outputVisibleArea();

            // --------------------------------------------------------
            // draw player and sparrow
            // --------------------------------------------------------

            // 1. player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. sparrow
            canvas.drawBitmap(this.sparrow.getImage(), this.sparrow.getxPosition(), this.sparrow.getyPosition(), paintbrush);


            //cat
            canvas.drawBitmap(this.cat.getImage(),this.cat.getxPosition(), this.cat.getyPosition(), paintbrush);

            //Draw the sprites rectangle cage
            int cageLeft = (this.screenWidth - 500) - CAGE_WIDTH;
            int cageTop = (this.screenHeight - DISTANCE_FROM_BOTTOM - CAGE_HEIGHT);
            int cageRight = (this.screenWidth - 500) + CAGE_WIDTH;
            int cageBottom = (this.screenHeight - DISTANCE_FROM_BOTTOM);
            canvas.drawRect(cageLeft, cageTop, cageRight, cageBottom, paintbrush);
            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);


            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            paintbrush.setTextSize(60);
            paintbrush.setStrokeWidth(5);
            String screenInfo = "Screen size: (" + this.screenWidth + "," + this.screenHeight + ")";
            canvas.drawText(screenInfo, 10, 100, paintbrush);

            // --------------------------------
            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {

        }
    }


    // Deal with user input
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
       }
        return true;
    }

    // Game status - pause & resume
    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    public void  resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}

