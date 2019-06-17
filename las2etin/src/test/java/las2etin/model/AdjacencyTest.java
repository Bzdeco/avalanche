package las2etin.model;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AdjacencyTest {
	@Test
	public void leftAdjacencyTest() {
		List<String> names = Arrays.asList("M-34-100-B-b-3-3-1", "M-34-100-B-a-1-4-4", "M-34-100-B-c-3-1-1", "M-34-101-A-c-1-1-1",
				"M-34-101-A-b-2-1-1", "M-34-101-A-c-4-3-1");

		Adjacency adjacency;
		List<String> actuals = new ArrayList<>();
		for (String name : names) {
			adjacency = new Adjacency(name);
			actuals.add(adjacency.getLeft());
		}

		List<String> expecteds = Arrays.asList("M-34-100-B-a-4-4-2", "M-34-100-B-a-1-4-3", "", "M-34-100-B-d-2-2-2", "M-34-101-A-b-1-2-2", "");

		assertEquals(expecteds, actuals);
	}

	@Test
	public void rightAdjacencyTest() {
		List<String> names = Arrays.asList("M-34-100-B-a-4-4-2", "M-34-100-B-d-3-1-1", "M-34-100-B-b-2-4-2", "M-34-101-A-c-2-2-3",
				"M-34-101-A-a-4-2-4", "M-34-101-A-c-4-3-2");

		Adjacency adjacency;
		List<String> actuals = new ArrayList<>();
		for (String name : names) {
			adjacency = new Adjacency(name);
			actuals.add(adjacency.getRight());
		}

		List<String> expecteds = Arrays.asList("M-34-100-B-b-3-3-1", "", "M-34-101-A-a-1-3-1", "", "M-34-101-A-b-3-1-3", "");

		assertEquals(expecteds, actuals);
	}

	@Test
	public void topAdjacencyTest() {
		List<String> names = Arrays.asList("M-34-100-B-a-4-2-1", "M-34-100-B-c-1-1-2", "M-34-100-B-b-2-1-2", "M-34-101-A-b-1-4-2",
				"M-34-101-A-c-1-2-1", "M-34-101-A-c-4-3-2");

		Adjacency adjacency;
		List<String> actuals = new ArrayList<>();
		for (String name : names) {
			adjacency = new Adjacency(name);
			actuals.add(adjacency.getTop());
		}

		List<String> expecteds = Arrays.asList("M-34-100-B-a-2-4-3", "", "", "M-34-101-A-b-1-2-4", "M-34-101-A-a-3-4-3", "M-34-101-A-c-4-1-4");

		assertEquals(expecteds, actuals);
	}

	@Test
	public void bottomAdjacencyTest() {
		List<String> names = Arrays.asList("M-34-100-B-b-3-4-4", "M-34-100-B-c-3-1-1", "M-34-100-B-d-2-1-3", "M-34-101-A-c-4-3-2",
				"M-34-101-A-b-1-4-1", "M-34-101-A-a-4-3-4");

		Adjacency adjacency;
		List<String> actuals = new ArrayList<>();
		for (String name : names) {
			adjacency = new Adjacency(name);
			actuals.add(adjacency.getBottom());
		}

		List<String> expecteds = Arrays.asList("M-34-100-B-d-1-2-2", "", "", "", "", "M-34-101-A-c-2-1-2");

		assertEquals(expecteds, actuals);
	}
}