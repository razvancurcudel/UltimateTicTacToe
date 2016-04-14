// // Copyright 2016 theaigames.com (developers@theaigames.com)

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// For the full copyright and license information, please view the LICENSE
// file that was distributed with this source code.

package bot;

import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * Field class <p> Handles everything that has to do with the field, such as storing the current
 * state and performing calculations on the field.
 *
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

class Field
{

    private final int COLS = 9, ROWS = 9;
    int mRoundNr;
    private int mMoveNr;
    private int[][] mBoard;
    private int[][] mMacroboard;
    private int myID;

    // Etapa 2
    private int[][] moveTracker;

    Field()
    {
        mBoard = new int[COLS][ROWS];
        mMacroboard = new int[COLS / 3][ROWS / 3];
        moveTracker = new int[COLS / 3][ROWS / 3];
        clearBoard();
    }

    /**
     * Parse data about the game given by the engine
     *
     * @param key   : type of data given
     * @param value : value
     */
    void parseGameData(String key, String value)
    {
        switch (key)
        {
            case "round":
                mRoundNr = Integer.parseInt(value);
                break;
            case "move":
                mMoveNr = Integer.parseInt(value);
                break;
            case "field":
                parseFromString(value); /* Parse Field with data */
                break;
            case "macroboard":
                parseMacroboardFromString(value); /* Parse macroboard with data */
                break;
        }
    }

    /**
     * Initialise field from comma separated String
     *
     * @param s :
     */
    private void parseFromString(String s)
    {
        System.err.println("Move " + mMoveNr);
        s = s.replace(";", ",");
        String[] r = s.split(",");
        int counter = 0;
        for (int y = 0; y < ROWS; y++)
        {
            for (int x = 0; x < COLS; x++)
            {
                mBoard[y][x] = Integer.parseInt(r[counter]);
                counter++;
            }
        }
    }

