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
    private int mRoundNr;
    private int mMoveNr;
    private int[][] mBoard;
    private int[][] mMacroboard;
    private String mLastError = "";
    private int myID;

    Field()
    {
        mBoard = new int[COLS][ROWS];
        mMacroboard = new int[COLS / 3][ROWS / 3];
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
                mBoard[x][y] = Integer.parseInt(r[counter]);
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
                mMacroboard[x][y] = Integer.parseInt(r[counter]);
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
        return mMacroboard[(int) x / 3][(int) y / 3] == -1;
    }

    /**
     * Returns reason why addMove returns false
     *
     * @return : reason why addMove returns false
     */
    public String getLastError ()
    {
        return mLastError;
    }

    @Override
    /**
     * Creates comma separated String with player ids for the microboards.
     *
     * @param args
     *            :
     * @return : String with player names for every cell, or 'empty' when cell is empty.
     */
    public String toString ()
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

    /**
     * Checks whether the field is full
     *
     * @return : Returns true when field is full, otherwise returns false.
     */
    public boolean isFull ()
    {
        for (int x = 0; x < COLS; x++)
            for (int y = 0; y < ROWS; y++)
                if (mBoard[x][y] == 0)
                    return false; // At least one cell is not filled
        // All cells are filled
        return true;
    }

    public int getNrColumns ()
    {
        return COLS;
    }

    public int getNrRows ()
    {
        return ROWS;
    }

    public boolean isEmpty ()
    {
        for (int x = 0; x < COLS; x++)
        {
            for (int y = 0; y < ROWS; y++)
            {
                if (mBoard[x][y] > 0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the player id on given column and row
     *
     * @return : int
     */
    public int getPlayerId (int column, int row)
    {
        return mBoard[column][row];
    }

    Field createCopy()
    {
        Field clone = new Field();
        clone.mRoundNr = this.mRoundNr;
        clone.mMoveNr = this.mMoveNr;
        for (int x = 0; x < COLS; x++)
        {
            for (int y = 0; y < ROWS; y++)
            {
                clone.mBoard[x][y] = this.mBoard[x][y];
            }
        }
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                clone.mMacroboard[x][y] = this.mMacroboard[x][y];
            }
        }
        return clone;
    }

    void placeMove(Move move, boolean maximize)
    {
        if (maximize)
        {
            mBoard[move.getX()][move.getY()] = 1;
        }
        else
        {
            mBoard[move.getX()][move.getY()] = 2;
        }
    }

    /**
     * Steps (so far): <p> 1) Check win/lose condition -> MAX/MIN_VALUE 2) Check macroBoard -> TBP
     * points (2.1) Ignore dead boards 3) Check each miniBoard -> TBP points (3.1) Ignore dead
     * squares
     *
     * @return The score for the current field - sum of all points
     */
    int computeScore()
    {
        // Get score from macro
        int score = getBoardScore(mMacroboard, 10);

        if (score > 5000) return Integer.MAX_VALUE; // WIN
        else if (score < -5000) return Integer.MIN_VALUE; // LOSS

        // Game not won/lost yet, get score from small boards
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
            {
                if (mMacroboard[row][col] == 0)
                {
                    score += getBoardScore(getBoard(row, col), 1);
                }
            }

        return score;
    }

    private ArrayList<ArrayList<Integer>> getLines(int[][] matrix)
    {
        ArrayList<ArrayList<Integer>> everything = new ArrayList<>();

        // Linii
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[0][1], matrix[0][2])));
        everything.add(new ArrayList<>(asList(matrix[1][0], matrix[1][1], matrix[1][2])));
        everything.add(new ArrayList<>(asList(matrix[2][0], matrix[2][1], matrix[2][2])));

        // Coloane
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[1][0], matrix[2][0])));
        everything.add(new ArrayList<>(asList(matrix[0][1], matrix[1][1], matrix[2][1])));
        everything.add(new ArrayList<>(asList(matrix[0][2], matrix[1][2], matrix[2][2])));

        // Diagonale
        everything.add(new ArrayList<>(asList(matrix[0][0], matrix[1][1], matrix[2][2])));
        everything.add(new ArrayList<>(asList(matrix[0][2], matrix[1][1], matrix[2][0])));

        return everything;
    }

    private int getBoardScore(int[][] board, int tenIfMacro)
    {
        int score = 0;
        int countMine, countTheir, countEmpty;

        ArrayList<ArrayList<Integer>> lines = getLines(board);

        for (ArrayList<Integer> line : lines)
        {
            countMine = countTheir = countEmpty = 0;

            // Count filled cells on line
            for (int i = 0; i < 3; i++)
            {
                if (line.get(i) == myID) countMine++;
                else if (line.get(i) == 3 - myID) countTheir++;
                else if (line.get(i) == 0) countEmpty++;
            }

            // TODO: Set scores for each case
            if (countEmpty == 0) // Line completely filled in
            {
                switch (countMine)
                {
                    case 3:
                        score += 100 * tenIfMacro; // WIN
                    case 2:
                        score += 1;
                        break;
                    case 1:
                        score += -1;
                        break;
                    default:
                        return -100 * tenIfMacro; // LOSS
                }
            } else if (countEmpty == 1)
            {
                switch (countMine)
                {
                    case 2:
                        score += 10;
                        break;
                    case 1:
                        score += 0;
                        break;
                    case 0:
                        score += -10;
                        break;
                }
            } else
            {
                if (countMine == 1) score += 1;
                else if (countTheir == 1) score += -1;
            }

        }

        return score;
    }

    private int[][] getBoard(int row, int col)
    {

        int[][] aux = new int[COLS / 3][ROWS / 3];

        for (int i = col * 3, k = 0; i < col * 3 + 3; i++, k++)
        {
            for (int j = row * 3, m = 0; j < row * 3 + 3; j++, m++)
            {
                aux[k][m] = mBoard[i][j];
            }
        }

        return aux;
    }

    void setMyID(int myID)
    {
        this.myID = myID;
    }
}