package dino;

import java.util.ArrayList;
import java.util.List;

class Node {
    private Node parent;
    private int[] position;
    private int g;
    private int h;
    private int f;

    public Node(Node parent, int[] position) {
        this.parent = parent;
        this.position = position;
        this.g = 0;
        this.h = 0;
        this.f = 0;
    }

    public Node getParent() {
        return parent;
    }

    public int[] getPosition() {
        return position;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public boolean equals(Node other) {
        return this.position[0] == other.position[0] && this.position[1] == other.position[1];
    }
}

public class AStar {
    public static List<int[]> astar(int[][] maze, int[] start, int[] end, int collisionLevel) {
        Node startNode = new Node(null, start);
        startNode.setG(0);
        startNode.setH(0);
        startNode.setF(0);
        Node endNode = new Node(null, end);
        endNode.setG(0);
        endNode.setH(0);
        endNode.setF(0);

        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.get(0);
            int currentIndex = 0;
            for (int i = 0; i < openList.size(); i++) {
                Node item = openList.get(i);
                if (item.getF() < currentNode.getF()) {
                    currentNode = item;
                    currentIndex = i;
                }
            }

            openList.remove(currentIndex);
            closedList.add(currentNode);

            if (currentNode.equals(endNode)) {
                List<int[]> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    path.add(current.getPosition());
                    current = current.getParent();
                }
            //    System.out.println(closedList.size());
                return path;
            }

            List<Node> children = new ArrayList<>();
            int[][] newPositions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 }, { -1, -1 }, { -1, 1 }, { 1, -1 },
                    { 1, 1 } };

            for (int[] newPosition : newPositions) {
                int[] nodePosition = { currentNode.getPosition()[0] + newPosition[0],
                        currentNode.getPosition()[1] + newPosition[1] };

                if (nodePosition[0] > maze.length - 1 || nodePosition[0] < 0
                        || nodePosition[1] > maze[maze.length - 1].length - 1 || nodePosition[1] < 0)
                    continue;

                if (maze[nodePosition[0]][nodePosition[1]] > collisionLevel)
                    continue;

                Node newNode = new Node(currentNode, nodePosition);
                children.add(newNode);
            }

            for (Node child : children) {
                boolean skipChild = false;
                for (Node closedChild : closedList) {
                    if (child.equals(closedChild)) {
                        skipChild = true;
                        break;
                    }
                }

                if (skipChild)
                    continue;

                child.setG(currentNode.getG() + 1);
                child.setH((int) (Math.pow(child.getPosition()[0] - endNode.getPosition()[0], 2)
                        + Math.pow(child.getPosition()[1] - endNode.getPosition()[1], 2)));
                child.setF(child.getG() + child.getH());

                boolean skipAddition = false;
                for (Node openNode : openList) {
                    if (child.equals(openNode) && child.getG() >= openNode.getG()) {
                        skipAddition = true;
                        break;
                    }
                }

                if (!skipAddition)
                    openList.add(child);
            }
        }
        return null;
    }
}
