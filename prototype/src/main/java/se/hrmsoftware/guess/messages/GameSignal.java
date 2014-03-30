package se.hrmsoftware.guess.messages;

public class GameSignal {
	private int lowerBoundary;
	private int upperBoundary;


	public GameSignal(int lowerBoundary, int upperBoundary) {
		this.lowerBoundary = lowerBoundary;
		this.upperBoundary = upperBoundary;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("GameSignal{");
		sb.append("lowerBoundary=").append(lowerBoundary);
		sb.append(", upperBoundary=").append(upperBoundary);
		sb.append('}');
		return sb.toString();
	}

	public int getLowerBoundary() {
		return lowerBoundary;
	}

	public int getUpperBoundary() {
		return upperBoundary;
	}
}
