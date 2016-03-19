package bot;

import java.util.ArrayList;

/**
 * @author Curcudel Ioan-Razvan
 */

public class Node
{

    public int score;
    public Field field;
    public ArrayList<Node> children;

    public Node(Field field)
    {
        this.field = field;
        this.children = new ArrayList<Node>();
    }

    public void setScore(int score)
    {
        this.score = score;
    }

}

