package se.hrmsoftware.guess.model;

import java.io.Serializable;

public class Range implements Serializable {
	private int lowerBound;
	private int upperBound;

	public Range(){}

	public Range(int lowerBound, int upperBound) {
		if (lowerBound >= upperBound) {
			throw new IllegalArgumentException("Lower must be, eh, lower than upper. Lower: "+lowerBound+", Upper: "+upperBound);
		}
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	public int getSize() { return upperBound - lowerBound; }

	/**
	 * Check if the value is included in this range (where upper and lower is part of the range).
	 * @param value .
	 * @return .
	 */
	public boolean isIncluded(int value) {
		return value >= lowerBound && value <= upperBound;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Range{");
		sb.append("lowerBound=").append(lowerBound);
		sb.append(", upperBound=").append(upperBound);
		sb.append('}');
		return sb.toString();
	}
}
