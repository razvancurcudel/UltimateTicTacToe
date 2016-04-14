package bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

// TODO: Implement Alpha-beta pruning   - Etapa 3
// TODO: Fix heuristics                 - Etapa 2.1
// TODO: Add static board               - Etapa 2.2
class Minimax
{

    Minimax()
    {

    }

    Move minimax(Field field, int depth, boolean maximize)
    {
        // maximize = true --> your turn
        // maximize = false -->
        // opponent's turn
        ArrayList<Move> moves = field.getAvailableMoves();

        // Check if we've reached the final level - compute score for board
        if (depth == 0 || moves.size() == 0)
        {
            Move m = new Move(-1, -1); // Coordinates don't matter
            m.score = field.computeScoreAnca();

            return m;
        }

        // Generate all possible moves
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

        for (Move move : moves)
        {
            // Create copy of field and place move
            Field copy = field.createCopy();
            copy.placeMove(move, maximize);

            // Get score of this move
            Move minMaxResult = minimax(copy, depth - 1, !maximize);

            // Push this move to the queue
            move.score = minMaxResult.score;
            queue.add(move);
        }

        // Get all best moves for our case
        Move m = queue.poll();
        ArrayList<Move> bestMoves = new ArrayList<>();

        do
        {
            bestMoves.add(m);
            m = queue.poll();
        } while (!queue.isEmpty() && m.score == bestMoves.get(0).score);

        Random r = new Random();

        // Return random best move for our case
        return bestMoves.get(r.nextInt(bestMoves.size()));
    }

    private class MinimizeComparator implements Comparator<Move>
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

    private class MaximizeComparator implements Comparator<Move>
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
