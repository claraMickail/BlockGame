package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] myBoard = board.flatten();
		int count = 0;


		//search first row
		for (Color c : myBoard[0]){
			System.out.println();
			if (c == this.targetGoal){
				count++;
			}
		}

		//search last row
		for (Color b: myBoard[myBoard.length -1 ]){
			if (b == this.targetGoal){
				count++;
				}
			}

		//check left border
		for (int i = 0; i < myBoard.length; i++){
			if (myBoard[i][0] == this.targetGoal){
				count++;
			}
		}

		//Check right boarder
		for (int j = 0; j < myBoard.length; j++){
			if (myBoard[j][myBoard.length -1 ] == this.targetGoal){
				count++;
			}
		}
		return count;
	}
	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
