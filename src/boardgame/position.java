package boardgame;

public class position {
	
	
	private int row;
	private int column;
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public position(int row, int column) {
		super();
		this.row = row;
		this.column = column;
	}
	
	
	@Override
	public String toString() {
		return row + ", " + column;
	}

}
