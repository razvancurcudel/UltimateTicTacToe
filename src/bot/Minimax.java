package bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

// TODO: Implement Alpha-beta pruning
public class Minimax
{

    public Minimax()
    {

    }

    //could be better with alpha beta.
    public Move minimax(Field field, int depth, boolean maximize)
    { // maximize = true --> your turn
        // maximize = false -->
        // opponent's turn
        ArrayList<Move> moves = field.getAvailableMoves();

        if (depth == 0 || moves.size() == 0)
        {
            Move m = new Move(-1, -1); // Coordinates don't matter
            m.score = field.computeScore();

            return m;
        }
        // Generate all moves possible
        // Make a copy of field, place first move and create a node
        // Apply minimax on that note with depth-1;

        // Create comparator based on maximize value
        Comparator<Move> comparator;
        if (maximize)
        {
            comparator = new MaximizeComparator();
        } else
        {
            comparator = new MinimizeComparator();
        }

        /**
         * Priority queue will store all possible moves with their score.
         *
         * Maximize == True
         *      PQ will return the highest score
         * Maximize == False
         *      PQ will return the smallest score
         */
        PriorityQueue<Move> queue = new PriorityQueue<>(1, comparator);

        for (int i = 0; i < moves.size(); i++)
        {
            // Create copy of field and place move
            Field copy = field.createCopy();
            copy.placeMove(moves.get(i), maximize);

            // Get score of this move
            Move minMaxResult = minimax(copy, depth - 1, !maximize);

            // Push this move to the queue
            moves.get(i).score = minMaxResult.score;
            queue.add(moves.get(i));
        }

        // Return best move for our case
        return queue.peek();

    }

    class MaximizeComparator implements Comparator<Move>
    {
        @Override
        public int compare(Move X, Move Y)
        {
            if (X.score == Y.score)
            {
                return 0;
            } else if (X.score > Y.score)
            {
                return 1;
            } else    // X.score < Y.score
            {
                return -1;
            }
        }
    }

    class MinimizeComparator implements Comparator<Move>
    {
        @Override
        public int compare(Move X, Move Y)
        {
            if (X.score == Y.score)
            {
                return 0;
            } else if (X.score > Y.score)
            {
                return -1;
            } else    // X.score < Y.score
            {
                return 1;
            }
        }
    }

}
