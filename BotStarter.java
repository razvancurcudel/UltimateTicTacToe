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
import java.util.Random;

/**
 * BotStarter class
 *
 * Magic happens here. You should edit this file, or more specifically the makeTurn() method to make
 * your bot do more than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

/*
*
*           0
*
*      1  4   7
 *
 *  1 2 3  0  5
*
* */

public class BotStarter
{

    public int minimax (Field root, int depth, boolean maximize)
    {
        if (depth == 0)
        {
            return computeScore(miniField);
        }

        ArrayList<Move> subtrees = new ArrayList<>();

        //int bestValue = Integer.MIN_VALUE;
        int availableMacroBoards = determineMacro(Field); // count number of -1 in macro

        for (int i = 0; i < availableMacroBoards; i++)
        {
            int availableMoves = determineMini(Field, i);
            for (int j = 0; j < availableMoves; j++)
                subtrees.add(minimax(Field.tryMove(j), depth - 1, !maximize));
        }

        if (maximize) return max(subtrees);
        else return min(subtrees);
    }


    /**
     * Makes a turn. Edit this method to make your bot smarter. Currently does only random moves.
     *
     * @return The column where the turn was made.
     */
    public Move makeTurn (Field field)
    {
        Random r = new Random();
        ArrayList<Move> moves = field.getAvailableMoves();
        Move move = moves.get(r.nextInt(moves.size())); /* get random move from available moves */

        return move;
    }

    public int someMax (int x)
    {
        int[] bestScore = new int[availableChoices];
        for (int i = 0; i < availableChoices; i++)
            bestScore[i] = someMax(x - 1);

        return max(bestScore);
    }


    public static void main (String[] args)
    {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }
}