    /**
     * Initialise macroboard from comma separated String
     *
     * @param s :
     */
    private void parseMacroboardFromString(String s)
    {
        String[] r = s.split(",");
        int counter = 0;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                mMacroboard[y][x] = Integer.parseInt(r[counter]);
                counter++;
            }
        }
    }

    private void clearBoard()
    {
        for (int x = 0; x < COLS; x++)
        {
            for (int y = 0; y < ROWS; y++)
            {
                mBoard[x][y] = 0;
            }
        }
    }

    ArrayList<Move> getAvailableMoves()
    {
        ArrayList<Move> moves = new ArrayList<>();

        for (int y = 0; y < ROWS; y++)
        {
            for (int x = 0; x < COLS; x++)
            {
                if (isInActiveMicroboard(x, y) && mBoard[x][y] == 0)
                {
                    moves.add(new Move(x, y));
                }
            }
        }

        return moves;
    }

    private Boolean isInActiveMicroboard(int x, int y)
    {
        return mMacroboard[x / 3][y / 3] == -1;
    }

    @Override
    /**
     * Creates comma separated String with player ids for the microboards.
     *
     * @param args
     *            :
     * @return : String with player names for every cell, or 'empty' when cell is empty.
     */
    public String toString()
    {
        String r = "";
        int counter = 0;
        for (int y = 0; y < ROWS; y++)
        {
            for (int x = 0; x < COLS; x++)
            {
                if (counter > 0)
                {
                    r += ",";
                }
                r += mBoard[x][y];
                counter++;
            }
        }
        return r;
    }

    // Create copy of current field
    Field createCopy()
    {
        Field clone = new Field();
        clone.mRoundNr = this.mRoundNr;
        clone.mMoveNr = this.mMoveNr;
        clone.myID = this.myID;

        for (int x = 0; x < COLS; x++)
        {
            System.arraycopy(this.mBoard[x], 0, clone.mBoard[x], 0, ROWS);
        }
        for (int x = 0; x < 3; x++)
        {
            System.arraycopy(this.mMacroboard[x], 0, clone.mMacroboard[x], 0, 3);
        }
        return clone;
    }

    /**
     * Places a move on the board
     *
     * @param move:     move to be placed
     * @param maximize: true - our bot; false - opponent
     */
    void placeMove(Move move, boolean maximize)
    {
        // Place player ID in cell
        if (maximize)
        {
            mBoard[move.getX()][move.getY()] = myID;
        } else
        {
            mBoard[move.getX()][move.getY()] = 3 - myID;
        }

        // Update macroBoard - win, loss, next possible moves
        updateBoard(move);

        // Update moveTracker
        moveTracker[move.getX() / 3][move.getX() / 3] = 1;
    }

    /**
     * Initialize moveTracker
     * <p>
     * 0 - no move placed in this macro cell
     * 1 - move placed in macro cell - score not yet computed
     * 2 - macro cell score computed
     */
    void clearMoveTracker()
    {
        for (int row = 0; row < ROWS / 3; row++)
            for (int col = 0; col < COLS / 3; col++)
                moveTracker[row][col] = 0;
    }

    private boolean isFull(int[][] board)
    {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (board[row][col] == 0) return false;
                
        return true;
    }

    // Update macro board based on previous move - param move
    private void updateBoard(Move move)
    {
        // Check win in board and update macro
        int result = checkWin(getBoard(move.getX() / 3, move.getY() / 3));
        if (result > 0)
            mMacroboard[move.getX() / 3][move.getY() / 3] = result;

        // Mark new active boards
        if (mMacroboard[move.getX() % 3][move.getY() % 3] > 0)
        {
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++)
                    if (mMacroboard[row][col] < 1 && !isFull(mMacroboard[row][col])) // Not won/lost - ties don't matter YET
                        mMacroboard[row][col] = -1;
        } else
        {
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++)
                    if (mMacroboard[row][col] < 1) // Not won/lost - ties don't matter YET
                        mMacroboard[row][col] = 0;

            mMacroboard[move.getX() % 3][move.getY() % 3] = -1;
        }
    }

    /**
     * Steps:
     * 1) Check win/lose condition
     * 2) Check macroBoard
     * 3) Check the miniBoard of the last move placed
     *
     * @return The score for the current field - sum of all points
     */
    int computeScore()
    {
//        // Update the value of each macro cell (if won/lost)
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++)
//            {
//                int result = checkWin(getBoard(i, j));
//                if (result > 0) mMacroboard[i][j] = result;
//            }

        // Get score from macro
        int score = getBoardScore(mMacroboard, 20);

        // Check win/loss on macro
        if (score > 15000) return Integer.MAX_VALUE; // WIN
        else if (score < -15000) return Integer.MIN_VALUE; // LOSS

        // Add score of the cells we placed a move in
        for (int row = 0; row < ROWS / 3; row++)
            for (int col = 0; col < COLS / 3; col++)
                if (moveTracker[row][col] == 1)
                    score += getBoardScore(getBoard(row, col), 1);

        // That's our score
        return score;
    }

    // Get each possible line from the board - 3 lines, 3 columns, 2 diagonals
    private ArrayList<ArrayList<Integer>> getLines(int[][] matrix)
    {
        ArrayList<ArrayList<Integer>> everything = new ArrayList<>();

        // Lines
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[0][1], matrix[0][2])));
        everything.add(new ArrayList<>(asList(matrix[1][0], matrix[1][1], matrix[1][2])));
        everything.add(new ArrayList<>(asList(matrix[2][0], matrix[2][1], matrix[2][2])));

        // Columns
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[1][0], matrix[2][0])));
        everything.add(new ArrayList<>(asList(matrix[0][1], matrix[1][1], matrix[2][1])));
        everything.add(new ArrayList<>(asList(matrix[0][2], matrix[1][2], matrix[2][2])));

        // Diagonals
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[1][1], matrix[2][2])));
        everything.add(new ArrayList<>(asList(matrix[0][2], matrix[1][1], matrix[2][0])));

        return everything;
    }

    /**
     * Compute score for a single 3x3 board. Return score if conditions for a case are met.
     * <p>
     * Cases:
     * 1) Check win/loss - +- 1000 points
     * 2) Check if I blocked any of opponent's moves - 250 points / blocked line (ex: X - X - O)
     * 3) Check if I'm close to win - 200 points / line (ex: O - O - empty)
     * 4) Empty board - check position of each O / X placed
     * (4.1) Middle cell - 50 points
     * (4.2) Corner cell - 30 points
     * (4.3) Side cell - 20 points
     *
     * @param board:  3x3 board (can be macro or small)
     * @param weight: weight of the board - 20 for macro, 1 for small board
     * @return board's score
     */
    private int getBoardScore(int[][] board, int weight)
    {
        int score = 0;

        // Check win
        int win_loss = checkWin(board);
        if (win_loss == myID) return 125 * weight;
        else if (win_loss == 3 - myID) return -125 * weight;

        // Check close to win
        int countMine, countEmpty, countTheir;
        int blocked = 0;
        int closeToWin = 0;

        ArrayList<ArrayList<Integer>> lines = getLines(board);

        for (ArrayList<Integer> line : lines)
        {

            countMine = countEmpty = countTheir = 0;

            for (int i = 0; i < 3; i++)
            {
                if (line.get(i) < 1) countEmpty++;
                else if (line.get(i) == myID) countMine++;
                else if (line.get(i) == 3 - myID) countTheir++;
            }

            if (countEmpty == 1 && countMine == 2) closeToWin++;
            if (countTheir == 2 && countMine == 1) blocked++;
        }

        // TODO: Fix these - maybe check which cell gives best chances
        // Example closeToWin with centre cell > closeToWin w/ corner > closeToWin w/ side
        // Same for blocked
        if (blocked > 0) return 95 * weight + blocked;
        if (closeToWin > 0) return 75 * weight + closeToWin;
        // Check each individual cell from the board

        // Corners
        if (board[0][0] == myID) score += 30 * weight;
        else if (board[0][0] == 3 - myID) score += -30 * weight;

        if (board[0][2] == myID) score += 30 * weight;
        else if (board[0][2] == 3 - myID) score += -30 * weight;

        if (board[2][0] == myID) score += 30 * weight;
        else if (board[2][0] == 3 - myID) score += -30 * weight;

        if (board[2][2] == myID) score += 30 * weight;
        else if (board[2][2] == 3 - myID) score += -30 * weight;

        // Sides
        if (board[0][1] == myID) score += 20 * weight;
        else if (board[0][1] == 3 - myID) score += -20 * weight;

        if (board[1][0] == myID) score += 20 * weight;
        else if (board[1][0] == 3 - myID) score += -20 * weight;

        if (board[1][2] == myID) score += 20 * weight;
        else if (board[1][2] == 3 - myID) score += -20 * weight;

        if (board[2][1] == myID) score += 20 * weight;
        else if (board[2][1] == 3 - myID) score += -20 * weight;

        // Centre
        if (board[1][1] == myID) score += 50 * weight;
        else if (board[1][1] == 3 - myID) score += -50 * weight;

        return score;
    }

    // Check if either player has won the current 3x3 board - param board
    private int checkWin(int[][] board)
    {
        int countMine, countTheir;

        ArrayList<ArrayList<Integer>> lines = getLines(board);

        for (ArrayList<Integer> line : lines)
        {
            countMine = countTheir = 0;
            for (int i = 0; i < 3; i++)
                if (line.get(i) == myID) countMine++;
                else if (line.get(i) == 3 - myID) countTheir++;

            if (countMine == 3) return myID;
            else if (countTheir == 3) return 3 - myID;
        }

        return -1;
    }

    // Gets the 3x3 board corresponding to mMacroboard[row][col]
    private int[][] getBoard(int row, int col)
    {
        // Small 3x3 board
        int[][] aux = new int[COLS / 3][ROWS / 3];

        // Copy elements to the 3x3 board
        for (int i = row * 3, k = 0; i < row * 3 + 3; i++, k++)
        {
            for (int j = col * 3, m = 0; j < col * 3 + 3; j++, m++)
            {
                aux[k][m] = mBoard[i][j];
            }
        }

        return aux;
    }

    // Set player ID
    void setMyID(int myID)
    {
        this.myID = myID;
    }


    // EURISTICA DE TEST - ANCA
    int computeScoreAnca()
    {
        // Check win on macro
        int winID = checkWin(mMacroboard);
        if (winID == myID) return 1000000;
        else if (winID == 3 - myID) return -1000000;

        // Get score from macro
        int score = getBoardScoreAnca(mMacroboard, 23);

        // Add score of the cells we placed a move in
        for (int row = 0; row < ROWS / 3; row++)
            for (int col = 0; col < COLS / 3; col++)
                if (moveTracker[row][col] == 1)
                    score += getBoardScoreAnca(getBoard(row, col), 1);

        // That's our score
        return score;
    }

    private int getBoardScoreAnca(int[][] board, int weight)
    {
        int countMine, countTheir;
        int myScore, theirScore;

        ArrayList<ArrayList<Integer>> lines = getLines(board);
        myScore = theirScore = 0;

        for (ArrayList<Integer> line : lines)
        {
            countMine = countTheir = 0;

            for (int i = 0; i < 3; i++)
            {
                if (line.get(i) == myID) countMine++;
                else if (line.get(i) == 3 - myID) countTheir++;
            }

            if (countMine > 0 && countTheir > 0) continue;
            else if (countMine == 1) myScore += 1;
            else if (countMine == 2) myScore += 7;
            else if (countTheir == 1) theirScore += 1;
            else if (countTheir == 2) theirScore += 7;
        }

        return myScore - theirScore;
    }

    public int computeScoreTina()
    {
        // Check win on macro
        int winID = checkWin(mMacroboard);
        if (winID == myID) return 1000000;
        else if (winID == 3 - myID) return -1000000;

        int score = getBoardScoreTina(mMacroboard);

        // Weight = 2
        if (mMacroboard[0][1] == myID) score += 1000 * 2;
        else if (mMacroboard[0][1] == 3 - myID) score -= 1000 * 2;
        else if (moveTracker[0][1] == 1) score += getBoardScoreTina(getBoard(0, 1)) * 2;

        if (mMacroboard[1][0] == myID) score += 1000 * 2;
        else if (mMacroboard[1][0] == 3 - myID) score -= 1000 * 2;
        else if (moveTracker[1][0] == 1) score += getBoardScoreTina(getBoard(1, 0)) * 2;

        if (mMacroboard[1][2] == myID) score += 1000 * 2;
        else if (mMacroboard[1][2] == 3 - myID) score -= 1000 * 2;
        else if (moveTracker[1][2] == 1) score += getBoardScoreTina(getBoard(1, 2)) * 2;

        if (mMacroboard[2][1] == myID) score += 1000 * 2;
        else if (mMacroboard[2][1] == 3 - myID) score -= 1000 * 2;
        else if (moveTracker[2][1] == 1) score += getBoardScoreTina(getBoard(2, 1)) * 2;

        // Weight = 3
        if (mMacroboard[0][0] == myID) score += 1000 * 3;
        else if (mMacroboard[0][0] == 3 - myID) score -= 1000 * 3;
        else if (moveTracker[0][0] == 1) score += getBoardScoreTina(getBoard(0, 0)) * 3;

        if (mMacroboard[0][2] == myID) score += 1000 * 3;
        else if (mMacroboard[0][2] == 3 - myID) score -= 1000 * 3;
        else if (moveTracker[0][2] == 1) score += getBoardScoreTina(getBoard(0, 2)) * 3;

        if (mMacroboard[2][0] == myID) score += 1000 * 3;
        else if (mMacroboard[2][0] == 3 - myID) score -= 1000 * 3;
        else if (moveTracker[2][0] == 1) score += getBoardScoreTina(getBoard(2, 0)) * 3;

        if (mMacroboard[2][2] == myID) score += 1000 * 3;
        else if (mMacroboard[2][2] == 3 - myID) score -= 1000 * 3;
        else if (moveTracker[2][2] == 1) score += getBoardScoreTina(getBoard(2, 2)) * 3;

        // Weight = 4
        if (mMacroboard[1][1] == myID) score += 1000 * 4;
        else if (mMacroboard[1][1] == 3 - myID) score -= 1000 * 4;
        else if (moveTracker[1][1] == 1) score += getBoardScoreTina(getBoard(1, 1)) * 4;

        // That's our score
        return score;
    }

    private int getBoardScoreTina(int[][] board)
    {
        int countMine, countTheir;
        int score;

        ArrayList<ArrayList<Integer>> lines = getLines(board);
        score = 0;

        for (int i = 0; i < 3; i++)
        {
            ArrayList<Integer> line = lines.get(i);

            countMine = countTheir = 0;

            for (int j = 0; j < 3; j++)
            {
                if (line.get(j) == myID) countMine++;
                else if (line.get(j) == 3 - myID) countTheir++;
            }

            if (countMine == 0 && countTheir == 0) score += 1;
            else if (countMine > 0 && countTheir > 0) continue;
            else if (countMine == 1) score += 3;
            else if (countMine == 2) score += 5;
            else if (countTheir == 1) score += 4;
            else if (countTheir == 2) score += 2;
        }

        return score;
    }
}
