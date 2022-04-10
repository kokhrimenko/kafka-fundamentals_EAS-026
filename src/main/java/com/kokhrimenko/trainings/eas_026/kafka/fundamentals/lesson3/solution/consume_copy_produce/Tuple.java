package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3.solution.consume_copy_produce;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tuple {
	private final String key;
	private final Integer value;

	public static Tuple of(String key, Integer value) {
		return new Tuple(key, value);
	}
}
