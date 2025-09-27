package tetris;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 *  Provides the logic for movement of Tetris pieces.
 *
 *  @author Erik Nelson, Omar Yu, and Jasmine Lin
 */

public class Movement {

    private int WIDTH;

    private int GAME_HEIGHT;

    Tetris tetris;

    public Movement (int width, int game_height, Tetris tetris) {
        this.WIDTH = width;
        this.GAME_HEIGHT = game_height;
        this.tetris = tetris;
    }

    /**
     * Rotate the current Tetromino 90 degrees to the right (clockwise).
     */
    public void rotateRight() {
        rotate(Rotation.RIGHT);
    }

    /**
     * Rotate the current Tetromino 90 degrees to the left (counter-clockwise).
     */
    public void rotateLeft() {
        rotate(Rotation.LEFT);
    }

    /**
     * Attempts to move the current Tetromino by a shift of deltaX and deltaY.
     * If the Tetromino cannot move and will collide with a boundary or existing piece,
     * it is placed at its current position and nullified so a new Tetromino can spawn.
     * @param deltaX
     * @param deltaY
     */
    public void tryMove(int deltaX, int deltaY) {
        Tetromino t = tetris.getCurrentTetromino();

        if (canMove(deltaX, deltaY)) {
            Tetromino.clear(t, tetris.getBoard(), t.pos.x, t.pos.y);
            t.pos.x += deltaX;
            t.pos.y += deltaY;
            Tetromino.draw(t, tetris.getBoard(), t.pos.x, t.pos.y);
        } else {
            if (deltaY < 0) {
                TETile[][] board = tetris.getBoard();
                Tetromino.draw(t, board, t.pos.x, t.pos.y);
                tetris.fillAux();
                tetris.setAuxTrue();
                tetris.setCurrentTetromino();
            }
        }
    }

    /**
     * Checks whether moving the current Tetromino by a shift of deltaX and deltaY
     * is valid, i.e. within bounds and does not collide with other pieces.
     * @param deltaX
     * @param deltaY
     * @return a boolean representing if the move is possible or not
     */
    public boolean canMove(int deltaX, int deltaY) {
        Tetromino t = tetris.getCurrentTetromino();

        for (int tx = 0; tx < t.width; tx++){
            for (int ty = 0; ty < t.height; ty++){
                if (t.shape[tx][ty]) {
                    int oldX = t.pos.x + tx;
                    int oldY = t.pos.y + ty;
                    int newX = oldX + deltaX;
                    int newY = oldY + deltaY;

                    // Out of bounds check
                    if (newX >= WIDTH || newX < 0 || newY >= GAME_HEIGHT || newY < 0) {
                        return false;
                    }

                    // Board check - exclude current tetromino's position
                    TETile[][] board = tetris.getBoard();
                    if (board[newX][newY] != Tileset.NOTHING) {
                        // Check if this occupied tile is part of the current tetromino
                        boolean isPartOfCurrentTetromino = false;
                        for (int ttx = 0; ttx < t.width; ttx++) {
                            for (int tty = 0; tty < t.height; tty++) {
                                if (t.shape[ttx][tty]) {
                                    int checkX = t.pos.x + ttx;
                                    int checkY = t.pos.y + tty;
                                    if (checkX == newX && checkY == newY) {
                                        isPartOfCurrentTetromino = true;
                                        break;
                                    }
                                }
                            }
                            if (isPartOfCurrentTetromino) break;
                        }

                        // Only return false if it's not part of current tetromino
                        if (!isPartOfCurrentTetromino) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Moves the current Tetromino down one tile, if not able to move down,
     * set the block in place and allow for a new Tetromino to be spawned.
     */
    public void dropDown() {
        Tetromino t = tetris.getCurrentTetromino();

        if (canMove(0, -1)) {
            Tetromino.clear(t, tetris.getBoard(), t.pos.x, t.pos.y);
            t.pos.y -= 1;
            Tetromino.draw(t, tetris.getBoard(), t.pos.x, t.pos.y);
        } else {
            TETile[][] board = tetris.getBoard();
            Tetromino.draw(t, board, t.pos.x, t.pos.y);
            tetris.fillAux();
            tetris.setCurrentTetromino();
            tetris.setAuxTrue();
        }
    }

    /**
     * Checks whether rotating the current Tetromino is valid,
     * i.e. it will remain within bounds and does not rotate/collide into
     * other pieces.
     * @param newShape
     * @return a boolean representing if the rotation is possible or not
     */
    public boolean canRotate(boolean[][] newShape) {
        Tetromino t = tetris.getCurrentTetromino();

        // 获取当前方块占用的所有位置
        Set<Point> currentPositions = new HashSet<>();
        for (int tx = 0; tx < t.width; tx++) {
            for (int ty = 0; ty < t.height; ty++) {
                if (t.shape[tx][ty]) {
                    currentPositions.add(new Point(t.pos.x + tx, t.pos.y + ty));
                }
            }
        }

        // 检查新形状的每个部分
        for (int tx = 0; tx < newShape.length; tx++) {
            for (int ty = 0; ty < newShape[0].length; ty++) {
                if (newShape[tx][ty]) {
                    int newX = t.pos.x + tx;
                    int newY = t.pos.y + ty;

                    // 边界检查
                    if (newX < 0 || newY < 0
                            || newX >= tetris.getAuxiliary().length
                            || newY >= tetris.getAuxiliary()[0].length) {
                        return false;
                    }

                    // 检查board，但排除当前方块原有的位置
                    if (tetris.getAuxiliary()[newX][newY] != Tileset.NOTHING
                            && !currentPositions.contains(new Point(newX, newY))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Rotation enum used to discern between left and right rotations.
     */
    public enum Rotation {
        RIGHT, LEFT
    }

    /**
     * Attempts to rotate the current Tetromino by the given direction r (left or right).
     * If the Tetromino cannot rotate, it will remain in its current orientation.
     * @param r
     */
    public void rotate(Rotation r) {
        Tetromino t = tetris.getCurrentTetromino();
        int h = t.shape.length;
        int w = t.shape[0].length;
        boolean[][] newShape = new boolean[h][w];
        if (r == Rotation.LEFT) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++){
                    newShape[i][j] = t.shape[j][h - i - 1];
                }
            }
        } else if (r == Rotation.RIGHT) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    newShape[i][j] = t.shape[w - j - 1][i];
                }
            }
        }

        if (canRotate(newShape)) {
            Tetromino.clear(t, tetris.getBoard(), t.pos.x, t.pos.y);
            t.shape = newShape;
            t.height = t.shape[0].length;
            t.width = t.shape.length;
            Tetromino.draw(t, tetris.getBoard(), t.pos.x, t.pos.y);
        }
    }
}
