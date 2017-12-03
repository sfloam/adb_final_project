//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//import org.junit.jupiter.api.Test;
//
//class Test_LockObj {
//
//	@Test
//	void test_getVariableID() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		assertEquals(1, lock.getVariableID());
//	}
//
//	@Test
//	void test_setVariableID() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		lock.setVariableID(2);
//		assertNotEquals(1, lock.getVariableID());
//		assertEquals(2, lock.getVariableID());
//	}
//
//	@Test
//	void test_getTransactionID() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		assertEquals("T1", lock.getTransactionID());
//	}
//
//	@Test
//	void test_setTransactionID() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		lock.setTransactionID("T2");
//		assertNotEquals("T1", lock.getTransactionID());
//		assertEquals("T2", lock.getTransactionID());
//	}
//
//	@Test
//	void test_getLockType() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		assertEquals("W", lock.getLockType());
//	}
//
//	@Test
//	void test_setLockType() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		lock.setLockType("R");
//		assertNotEquals("W", lock.getLockType());
//		assertEquals("R", lock.getLockType());
//	}
//
//	@Test
//	void test_toString() {
//		LockObj lock = new LockObj("W", "T1", 1);
//		assertEquals("1 T1 W", lock.getVariableID() + " " + lock.getTransactionID() + " " + lock.getLockType());
//	}
//
//}
