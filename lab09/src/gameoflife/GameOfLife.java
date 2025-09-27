package gameoflife;

import edu.princeton.cs.algs4.StdDraw;
import jdk.internal.foreign.StringSupport;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Am implementation of Conway's Game of Life using StdDraw.
 * Credits to Erik Nelson, Jasmine Lin and Elana Ho for
 * creating the assignment.
 */
public class GameOfLife {

    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 50;
    private static final String SAVE_FILE = "src/save.txt";
    private long prevFrameTimestep;
    private TERenderer ter;
    private Random random;
    private TETile[][] currentState;
    private int width;
    private int height;

    /**
     * Initializes our world.
     * @param seed
     */
    public GameOfLife(long seed) {
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        ter = new TERenderer();
        ter.initialize(width, height);
        random = new Random(seed);
        TETile[][] randomTiles = new TETile[width][height];
        fillWithRandomTiles(randomTiles);
        currentState = randomTiles;
    }

    /**
     * Constructor for loading in the state of the game from the
     * given filename and initializing it.
     * @param filename
     */
    public GameOfLife(String filename) {
        this.currentState = loadBoard(filename);
        ter = new TERenderer();
        ter.initialize(width, height);
    }

    /**
     * Constructor for loading in the state of the game from the
     * given filename and initializing it. For testing purposes only, so
     * do not modify.
     * @param filename
     */
    public GameOfLife(String filename, boolean test) {
        this.currentState = loadBoard(filename);
    }

    /**
     * Initializes our world without using StdDraw. For testing purposes only,
     * so do not modify.
     * @param seed
     */
    public GameOfLife(long seed, boolean test) {
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        random = new Random(seed);
        TETile[][] randomTiles = new TETile[width][height];
        fillWithRandomTiles(randomTiles);
        currentState = randomTiles;
    }

    /**
     * Initializes our world with a given TETile[][] without using StdDraw.
     * For testing purposes only, so do not modify.
     * @param tiles
     * @param test
     */
    public GameOfLife(TETile[][] tiles, boolean test) {
        TETile[][] transposeState = transpose(tiles);
        this.currentState = flip(transposeState);
        this.width = tiles[0].length;
        this.height = tiles.length;
    }

