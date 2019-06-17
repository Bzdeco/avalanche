package las2etin.model;

import lombok.Getter;

public class Adjacency {
	//note: there's probably a simpler way to do this, but it works just fine for now

	// M-34-100-B- part of the map
	private static String[][] lasMapB = new String[][] {
			{"","","","a-1-2-2","a-2-1-1","a-2-1-2","a-2-2-1","a-2-2-2","b-1-1-1","b-1-1-2","b-1-2-1","b-1-2-2","b-2-1-1","b-2-1-2","b-2-2-1","b-2-2-2"},
			{"","","","a-1-2-4","a-2-1-3","a-2-1-4","a-2-2-3","a-2-2-4","b-1-1-3","b-1-1-4","b-1-2-3","b-1-2-4","b-2-1-3","b-2-1-4","b-2-2-3","b-2-2-4"},
			{"","","a-1-4-1","a-1-4-2","a-2-3-1","a-2-3-2","a-2-4-1","a-2-4-2","b-1-3-1","b-1-3-2","b-1-4-1","b-1-4-2","b-2-3-1","b-2-3-2","b-2-4-1","b-2-4-2"},
			{"","","a-1-4-3","a-1-4-4","a-2-3-3","a-2-3-4","a-2-4-3","a-2-4-4","b-1-3-3","b-1-3-4","b-1-4-3","b-1-4-4","b-2-3-3","b-2-3-4","b-2-4-3","b-2-4-4"},
			{"","","","a-3-2-2","a-4-1-1","a-4-1-2","a-4-2-1","a-4-2-2","b-3-1-1","b-3-1-2","b-3-2-1","b-3-2-2","b-4-1-1","b-4-1-2","b-4-2-1","b-4-2-2"},
			{"","","","a-3-2-4","a-4-1-3","a-4-1-4","a-4-2-3","a-4-2-4","b-3-1-3","b-3-1-4","b-3-2-3","b-3-2-4","b-4-1-3","b-4-1-4","b-4-2-3","b-4-2-4"},
			{"","","a-3-4-1","a-3-4-2","a-4-3-1","a-4-3-2","a-4-4-1","a-4-4-2","b-3-3-1","b-3-3-2","b-3-4-1","b-3-4-2","b-4-3-1","b-4-3-2","b-4-4-1","b-4-4-2"},
			{"","","a-3-4-3","a-3-4-4","a-4-3-3","a-4-3-4","a-4-4-3","a-4-4-4","b-3-3-3","b-3-3-4","b-3-4-3","b-3-4-4","b-4-3-3","b-4-3-4","b-4-4-3","b-4-4-4"},
			{"","c-1-1-2","c-1-2-1","c-1-2-2","c-2-1-1","c-2-1-2","c-2-2-1","c-2-2-2","d-1-1-1","d-1-1-2","d-1-2-1","d-1-2-2","d-2-1-1","d-2-1-2","d-2-2-1","d-2-2-2"},
			{"","c-1-1-4","c-1-2-3","c-1-2-4","c-2-1-3","c-2-1-4","c-2-2-3","c-2-2-4","d-1-1-3","d-1-1-4","d-1-2-3","d-1-2-4","d-2-1-3","d-2-1-4","d-2-2-3","d-2-2-4"},
			{"c-1-3-1","c-1-3-2","c-1-4-1","c-1-4-2","c-2-3-1","c-2-3-2","c-2-4-1","c-2-4-2","d-1-3-1","d-1-3-2","","","","","","d-2-4-2"},
			{"c-1-3-3","c-1-3-4","c-1-4-3","c-1-4-4","c-2-3-3","c-2-3-4","c-2-4-3","c-2-4-4","d-1-3-3","d-1-3-4","","","","","",""},
			{"c-3-1-1","c-3-1-2","c-3-2-1","c-3-2-2","c-4-1-1","c-4-1-2","c-4-2-1","c-4-2-2","d-3-1-1","","","","","","",""},
			{"","","","","","c-4-1-4","c-4-2-3","c-4-2-4","","","","","","","",""},
			{"","","","","","","","","","","","","","","",""},
			{"","","","","","","","","","","","","","","",""}
	};

