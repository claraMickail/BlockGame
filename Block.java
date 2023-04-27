package myPackage3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

    private int xCoord;
    private int yCoord;
    private int size; // height/width of the square
    private int level; // the root (outer most block) is at level 0
    private int maxDepth;
    private Color color;
    private Block[] children; // {UpperRight, UpperLeft, LowerLeft, LowerRight}

    public static Random gen = new Random();


    public Block() {}

    public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
        this.xCoord=x;
        this.yCoord=y;
        this.size=size;
        this.level=lvl;
        this.maxDepth = maxD;
        this.color=c;
        this.children = subBlocks;
    }


    /*
     * Creates a random block given its level and a max depth.
     *
     * xCoord, yCoord, size, and highlighted should not be initialized
     */
    public Block(int lvl, int maxDepth) {

        this.level = lvl;
        this.maxDepth = maxDepth;

        if (maxDepth <= 0 || lvl > maxDepth || lvl < 0){
            throw new IllegalArgumentException("Invalid Block");
        }

        if (level < maxDepth && gen.nextDouble() < Math.exp(-0.25 * lvl)){
            this.color = null;
            this.children = new Block[4];
            for (int i = 0; i < 4; i++){
                children[i] = new Block(lvl+1, maxDepth);
            }

        }else{
            this.children = new Block[0];
            this.color = GameColors.BLOCK_COLORS[gen.nextInt(GameColors.BLOCK_COLORS.length)];
        }
    }


    /*
     * Updates size and position for the block and all of its sub-blocks, while
     * ensuring consistency between the attributes and the relationship of the
     * blocks.
     *
     *  The size is the height and width of the block. (xCoord, yCoord) are the
     *  coordinates of the top left corner of the block.
     */

    public void updateSizeAndPosition (int size, int xCoord, int yCoord) {

        if (xCoord < 0 || yCoord < 0){
            throw new IllegalArgumentException("coordinates cant be neg");
        }

        if (size <= 0 || this.level > maxDepth) {
            throw new IllegalArgumentException("size not valid");
        }

        if (size % 2 != 0 && this.level != this.maxDepth){
            throw new IllegalArgumentException("invalid size");
        }

        if (!sizeCheck(size, level)){
            throw new IllegalArgumentException("size is invalid");
        }

        this.size = size;
        this.xCoord = xCoord;
        this.yCoord = yCoord;

        if (children.length == 4){
            int childrenSize = size/2;
            children[0].updateSizeAndPosition(childrenSize, xCoord + childrenSize, yCoord);
            children[1].updateSizeAndPosition(childrenSize,xCoord, yCoord);
            children[2].updateSizeAndPosition(childrenSize, xCoord,yCoord + childrenSize );
            children[3].updateSizeAndPosition(childrenSize, xCoord+childrenSize, yCoord + childrenSize);

        }
    }
    private boolean sizeCheck(int size, int lvl){
        if (maxDepth == lvl){
            return true;
        }else {
            int tempSize = size/2;
            boolean myCheck = sizeCheck(tempSize, lvl + 1);
            return (myCheck && size % 2 == 0);
        }
}


    /*
     * Returns a List of blocks to be drawn to get a graphical representation of this block.
     *
     * This includes, for each undivided Block:
     * - one BlockToDraw in the color of the block
     * - another one in the FRAME_COLOR and stroke thickness 3
     *
     * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
     *
     * The order in which the blocks to draw appear in the list does NOT matter.
     */
    public ArrayList<BlockToDraw> getBlocksToDraw() {

        ArrayList<BlockToDraw> drawArr = new ArrayList<BlockToDraw>();

        if (this.children.length == 0) {
            drawArr.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
            drawArr.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord,this.size, 3));
        }
        else {
            for (Block b : this.children)
                drawArr.addAll(b.getBlocksToDraw());
        }

        return drawArr;
    }

    public BlockToDraw getHighlightedFrame() {
        return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
    }



    /*
     * Return the Block within this Block that includes the given location
     * and is at the given level. If the level specified is lower than
     * the lowest block at the specified location, then return the block
     * at the location with the closest level value.
     *
     * The location is specified by its (x, y) coordinates. The lvl indicates
     * the level of the desired Block. Note that if a Block includes the location
     * (x, y), and that Block is subdivided, then one of its sub-Blocks will
     * contain the location (x, y) too. This is why we need lvl to identify
     * which Block should be returned.
     *
     * Input validation:
     * - this.level <= lvl <= maxDepth (if not throw exception)
     * - if (x,y) is not within this Block, return null.
     */


    public Block getSelectedBlock(int x, int y, int lvl) {

        if (lvl > maxDepth || x < 0 || y < 0 || lvl < 0){
            throw new IllegalArgumentException("Invalid");
        }
        else if ( x < xCoord + size && x >= xCoord && y < yCoord + size && y >= yCoord) {
            if (this.level == lvl || children.length == 0) {
                return this;
            } else {
                for (Block b : children) {
                    Block bSelected = b.getSelectedBlock(x, y, lvl);
                    if (bSelected != null) {
                        return bSelected;
                    }
                }
            }
        }
        return null;
    }



    /*
     * Swaps the child Blocks of this Block.
     * If input is 1, swap vertically. If 0, swap horizontally.
     * If this Block has no children, do nothing. The swap
     * should be propagate, effectively implementing a reflection
     * over the x-axis or over the y-axis.
     *
     */
    public void reflect(int direction) {
        //reflect on x-axis
        if (direction == 0) {
            if (this.children.length != 0) {
                for (int i = 0; i < this.children.length / 2; i++) {
                    Block temp = this.children[i];
                    this.children[i] = this.children[this.children.length - i - 1];
                    this.children[this.children.length - i - 1] = temp;
                    this.children[i].reflect(direction);
                    this.children[this.children.length - i - 1].reflect(direction);
                    this.updateSizeAndPosition(size, this.xCoord, this.yCoord);
                }
                for (int i=0; i < children.length; i++){
                    this.children[i].rotate(direction);
                }
            }
            //reflect on y-axis
        } else if (direction == 1) {
            if (this.children.length != 0) {
                Block tempA = this.children[0];
                Block tempB = this.children[3];
                this.children[0] = this.children[1];
                this.children[3] = this.children[2];
                this.children[1] = tempA;
                this.children[2] = tempB;
                this.updateSizeAndPosition(size, this.xCoord, this.yCoord);
            }

            for (int i=0; i < children.length; i++){
                this.children[i].reflect(direction);
            }

            }
        else {
            throw new IllegalArgumentException("Invalid direction");
        }
    }


    /*
     * Rotate this Block and all its descendants.
     * If the input is 1, rotate clockwise. If 0, rotate
     * counterclockwise. If this Block has no children, do nothing.
     */
    public void rotate(int direction) {
        /*
         * ADD YOUR CODE HERE
         */

        if (direction == 0){
            if (this.children.length != 0) {
                Block temp0 = this.children[0];
                Block temp1 = this.children[1];
                Block temp2 = this.children[2];
                Block temp3 = this.children[3];
                this.children[0] = temp3;
                this.children[1] = temp0;
                this.children[2] = temp1;
                this.children[3] = temp2;
                this.updateSizeAndPosition(size, this.xCoord, this.yCoord);
            }

            for (int i=0; i < children.length; i++){
                this.children[i].rotate(direction);
            }
        }else if (direction == 1){
            if (this.children.length != 0) {
                Block temp0 = this.children[0];
                Block temp1 = this.children[1];
                Block temp2 = this.children[2];
                Block temp3 = this.children[3];
                this.children[0] = temp1;
                this.children[1] = temp2;
                this.children[2] = temp3;
                this.children[3] = temp0;
                this.updateSizeAndPosition(size, this.xCoord, this.yCoord);
            }
            for (int i=0; i < children.length; i++){
                this.children[i].rotate(direction);
            }
        }else {
                throw new IllegalArgumentException("Invalid direction");
            }
        }



    /*
     * Smash this Block.
     *
     * If this Block can be smashed,
     * randomly generate four new children Blocks for it.
     * (If it already had children Blocks, discard them.)
     * Ensure that the invariants of the Blocks remain satisfied.
     *
     * A Block can be smashed if it is not the top-level Block
     * and it is not already at the level of the maximum depth.
     *
     * Return True if this Block was smashed and False otherwise.
     *
     */
    public boolean smash() {
        /*
         * ADD YOUR CODE HERE
         */

        if (this.level != maxDepth && this.level != 0){
            if (children.length == 0){
                this.color = null;
            }
            this.children = new Block[4];
            for (int i = 0; i < 4; i++){
                this.children[i] = new Block(level + 1, maxDepth);
            }
            updateSizeAndPosition(size, xCoord, yCoord);
            return true;
        }
        return false;
    }


    /*
     * Return a two-dimensional array representing this Block as rows and columns of unit cells.
     *
     * Return and array arr where, arr[i] represents the unit cells in row i,
     * arr[i][j] is the color of unit cell in row i and column j.
     *
     * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
     */
    public Color[][] flatten() {

        int numUnits = (int) (size / Math.pow(2, this.maxDepth - this.level));
        int arrSize = this.size / numUnits;
        Color[][] arr = new Color[arrSize][arrSize];
        getColors(arr, numUnits);
        return arr;
    }

    private void getColors(Color[][] tempArr, int units){
        updateSizeAndPosition(size, xCoord, yCoord);
        if (children.length == 0){
            int xStart = yCoord /units;
            int yStart = xCoord/units;
            for (int i = 0; i< size/units; i++){
                for (int j = 0; j < size/units; j++){
                    tempArr[j + xStart][ i + yStart] = this.color;
                }
            }
        }else{
            for ( Block b : children){
                b.getColors(tempArr,units);
            }
        }
    }
    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getLevel() {
        return this.level;
    }


    /*
     * The next 5 methods are needed to get a text representation of a block.
     * They are used for debugging.
     */
    public String toString() {
        return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
    }

    public void printBlock() {
        this.printBlockIndented(0);
    }

    private void printBlockIndented(int indentation) {
        String indent = "";
        for (int i=0; i<indentation; i++) {
            indent += "\t";
        }

        if (this.children.length == 0) {
            // it's a leaf. Print the color!
            String colorInfo = GameColors.colorToString(this.color) + ", ";
            System.out.println(indent + colorInfo + this);
        }
        else {
            System.out.println(indent + this);
            for (Block b : this.children)
                b.printBlockIndented(indentation + 1);
        }
    }

    private static void coloredPrint(String message, Color color) {
        System.out.print(GameColors.colorToANSIColor(color));
        System.out.print(message);
        System.out.print(GameColors.colorToANSIColor(Color.WHITE));
    }

    public void printColoredBlock(){
        Color[][] colorArray = this.flatten();
        for (Color[] colors : colorArray) {
            for (Color value : colors) {
                String colorName = GameColors.colorToString(value).toUpperCase();
                if(colorName.length() == 0){
                    colorName = "\u2588";
                }
                else{
                    colorName = colorName.substring(0, 1);
                }
                coloredPrint(colorName, value);
            }
            System.out.println();
        }
    }
}