    /**
     * Flips the matrix along the x-axis.
     * @param tiles
     * @return
     */
    private TETile[][] flip(TETile[][] tiles) {
        int w = tiles.length;
        int h = tiles[0].length;

        TETile[][] rotateMatrix = new TETile[w][h];
        int y = h - 1;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                rotateMatrix[i][y] = tiles[i][j];
            }
            y--;
        }
        return rotateMatrix;//上下颠倒
    }

    /**
     * Transposes the tiles.
     * @param tiles
     * @return
     */
    private TETile[][] transpose(TETile[][] tiles) {
        int w = tiles[0].length;
        int h = tiles.length;

        TETile[][] transposeState = new TETile[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                transposeState[x][y] = tiles[y][x];
            }
        }
        return transposeState;
    }

    /**
     * Runs the game. You don't have to worry about how this method works.
     * DO NOT MODIFY THIS METHOD!
     */
    public void runGame() {
        boolean paused = false;
        long evoTimestamp = System.currentTimeMillis();
        long pausedTimestamp = System.currentTimeMillis();
        long clickTimestamp = System.currentTimeMillis();
        while (true) {
            if (!paused && System.currentTimeMillis() - evoTimestamp > 250) {
                evoTimestamp = System.currentTimeMillis();
                currentState = nextGeneration(currentState);
            }
            if (System.currentTimeMillis() - prevFrameTimestep > 17) {
                prevFrameTimestep = System.currentTimeMillis();

                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                int tileX = (int) mouseX;
                int tileY = (int) mouseY;

                TETile currTile = currentState[tileX % width][tileY % height];

                if (StdDraw.isMousePressed() && System.currentTimeMillis() - clickTimestamp > 250) {
                    clickTimestamp = System.currentTimeMillis();
                    if (currTile == Tileset.CELL) {
                        currentState[tileX][tileY] = Tileset.NOTHING;
                    } else {
                        currentState[tileX][tileY] = Tileset.CELL;
                    }
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && System.currentTimeMillis() - pausedTimestamp > 500) {
                    pausedTimestamp = System.currentTimeMillis();
                    paused = !paused;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_S)) {
                    saveBoard();
                    System.exit(0);
                }
                ter.renderFrame(currentState);
            }
        }
    }


    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public void fillWithRandomTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = randomTile();
            }
        }
    }

    /**
     * Fills the 2D array of tiles with NOTHING tiles.
     * @param tiles
     */
    public void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Selects a random tile, with a 50% change of it being a CELL
     * and a 50% change of being NOTHING.
     */
    private TETile randomTile() {
        // The following call to nextInt() uses a bound of 3 (this is not a seed!) so
        // the result is bounded between 0, inclusive, and 3, exclusive. (0, 1, or 2)
        int tileNum = random.nextInt(2);
        return switch (tileNum) {
            case 0 -> Tileset.CELL;
            default -> Tileset.NOTHING;
        };
    }

    /**
     * Returns the current state of the board.
     * @return
     */
    public TETile[][] returnCurrentState() {
        return currentState;
    }

    /**
     * At each timestep, the transitions will occur based on the following rules:
     *  1.Any live cell with fewer than two live neighbors dies, as if by underpopulation.
     *  2.Any live cell with two or three neighbors lives on to the next generation.
     *  3.Any live cell with more than three neighbors dies, as if by overpopulation,
     *  4.Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
     * @param tiles
     * @return
     */
    public TETile[][] nextGeneration(TETile[][] tiles) {
        TETile[][] nextGen = new TETile[width][height];
        // The board is filled with Tileset.NOTHING
        fillWithNothing(nextGen);

        for(int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                if (tiles[i][j].equals(Tileset.NOTHING)) {
                    int sum = countNeighbors(tiles, i, j, width, height);
                    if (sum == 3) {
                        nextGen[i][j] = Tileset.CELL;
                    }
                }
                if (tiles[i][j].equals(Tileset.CELL)) {
                    int sum = countNeighbors(tiles, i, j, width, height);
                    if (sum == 3 || sum == 2) {
                        nextGen[i][j] = Tileset.CELL;
                    }
                }
            }
        }
        //DONE
        return nextGen;
    }

    private int countNeighbors(TETile[][] tiles,int x,int y,int width,int height){
        int sum = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (dx == 0 && dy == 0) continue;

                // 检查是否在 [0, width) × [0, height) 范围内
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (Tileset.CELL.equals(tiles[nx][ny])) {
                        sum++;
                    }
                }
            }
        }
        return sum;
    }

    /**
     * Helper method for saveBoard without rendering and running the game.
     * @param tiles
     */
    public void saveBoard(TETile[][] tiles) {
        TETile[][] transposeState = transpose(tiles);
        this.currentState = flip(transposeState);
        this.width = tiles[0].length;
        this.height = tiles.length;
        saveBoard();
    }

    /**
     * Saves the state of the current state of the board into the
     * save.txt file (make sure it's saved into this specific file).
     * 0 represents NOTHING, 1 represents a CELL.
     */
    public void saveBoard() {
        try {
            List<String> lines = new ArrayList<>();
            TETile[][] haha=flip(this.currentState);
            // 第一行：width height
            lines.add(this.width + " " + this.height);

            // 逐行遍历 currentState
            for (int i = 0; i < this.height; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < this.width; j++) {
                    if (haha[j][i].equals(Tileset.NOTHING)) {
                        line.append('0');
                    } else if (haha[j][i].equals(Tileset.CELL)) {
                        line.append('1');
                    } else {
                        // 可选：处理未知图块（根据需求决定是否保留）
                        line.append('0'); // 默认当作 NOTHING
                    }
                }
                lines.add(line.toString());
            }

            Path savePath = Paths.get(SAVE_FILE);
            Files.write(
                    savePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            System.err.println("保存游戏板失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the board from filename and returns it in a 2D TETile array.
     * 0 represents NOTHING, 1 represents a CELL.
     */
    public TETile[][] loadBoard(String filename) {
        try {
            // 读取所有行
            List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);

            if (lines.isEmpty()) {
                throw new IOException("文件为空");
            }

            // 解析第一行：width 和 height
            String[] dims = lines.get(0).trim().split("\\s+");
            if (dims.length < 2) {
                throw new IOException("第一行格式错误：应为 'width height'");
            }

            int width = Integer.parseInt(dims[0]);
            int height = Integer.parseInt(dims[1]);
            this.width = width;
            this.height = height;

            // 创建 TETile 二维数组
            TETile[][] board = new TETile[height][width];

            // 从第二行开始解析每一行的 0/1 数据
            for (int i = 0; i < height; i++) {//
                String rowStr = lines.get(i + 1).trim();
                for (int j = 0; j < width; j++) {
                    char c = rowStr.charAt(j);
                    if (c == '0') {
                        board[j][i] = Tileset.NOTHING;
                    } else if (c == '1') {
                        board[j][i] = Tileset.CELL;
                    }
                }
            }

            // ✅ 根据你的代码逻辑，需要 flip
            return flip(board);

        } catch (IOException | NumberFormatException e) {
            System.err.println("加载存档失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This is where we run the program. DO NOT MODIFY THIS METHOD!
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            // Read in the board from a file.
            if (args[0].equals("-l")) {
                GameOfLife g = new GameOfLife(args[1]);
                g.runGame();
            }
            System.out.println("Verify your program arguments!");
            System.exit(0);
        } else {
            long seed = args.length > 0 ? Long.parseLong(args[0]) : (new Random()).nextLong();
            GameOfLife g = new GameOfLife(seed);
            g.runGame();
        }
    }
}
