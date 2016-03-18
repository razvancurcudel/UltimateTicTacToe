package bot;

import java.util.ArrayList;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Minimax {

	public Minimax() {

	}

	
	//could be better with alpha beta.
	public int minimax(Field field, int depth, boolean maximize) { // maximize = true --> your turn
																	// maximize = false -->
																	// opponent's turn
		ArrayList<Move> moves = field.getAvailableMoves();
		
		if (depth == 0 || moves.size() == 0) {
			// return root.getEval(); // evaluate heuristic if is leaf
			return 0;
		}
		// Generate all moves possible
		// Make a copy of field, place first move and create a node
		// Apply minimax on that note with depth-1;

		if (maximize) {
			int bestScore = -Integer.MAX_VALUE;
			for (int i = 0; i < moves.size(); i++) {
				Field copy = field.createCopy();
				copy.placeMove(moves.get(i), maximize);
				int score = minimax(copy, depth - 1, !maximize);
				bestScore = Math.max(bestScore, score);
			}
			return bestScore;
		} else {
			int bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < moves.size(); i++) {
				Field copy = field.createCopy();
				copy.placeMove(moves.get(i), maximize);
				int score = minimax(copy, depth - 1, !maximize);
				bestScore = Math.min(bestScore, score);
			}
			return bestScore;
		}
	}

//	public int minimax(Node root, int depth, boolean maximize) { // maximize = true --> your turn
//		// maximize = false -->
//		// opponent's turn
//		if (depth == 0 || root.children.size() == 0) {
//			// return root.getEval(); // evaluate heuristic if is leaf
//			return 0;
//		}
//		// Generate all moves possible
//		// Make a copy of field, place first move and create a node
//		// Apply minimax on that note with depth-1;
//		ArrayList<Move> moves = root.field.getAvailableMoves();
//
//		if (maximize) {
//			int bestScore = -Integer.MAX_VALUE;
//			for (int i = 0; i < moves.size(); i++) {
//				Field copy = root.field.createCopy();
//				copy.placeMove(moves.get(i), maximize);
//				Node child = new Node(copy);
//				root.children.add(child);
//				child.score = minimax(child, depth - 1, !maximize);
//				bestScore = Math.max(bestScore, child.score);
//			}
//			return bestScore;
//		} else {
//			int bestScore = Integer.MAX_VALUE;
//			for (int i = 0; i < moves.size(); i++) {
//				Field copy = root.field.createCopy();
//				copy.placeMove(moves.get(i), maximize);
//				Node child = new Node(copy);
//				root.children.add(child);
//				child.score = minimax(child, depth - 1, !maximize);
//				bestScore = Math.min(bestScore, child.score);
//			}
//		}
//		return 0;
//	}

}