	// M-34-101-A- part of the map
	private static String[][] lasMapA = new String[][] {
			{"a-1-1-1","a-1-1-2","a-1-2-1","a-1-2-2","a-2-1-1","a-2-1-2","a-2-2-1","a-2-2-2","b-1-1-1","b-1-1-2","b-1-2-1","b-1-2-2","b-2-1-1","","",""},
			{"a-1-1-3","a-1-1-4","a-1-2-3","a-1-2-4","a-2-1-3","a-2-1-4","a-2-2-3","a-2-2-4","b-1-1-3","b-1-1-4","b-1-2-3","b-1-2-4","","","",""},
			{"a-1-3-1","a-1-3-2","a-1-4-1","a-1-4-2","a-2-3-1","a-2-3-2","a-2-4-1","a-2-4-2","b-1-3-1","b-1-3-2","b-1-4-1","b-1-4-2","","","",""},
			{"a-1-3-3","a-1-3-4","a-1-4-3","a-1-4-4","a-2-3-3","a-2-3-4","a-2-4-3","a-2-4-4","b-1-3-3","","","","","","",""},
			{"a-3-1-1","a-3-1-2","a-3-2-1","a-3-2-2","a-4-1-1","a-4-1-2","a-4-2-1","a-4-2-2","b-3-1-1","","","","","","",""},
			{"a-3-1-3","a-3-1-4","a-3-2-3","a-3-2-4","a-4-1-3","a-4-1-4","a-4-2-3","a-4-2-4","b-3-1-3","","","","","","",""},
			{"a-3-3-1","a-3-3-2","a-3-4-1","a-3-4-2","a-4-3-1","a-4-3-2","a-4-4-1","a-4-4-2","b-3-3-1","","","","","","",""},
			{"a-3-3-3","a-3-3-4","a-3-4-3","a-3-4-4","a-4-3-3","a-4-3-4","a-4-4-3","a-4-4-4","","","","","","","",""},
			{"c-1-1-1","c-1-1-2","c-1-2-1","c-1-2-2","c-2-1-1","c-2-1-2","c-2-2-1","","","","","","","","",""},
			{"c-1-1-3","c-1-1-4","c-1-2-3","c-1-2-4","c-2-1-3","c-2-1-4","c-2-2-3","","","","","","","","",""},
			{"c-1-3-1","c-1-3-2","c-1-4-1","c-1-4-2","c-2-3-1","c-2-3-2","c-2-4-1","","","","","","","","",""},
			{"c-1-3-3","c-1-3-4","c-1-4-3","c-1-4-4","c-2-3-3","c-2-3-4","c-2-4-3","","","","","","","","",""},
			{"c-3-1-1","c-3-1-2","c-3-2-1","c-3-2-2","c-4-1-1","c-4-1-2","","","","","","","","","",""},
			{"","","c-3-2-3","c-3-2-4","c-4-1-3","c-4-1-4","","","","","","","","","",""},
			{"","","","","c-4-3-1","c-4-3-2","","","","","","","","","",""},
			{"","","","","","","","","","","","","","","",""}
	};

	private static final int NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE = 16;
	private int xIndex, yIndex;
	@Getter
	private String left, right, top, bottom;

	public Adjacency(String name)
	{
		if (name.startsWith("M-34-101-A-")) {
			findXAndYIndexes(name, lasMapA);
			top = findTopOrBottom(name, lasMapA, 1);
			bottom = findTopOrBottom(name, lasMapA, -1);
		}
		else if (name.startsWith("M-34-100-B-")) {
			findXAndYIndexes(name, lasMapB);
			top = findTopOrBottom(name, lasMapB, 1);
			bottom = findTopOrBottom(name, lasMapB, -1);
		}

		left = findLeft(name);
		right = findRight(name);
	}

	private void findXAndYIndexes(String name, String[][] map) {
		for (int i = 0; i < NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE; i++) {
			for (int j = 0; j < NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE; j++) {
				if (!map[i][j].isEmpty() && name.contains(map[i][j])) {
					xIndex = j;
					yIndex = i;
					return;
				}
			}
		}
	}

	private String findLeft(String name) {
		if (name.startsWith("M-34-101-A-")) {
			if (xIndex > 0 && !lasMapA[yIndex][xIndex-1].isEmpty()) {
				return "M-34-101-A-" + lasMapA[yIndex][xIndex-1];
			}
			else if (xIndex == 0 && !lasMapB[yIndex][NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE-1].isEmpty()) {
				return "M-34-100-B-" + lasMapB[yIndex][NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE-1];
			}
			else {
				return "";
			}
		}
		else if (name.startsWith("M-34-100-B-")) {
			if (xIndex > 0 && !lasMapB[yIndex][xIndex-1].isEmpty()) {
				return "M-34-100-B-" + lasMapB[yIndex][xIndex-1];
			}
			else {
				return "";
			}
		}
		return "";
	}

	private String findRight(String name) {
		if (name.startsWith("M-34-101-A-")) {
			if (xIndex < NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE-1 && !lasMapA[yIndex][xIndex+1].isEmpty()) {
				return "M-34-101-A-" + lasMapA[yIndex][xIndex+1];
			}
			else {
				return "";
			}
		}
		else if (name.contains("M-34-100-B-")) {
			if (xIndex < NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE-1 && !lasMapB[yIndex][xIndex+1].isEmpty()) {
				return "M-34-100-B-" + lasMapB[yIndex][xIndex+1];
			}
			else if (xIndex == NUMBER_OF_SUBREGIONS_IN_SCAN_SIDE-1 && !lasMapA[yIndex][0].isEmpty()) {
				return "M-34-101-A-" + lasMapA[yIndex][0];
			}
			else {
				return "";
			}
		}
		return "";
	}

	private String findTopOrBottom(String name, String[][] map, int direction) {
		String prefix = map == lasMapA ? "M-34-101-A-" : "M-34-100-B-";
		try {
			if (!map[yIndex-direction][xIndex].isEmpty()) {
				return prefix + map[yIndex-direction][xIndex];
			}
			else {
				return "";
			}
		}
		catch (IndexOutOfBoundsException e) {
			return "";
		}
	}
}