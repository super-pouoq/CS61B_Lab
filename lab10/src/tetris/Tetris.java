package tetris;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TERenderer;
import tileengine.Tileset;

import java.util.*;

/**
 *  Provides the logic for Tetris.
 *
 *  @author Erik Nelson, Omar Yu, Noah Adhikari, Jasmine Lin
 */

public class Tetris {

    private static int WIDTH = 10;
    private static int HEIGHT = 40;

    // Tetrominoes spawn above the area we display, so we'll have our Tetris board have a
    // greater height than what is displayed.
    private static int GAME_HEIGHT = 25;

    // Contains the tiles for the board.
    private TETile[][] board;

    // Helps handle movement of pieces.
    private Movement movement;

    // Checks for if the game is over.
    private boolean isGameOver;

    // The current Tetromino that can be controlled by the player.
    private Tetromino currentTetromino;

    // The current game's score.
    private int score;

    /**
     * Checks for if the game is over based on the isGameOver parameter.
     * @return boolean representing whether the game is over or not
     */
    private boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Renders the game board and score to the screen.
     */
    private void renderBoard() {
        ter.drawTiles(board);
        renderScore();
        StdDraw.show();

    }

    /**
     * Creates a new Tetromino and updates the instance variable
     * accordingly. Flags the game to end if the top of the board
     * is filled and the new piece cannot be spawned.
     */
    private void spawnPiece() {
        // The game ends if this tile is filled
        if (board[9][19] != Tileset.NOTHING) {
            isGameOver = true;
        }

        // Otherwise, spawn a new piece and set its position to the spawn point
        currentTetromino = Tetromino.values()[bagRandom.getValue()];
        currentTetromino.reset();
    }

    /**
     * Updates the board based on the user input. Makes the appropriate moves
     * depending on the user's input.
     */
    private void updateBoard() {
        Tetromino t = currentTetromino;
        while(t == null){
            spawnPiece();
            if(isGameOver()){
                return;
            }
            t = currentTetromino;
        }

        handleInput();

        if (actionDeltaTime() > 500) {
            // 检查是否可以继续下落
            if (movement.canMove(0, -1)) {
                movement.tryMove(0, -1); // 继续下落
            } else {
                // 无法下落，固定方块
                lockPiece();
                spawnPiece();
            }
            resetActionTimer();
            return;
        }

        Tetromino.draw(t, board, t.pos.x, t.pos.y);
    }

    private void lockPiece() {
        // 将当前方块固定在board上
        Tetromino t = currentTetromino;
        for (int tx = 0; tx < t.width; tx++) {
            for (int ty = 0; ty < t.height; ty++) {
                if (t.shape[tx][ty]) {
                    int x = t.pos.x + tx;
                    int y = t.pos.y + ty;
                    if (x >= 0 && x < WIDTH && y >= 0 && y < GAME_HEIGHT) {
                        board[x][y] = t.tile;
                    }
                }
            }
        }
        fillAux(); // 同步到辅助数组
    }

