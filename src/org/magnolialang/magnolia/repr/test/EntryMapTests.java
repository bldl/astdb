package org.magnolialang.magnolia.repr.test;

import org.junit.Before;
import org.junit.Test;
import org.magnolialang.magnolia.repr.Entry;
import org.magnolialang.magnolia.repr.EntryMap;
import org.magnolialang.magnolia.repr.Key;
import org.magnolialang.magnolia.repr.Node;

public class EntryMapTests {

	EntryMap simple, simpleM, complex, complexM;
	Entry<Integer>[] es;
	Entry<String> se;
	Node sm, cm;
	int MAX = 10;

	@SuppressWarnings("serial")
	protected static final Key<Integer> ikey = new Key<Integer>() {
	};
	@SuppressWarnings("serial")
	protected static final Key<String> skey = new Key<String>() {
	};


	@Before
	public void setUp() throws Exception {

		sm = new Node("simpleMaster");
		cm = new Node("complexMaster");
		es = setupEntries(MAX);
		se = new Entry<String>(skey, "s");

		simple = new EntryMap();
		simpleM = new EntryMap(sm);
		complex = new EntryMap(es[0]);
		complexM = new EntryMap(cm, es[5], es[6]);
	}


	private Entry<Integer>[] setupEntries(int amount) {
		Entry<Integer>[] es = new Entry[amount];
		for(int i = 0; i < amount; i++) {
			es[i] = new Entry<Integer>(ikey, i);
		}
		return es;
	}


	@Test
	public void testGetEntryHasDataCorresponds() {
		for(Key key : complex.getKeys()) {
			assert (complex.hasData(key)) : "every key should correspond to a value";
			assert (complex.getEntry(key) != null) : "getEntry should never return null if the entry exists";
		}

		assert (!simple.hasData(ikey));
		assert (simple.getEntry(ikey) == null);
		simple.addEntry(es[4]);
		assert (simple.hasData(ikey));
		assert (simple.getEntry(ikey).equals(es[4]));

	}


	@Test
	public void testGetKeys() {
		assert (simple.getKeys().size() == 0);
		assert (complex.getKeys().size() == 1);
		assert (simpleM.getKeys().size() == 0);
		assert (complexM.getKeys().size() == 1); // should be 1 since the different elements had the same key

		complexM.addEntry(se);
		assert (complexM.getKeys().size() == 2);

		simple.addEntry(es[3]);
		assert (simple.getKeys().size() == 1) : "amount of entries should increase after addEntry";

	}


	@Test
	public void testGetSetMasterNode() {
		assert (simple.getMasterNode() == null) : "master should be null if not given";
		assert (complex.getMasterNode() == null) : "master should be null if not given";
		assert (simpleM.getMasterNode().equals(sm)) : "master of simpleM should be sm";
		assert (complexM.getMasterNode().equals(cm)) : "master of complexM should be cm";

		Node newMaster = new Node("newMaster");
		simple.setMasterNode(newMaster);
		assert (simple.getMasterNode().equals(newMaster)) : "MasterNode not updated correctly in setMaster";
	}

}
