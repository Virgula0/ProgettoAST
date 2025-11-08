package com.rosa.angelo.progetto.ast.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestGenericRepositoryException {
	@Test
	public void testExceptionThrower() {
		String msg = "test";
		Exception test = new Exception(msg);
		GenericRepositoryException ex = new GenericRepositoryException(test.getMessage());
		assertThat(ex.getMessage()).isEqualTo(msg);
	}

}
