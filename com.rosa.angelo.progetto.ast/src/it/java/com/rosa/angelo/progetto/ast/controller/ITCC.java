package com.rosa.angelo.progetto.ast.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

// TODO: this is just for let failsafe to generate a report. 
// Will be removed in real IT implementations
public class ITCC {
	private CC a;
	
	@Before
	public void setup() {
		a = new CC();
	}

	@Test
	public void test() {
		assertThat(a.testThis()).isEqualTo("test");
	}
}
