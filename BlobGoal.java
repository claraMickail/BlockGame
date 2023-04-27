package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}
	@Override
	public int score(Block board) {
		Color[][] myBoard = board.flatten();
		boolean[][] myVisited = new boolean[myBoard.length][myBoard.length];
		int temp = 0;
		int score = 0;

		for (int i = 0; i < myBoard.length; i++){
			for (int j = 0 ; j < myBoard.length; j++){
				if (myBoard[i][j] == this.targetGoal && !myVisited[i][j]){
					int size = undiscoveredBlobSize(i,j,myBoard,myVisited);
					if (score < size){
						score = size;
						temp = score;
					}
				}
			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if ( i >= 0 && j >= 0 && i < unitCells.length && j < unitCells.length && unitCells[i][j] == this.targetGoal && !visited[i][j]){
			visited[i][j] = true;
			int size = 1;
			size += undiscoveredBlobSize( i+ 1, j, unitCells, visited);
			size += undiscoveredBlobSize(i - 1, j, unitCells, visited);
			size += undiscoveredBlobSize(i, j + 1, unitCells, visited);
			size += undiscoveredBlobSize(i, j - 1, unitCells, visited);

			return size;
		}

		return 0;

	}

}
