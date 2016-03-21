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

public class Field
{

    private final int COLS = 9, ROWS = 9;
    private int mRoundNr;
    private int mMoveNr;
    private int[][] mBoard;
    private int[][] mMacroboard;
    private String mLastError = "";

    public Field ()
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
    public void parseGameData (String key, String value)
    {
        if (key.equals("round"))
        {
            mRoundNr = Integer.parseInt(value);
        }
        else if (key.equals("move"))
        {
            mMoveNr = Integer.parseInt(value);
        }
        else if (key.equals("field"))
        {
            parseFromString(value); /* Parse Field with data */
        }
        else if (key.equals("macroboard"))
        {
            parseMacroboardFromString(value); /* Parse macroboard with data */
        }
    }

    /**
     * Initialise field from comma separated String
     *
     * @param s :
     */
    public void parseFromString (String s)
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
    public void parseMacroboardFromString (String s)
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

    public void clearBoard ()
    {
        for (int x = 0; x < COLS; x++)
        {
            for (int y = 0; y < ROWS; y++)
            {
                mBoard[x][y] = 0;
            }
        }
    }

    public ArrayList<Move> getAvailableMoves ()
    {
        ArrayList<Move> moves = new ArrayList<Move>();

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

    public Boolean isInActiveMicroboard (int x, int y)
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
     * @param column
     * @param row
     *
     * @return : int
     */
    public int getPlayerId (int column, int row)
    {
        return mBoard[column][row];
    }

    public Field createCopy ()
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

    public void placeMove (Move move, boolean maximize)
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
    public int computeScore ()
    {
        return -1;
    }

    public int[][] getBoardMatrix (int[][] mMacroboard, int row, int col)
    {

        int[][] aux = new int[COLS / 3][ROWS / 3];

        for (int i = col * 3, k = 0; i < ((col * 3) + 3); i++, k++)
        {
            for (int j = row * 3, m = 0; j < ((row * 3) + 3); j++, m++)
            {
                aux[k][m] = mBoard[i][j];
            }
        }

        return aux;
    }

    public ArrayList<ArrayList<Integer>> getEverything (int[][] matrix)
    {

        /*

        ArrayList<String> list = new ArrayList<String>()
            {{
                add("A");
                add("B");
                add("C");
            }};

            List<String> strings = new ArrayList<>(asList("foo", "bar", "baz"));
        * */

        ArrayList<ArrayList<Integer>> everything = new ArrayList<ArrayList<Integer>>();

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
}