    private void handleInput() {
        // 支持持续按键检测
        if (StdDraw.isKeyPressed('a') || StdDraw.isKeyPressed('A')) {
            movement.tryMove(-1, 0);
            StdDraw.pause(50); // 防止移动过快
        }
        if (StdDraw.isKeyPressed('d') || StdDraw.isKeyPressed('D')) {
            movement.tryMove(1, 0);
            StdDraw.pause(50);
        }
        if (StdDraw.isKeyPressed('s') || StdDraw.isKeyPressed('S')) {
            movement.tryMove(0, -1);
            StdDraw.pause(50);
        }

        // 一次性按键
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            switch (key) {
                case 'w':
                case 'W':
                case ' ':
                    movement.rotateRight();
                    break;
            }
        }
    }

    /**
     * Increments the score based on the number of lines that are cleared.
     *
     * @param linesCleared
     */
    private void incrementScore(int linesCleared) {
        if(linesCleared==1)score+=100;
        if(linesCleared==2)score+=300;
        if(linesCleared==3)score+=500;
        if(linesCleared==4)score+=800;
    }

    /**
     * Clears lines/rows on the provided tiles/board that are horizontally filled.
     * Repeats this process for cascading effects and updates score accordingly.
     * @param tiles
     */
    public void clearLines(TETile[][] tiles) {
        int linesCleared = 0;
        int width = tiles.length;
        int height = tiles[0].length;

        boolean hasFullLine;
        do {
            hasFullLine = false;
            for (int i = height - 1; i >= 0; i--) {
                boolean isFull = true;
                for (int j = 0; j < width; j++) {
                    if (tiles[j][i].equals(Tileset.NOTHING)) {
                        isFull = false;
                        break;
                    }
                }

                if (isFull) {
                    linesCleared++;
                    hasFullLine = true;

                    // 将上方的行下移
                    for (int k = i; k < height - 1; k++) {
                        for (int j = 0; j < width; j++) {
                            tiles[j][k] = tiles[j][k + 1];
                        }
                    }

                    // 最顶行设为空
                    for (int j = 0; j < width; j++) {
                        tiles[j][height - 1] = Tileset.NOTHING;
                    }
                    break; // 退出当前循环，重新开始检查
                }
            }
        } while (hasFullLine); // 继续直到没有满行

        incrementScore(linesCleared);
        fillAux();
    }

    /**
     * Where the game logic takes place. The game should continue as long as the game isn't
     * over.
     */
    public void runGame() {
        resetActionTimer();
        spawnPiece(); // 初始生成一个方块
        while (!isGameOver()) {
            updateBoard();          // 处理输入、移动、可能锁定方块
            clearLines(board);      // ←←← 在这里调用！检查并清除满行
            renderBoard();          // 渲染画面（含分数）
            StdDraw.pause(10);      // 控制帧率（可选，避免 CPU 占用过高）
        }

        // 游戏结束后可显示 "Game Over"
        renderBoard();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "GAME OVER");
        StdDraw.show();

    }

    /**
     * Renders the score using the StdDraw library.
     */
    private void renderScore() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH - 3, HEIGHT - 0.5, "Score: " + score);
    }

    /**
     * Use this method to run Tetris.
     * @param args
     */
    public static void main(String[] args) {
        long seed = args.length > 0 ? Long.parseLong(args[0]) : (new Random()).nextLong();
        Tetris tetris = new Tetris(seed);
        tetris.runGame();
    }

    /**
     * Everything below here you don't need to touch.
     */

    // This is our tile rendering engine.
    private final TERenderer ter = new TERenderer();

    // Used for randomizing which pieces are spawned.
    private Random random;
    private BagRandomizer bagRandom;

    private long prevActionTimestamp;
    private long prevFrameTimestamp;

    // The auxiliary board. At each time step, as the piece moves down, the board
    // is cleared and redrawn, so we keep an auxiliary board to track what has been
    // placed so far to help render the current game board as it updates.
    private TETile[][] auxiliary;
    private boolean auxFilled;

    public Tetris() {
        board = new TETile[WIDTH][GAME_HEIGHT];
        auxiliary = new TETile[WIDTH][GAME_HEIGHT];
        random = new Random(new Random().nextLong());
        bagRandom = new BagRandomizer(random, Tetromino.values().length);
        auxFilled = false;
        movement = new Movement(WIDTH, GAME_HEIGHT, this);
        fillBoard(Tileset.NOTHING);
        fillAux();
    }

    public Tetris(long seed) {
        board = new TETile[WIDTH][GAME_HEIGHT];
        auxiliary = new TETile[WIDTH][GAME_HEIGHT];
        random = new Random(seed);
        bagRandom = new BagRandomizer(random, Tetromino.values().length);
        auxFilled = false;
        movement = new Movement(WIDTH, GAME_HEIGHT, this);

        ter.initialize(WIDTH, HEIGHT);
        fillBoard(Tileset.NOTHING);
        fillAux();
    }

    // Setter and getter methods.

    /**
     * Returns the current game board.
     * @return
     */
    public TETile[][] getBoard() {
        return board;
    }

    /**
     * Returns the score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the current auxiliary board.
     * @return
     */
    public TETile[][] getAuxiliary() {
        return auxiliary;
    }


    /**
     * Returns the current Tetromino/piece.
     * @return
     */
    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    /**
     * Sets the current Tetromino to null.
     * @return
     */
    public void setCurrentTetromino() {
        currentTetromino = null;
    }

    /**
     * Sets the boolean auxFilled to true;
     */
    public void setAuxTrue() {
        auxFilled = true;
    }

    /**
     * Fills the entire board with the specific tile that is passed in.
     * @param tile
     */
    private void fillBoard(TETile tile) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = tile;
            }
        }
    }

    /**
     * Copies the contents of the src array into the dest array using
     * System.arraycopy.
     * @param src
     * @param dest
     */
    private static void copyArray(TETile[][] src, TETile[][] dest) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, src[0].length);
        }
    }

    /**
     * Copies over the tiles from the game board to the auxiliary board.
     */
    public void fillAux() {
        copyArray(board, auxiliary);
    }

    /**
     * Copies over the tiles from the auxiliary board to the game board.
     */
    private void auxToBoard() {
        copyArray(auxiliary, board);
    }

    /**
     * Calculates the delta time with the previous action.
     * @return the amount of time between the previous Tetromino movement with the present
     */
    private long actionDeltaTime() {
        return System.currentTimeMillis() - prevActionTimestamp;
    }

    /**
     * Resets the action timestamp to the current time in milliseconds.
     */
    private void resetActionTimer() {
        prevActionTimestamp = System.currentTimeMillis();
    }

}
