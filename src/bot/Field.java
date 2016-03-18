// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

import java.util.ArrayList;

/**
 * Field class
 * 
 * Handles everything that has to do with the field, such as storing the current
 * state and performing calculations on the field.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Field {
	private int mRoundNr;
	private int mMoveNr;
	private int[][] mBoard;
	private int[][] mMacroboard;

	private final int COLS = 9, ROWS = 9;
	private String mLastError = "";

	public Field() {
		mBoard = new int[COLS][ROWS];
		mMacroboard = new int[COLS / 3][ROWS / 3];
		clearBoard();
	}

	public int[][] getBoardMatrix(int[][] mMacroboard, int row, int col) {
		
		int[][] aux = new int[COLS / 3][ROWS / 3];

		for (int i = col * 3, k = 0; i < ((col * 3) + 3); i++, k++) {
			for (int j = row * 3, m = 0; j < ((row * 3) + 3); j++, m++) {
				aux[k][m] = mBoard[i][j];
			}
		}
		
		return aux;
	}

	public ArrayList<ArrayList<Integer>> getEverything(int[][] matrix) {

		ArrayList<Integer> thing;
		ArrayList<ArrayList<Integer>> everything = new ArrayList<ArrayList<Integer>>();
		
		//linii
		for (int i = 0; i < COLS / 3; i++) {
			thing = new ArrayList<Integer>();
			for (int j = 0; j < ROWS / 3; j++) {
				thing.add(matrix[i][j]);
			}
			everything.add(thing);
		}
		
		//linii
		for (int j = 0; j < COLS / 3; j++) {
			thing = new ArrayList<Integer>();
			for (int i = 0; i < ROWS / 3; i++) {
				thing.add(matrix[i][j]);
			}
			everything.add(thing);
		}
		
		//diagonale
		thing = new ArrayList<Integer>();
		for(int i = 0; i < COLS / 3; i++) {
			thing.add(matrix[i][i]);
		}
			everything.add(thing);
			thing = new ArrayList<Integer>();
			thing.add(matrix[2][0]);
			thing.add(matrix[1][1]);
			thing.add(matrix[0][2]);
			everything.add(thing);
			
		return everything;
	}

	

	public static void main(String[] args) {
		Field a = new Field();
		a.clearBoard();
		a.mBoard[6][1] = 9;
		for (int i = 0; i < a.COLS; i++) {
			for (int j = 0; j < a.ROWS; j++) {
				System.out.print(a.mBoard[i][j] + " ");
			}
			System.out.println();
		}
		int[][] m = a.getBoardMatrix(a.mMacroboard, 0, 2);
		/*int[][] mat = new int[3][3];
		mat[0][0] = 1;
		mat[2][2] = 2;
		mat[1][1] = 3;*/
		System.out.println("--------");
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				System.out.print(m[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println(a.getEverything(m));
	}

	/**
	 * Parse data about the game given by the engine
	 * 
	 * @param key
	 *            : type of data given
	 * @param value
	 *            : value
	 */
	public void parseGameData(String key, String value) {
		if (key.equals("round")) {
			mRoundNr = Integer.parseInt(value);
		} else if (key.equals("move")) {
			mMoveNr = Integer.parseInt(value);
		} else if (key.equals("field")) {
			parseFromString(value); /* Parse Field with data */
		} else if (key.equals("macroboard")) {
			parseMacroboardFromString(value); /* Parse macroboard with data */
		}
	}

	/**
	 * Initialise field from comma separated String
	 * 
	 * @param String
	 *            :
	 */
	public void parseFromString(String s) {
		System.err.println("Move " + mMoveNr);
		s = s.replace(";", ",");
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				mBoard[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}

	/**
	 * Initialise macroboard from comma separated String
	 * 
	 * @param String
	 *            :
	 */
	public void parseMacroboardFromString(String s) {
		String[] r = s.split(",");
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				mMacroboard[x][y] = Integer.parseInt(r[counter]);
				counter++;
			}
		}
	}

	public void clearBoard() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				mBoard[x][y] = 0;
			}
		}
	}

	public ArrayList<Move> getAvailableMoves() {
		ArrayList<Move> moves = new ArrayList<Move>();

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (isInActiveMicroboard(x, y) && mBoard[x][y] == 0) {
					moves.add(new Move(x, y));
				}
			}
		}

		return moves;
	}

	public Boolean isInActiveMicroboard(int x, int y) {
		return mMacroboard[(int) x / 3][(int) y / 3] == -1;
	}

	/**
	 * Returns reason why addMove returns false
	 * 
	 * @param args
	 *            :
	 * @return : reason why addMove returns false
	 */
	public String getLastError() {
		return mLastError;
	}

	@Override
	/**
	 * Creates comma separated String with player ids for the microboards.
	 * 
	 * @param args
	 *            :
	 * @return : String with player names for every cell, or 'empty' when cell
	 *         is empty.
	 */
	public String toString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				if (counter > 0) {
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
	 * @param args
	 *            :
	 * @return : Returns true when field is full, otherwise returns false.
	 */
	public boolean isFull() {
		for (int x = 0; x < COLS; x++)
			for (int y = 0; y < ROWS; y++)
				if (mBoard[x][y] == 0)
					return false; // At least one cell is not filled
		// All cells are filled
		return true;
	}

	public int getNrColumns() {
		return COLS;
	}

	public int getNrRows() {
		return ROWS;
	}

	public boolean isEmpty() {
		for (int x = 0; x < COLS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (mBoard[x][y] > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the player id on given column and row
	 * 
	 * @param args
	 *            : int column, int row
	 * @return : int
	 */
	public int getPlayerId(int column, int row) {
		return mBoard[column][row];
	}
}