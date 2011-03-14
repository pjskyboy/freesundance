package com.freesundance.http;

import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestCaseThreeUsage extends TestCase {

	private static final Logger log = Logger.getLogger(TestCaseThreeUsage.class);
	@Test
	public void testDoItValidUser() throws Exception {
		ThreeUsage unit = ThreeUsage.doIt("07413770734", "skyboy125");
		assertNotNull(unit);
		Map<String, ThreeAllowanceItem> map = unit.getAllowance();
		assertNotNull(map);
		assertNotNull(map.get("Voice minutes"));
		assertNotNull(map.get("Internet (MBs)"));
		assertNotNull(map.get("Texts"));
		assertNotNull(map.get("datetime"));
	}

	@Test
	public void testDoItInValidUser() throws Exception {
		try {
			ThreeUsage.doIt("blah", "blah");
			fail("how did we get past the login form?");
		} catch (ThreeUsageCredentialException expected) {
			assertEquals(ThreeUsage.THREE_INVALID_USERNAME_OR_PASSWORD_MESSAGE,
					expected.getMessage());
		} catch (Throwable unexpected) {
			log.error(unexpected);
			fail("something bad has happened");
		}
	}

}
